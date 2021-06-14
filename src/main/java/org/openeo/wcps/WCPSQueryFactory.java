package org.openeo.wcps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.Math;

import org.openeo.wcps.domain.Aggregate;
import org.openeo.wcps.domain.Filter;
import org.openeo.spring.model.Collection;
import org.openeo.spring.model.Dimension;
import org.openeo.spring.model.DimensionSpatial;
import org.openeo.spring.model.DimensionTemporal;
import org.openeo.spring.model.TemporalDimension;
import org.openeo.wcps.WCPSReduceFunc;
import org.openeo.spring.components.CollectionMap;
import org.openeo.wcps.WCPSApplyFunc;

public class WCPSQueryFactory {

	private StringBuilder wcpsStringBuilder;
	private Vector<Collection> collectionIDs;
	private Vector<Filter> filters;
	private Vector<Filter> filtersPolygon;
	private Vector<Aggregate> aggregates;
	private String outputFormat = "json";
	private JSONObject processGraph;
	private boolean withUDF = false;
	private String openEOEndpoint;
	private String wcpsEndpoint;
	private CollectionMap collectionMap;
	Logger log = LogManager.getLogger();

	/**
	 * Creates WCPS query from openEO process Graph
	 * 
	 * @param openEOGraph
	 */
	public WCPSQueryFactory(JSONObject openEOGraph, String openEOEndpoint, String wcpsEndpoint, CollectionMap collectionMap) {
		log.debug("openEO endpoint: " + openEOEndpoint);
		log.debug("wcps endpoint: " + wcpsEndpoint);
		collectionIDs = new Vector<Collection>();
		aggregates = new Vector<Aggregate>();
		filters = new Vector<Filter>();
		filtersPolygon = new Vector<Filter>();
		wcpsStringBuilder = new StringBuilder("");
		this.processGraph = openEOGraph;
		this.openEOEndpoint = openEOEndpoint;
		this.wcpsEndpoint = wcpsEndpoint;
		this.collectionMap = collectionMap;
		this.build();
	}

	public String getOutputFormat() {
		return outputFormat;
	}
	
	public void setOutputFormat(String outputFormat) {
		this.outputFormat =  outputFormat;
	}
	
	// Does Process Graph have a UDF in it ?
	public boolean isWithUDF() {
		return withUDF;
	}

	private StringBuilder basicWCPSStringBuilder(String varPayLoad) {
		StringBuilder basicWCPS;
		basicWCPS = new StringBuilder("for ");
		
		// Update WCPS when there are multiple cubes to be accessed
		log.debug("collectionIDs size: " +  collectionIDs.size());
		for (int c = 1; c <= collectionIDs.size(); c++) {
			String collectionID = processGraph.getJSONObject(collectionIDs.get(c - 1).getId()).getJSONObject("arguments").getString("id");
			basicWCPS.append("$cube" + collectionID + collectionIDs.get(c - 1).getId() + " in (" + collectionID + ")");
			if (c < collectionIDs.size()) {
				basicWCPS.append(", ");
			}
		}
		// varPayLoad is for when there is 'let' function to be used in WCPS
//		basicWCPS.append(" let " + varPayLoad + " $mock := 1 return encode ( ");
		basicWCPS.append(varPayLoad + " return encode ( ");
		return basicWCPS;
	}
	
	private void buildLoadCollection(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		String collectionID = currentProcessArguments.getString("id");
		Collection collection = collectionMap.get(collectionID);

		if (collection.getExtent().getTemporal().getInterval().get(0) == null) {
			collDims2D = true;
		}
		
		// Create WCPS query for Process
		wcpsPayLoad.append(createFilteredCollectionString("$cube"+collectionID+nodeKeyOfCurrentProcess, collectionID));
		log.debug("Initial PayLoad WCPS is: ");
		log.debug(wcpsPayLoad.toString());
		// Add WCPS query for Process to the main WCPS query
		wcpsStringBuilder.append(wcpsPayLoad.toString());
		// Store WCPS query for Process into the storedPayLoads
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsPayLoad.toString());
		wcpsPayLoad = new StringBuilder("");
		log.debug("Load Collection PayLoad is : ");
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.info("Collection Dims : " + collDims2D);
		//loadedCubes = loadedCubes+1;
	}
	
	private void buildLt(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
//		StringBuilder wcpsApplypayLoad = new StringBuilder("");
//		StringBuilder wcpsStringBuilderApply = basicWCPSStringBuilder(varPayLoad.toString());
//		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
//		String x = null;
//		String y = null;
//		
//		if (processArguments.get("x") instanceof JSONObject) {
//			for (String fromType : processArguments.getJSONObject("x").keySet()) {
//				if (fromType.equals("from_parameter") && processArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
//					x = wcpsPayLoad.toString();
//				}
//				else if (fromType.equals("from_node")) {
//					String dataNodeX = processArguments.getJSONObject("x").getString("from_node");
//					String collectionNodeKey = getFilterCollectionNode(dataNodeX);
//					//collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
//					//collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("x").getString("from_node"));							
//					String ltPayLoadX = storedPayLoads.getString(dataNodeX);
//					x = ltPayLoadX;
//				}						
//			}
//		}
//		else {
//			x = String.valueOf(processArguments.getDouble("x"));
//		}
//		if (processArguments.get("y") instanceof JSONObject) {
//			for (String fromType : processArguments.getJSONObject("y").keySet()) {
//				if (fromType.equals("from_parameter") && processArguments.getJSONObject("y").getString("from_parameter").equals("y")) {
//					y = wcpsPayLoad.toString();
//				}
//				else if (fromType.equals("from_node")) {
//					String dataNodeY = processArguments.getJSONObject("y").getString("from_node");
//					String collectionNodeKey = getFilterCollectionNode(dataNodeY);
//					//collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
//					//collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("y").getString("from_node"));
//					String ltPayLoadY = storedPayLoads.getString(dataNodeY);
//					y = ltPayLoadY;
//				}						
//			}
//		}
//		else {
//			y = String.valueOf(processArguments.getDouble("y"));
//		}
//		
//		wcpsApplypayLoad.append("(" + createLessThanWCPSString(x, y) + ")");
//		wcpsPayLoad=wcpsApplypayLoad;
//		wcpsStringBuilder = wcpsStringBuilderApply.append(wcpsApplypayLoad.toString());
//		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsApplypayLoad.toString());
//		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
//		log.debug("Less Than Process PayLoad is : ");
//		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildGt(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
//		StringBuilder wcpsApplypayLoad = new StringBuilder("");
//		StringBuilder wcpsStringBuilderApply = basicWCPSStringBuilder(varPayLoad.toString());				
//		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
//		String x = null;
//		String y = null;
//		
//		if (processArguments.get("x") instanceof JSONObject) {
//			for (String fromType : processArguments.getJSONObject("x").keySet()) {
//				if (fromType.equals("from_parameter") && processArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
//					x = wcpsPayLoad.toString();
//				}
//				else if (fromType.equals("from_node")) {
//					String dataNodeX = processArguments.getJSONObject("x").getString("from_node");
//					//String collectionNodeKey = getFilterCollectionNode(dataNodeX);
//					//collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
//					//collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("x").getString("from_node"));							
//					String ltPayLoadX = storedPayLoads.getString(dataNodeX);
//					x = ltPayLoadX;
//				}						
//			}
//		}
//		else {
//			x = String.valueOf(processArguments.getDouble("x"));
//		}
//		if (processArguments.get("y") instanceof JSONObject) {
//			for (String fromType : processArguments.getJSONObject("y").keySet()) {
//				if (fromType.equals("from_parameter") && processArguments.getJSONObject("y").getString("from_parameter").equals("y")) {
//					y = wcpsPayLoad.toString();
//				}
//				else if (fromType.equals("from_node")) {
//					String dataNodeY = processArguments.getJSONObject("y").getString("from_node");
//					//String collectionNodeKey = getFilterCollectionNode(dataNodeY);
//					//collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
//					//collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("y").getString("from_node"));
//					String ltPayLoadY = storedPayLoads.getString(dataNodeY);
//					y = ltPayLoadY;
//				}						
//			}
//		}
//		else {
//			y = String.valueOf(processArguments.getDouble("y"));
//		}
//		
//		wcpsApplypayLoad.append("(" + createGreatThanWCPSString(x, y) + ")");
//		wcpsPayLoad=wcpsApplypayLoad;
//		wcpsStringBuilder = wcpsStringBuilderApply.append(wcpsApplypayLoad.toString());
//		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsApplypayLoad.toString());
//		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
//		log.debug("Less Than Process PayLoad is : ");
//		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildMaskColored(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsMaskColorpayLoad = new StringBuilder("switch case ");
		StringBuilder wcpsStringBuilderMaskColorPayload = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		wcpsMaskColorpayLoad.append(processArguments.getString("lowerThreshold") + " < (" + payLoad + ") > " + processArguments.getString("upperThreshold") + " return {red:" + processArguments.get("red") + "; green:" + processArguments.get("green") + "; blue:" + processArguments.get("blue") + "} default return {red: 230; green: 240; blue: 255}");
		wcpsPayLoad=wcpsMaskColorpayLoad;
		wcpsStringBuilder=wcpsStringBuilderMaskColorPayload.append(wcpsMaskColorpayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMaskColorpayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Mask Colored Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildMergeCubes(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsMergepayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderMerge = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad1 = null;
		String payLoad2 = null;
		String cube1 = null;
		String cube2 = null;
		String endMergeNode = null;
		String temporalStartCube1 = null;
		String temporalEndCube1 = null;
		String temporalStartCube2 = null;
		String temporalEndCube2 = null;
		String nodeKeyofCube1 = null;				
		JSONArray endMergeNodeAsArray = new JSONArray();
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String overlapResolver = null;
		JSONObject mergeProcess = null;
		try {
		mergeProcess = processArguments.getJSONObject("overlap_resolver").getJSONObject("process_graph");
		for (String mergeProcessKey : mergeProcess.keySet()) {
			JSONObject mergeProcessID =  mergeProcess.getJSONObject(mergeProcessKey);
			for (String applierField : mergeProcessID.keySet()) {
				if (applierField.equals("result")) {
					Boolean resultFlag = mergeProcessID.getBoolean("result");
					if (resultFlag) {
						endMergeNode = mergeProcessKey;
						endMergeNodeAsArray.put(endMergeNode);
						log.debug("End Merge Process is : " + mergeProcess.getJSONObject(endMergeNode).getString("process_id"));
					}
				}
			}
		}
		
		String xFromParameter = mergeProcess.getJSONObject(endMergeNode).getJSONObject("arguments").getJSONObject("x").getString("from_parameter");
		String yFromParameter = mergeProcess.getJSONObject(endMergeNode).getJSONObject("arguments").getJSONObject("y").getString("from_parameter");
		if (processArguments.get("cube1") instanceof JSONObject && xFromParameter.contentEquals("x")) {
			for (String fromType : processArguments.getJSONObject("cube1").keySet()) {
				if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("cube1").getString("from_node");
					nodeKeyofCube1 = processArguments.getJSONObject("cube1").getString("from_node");
					temporalStartCube1 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(0);
					temporalEndCube1 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(1);
					cube1 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getString("id");
					payLoad1 = storedPayLoads.getString(dataNode);
					//payLoadCRS = createFilteredCollectionString("$cube"+cube1+getFilterCollectionNode(dataNode), cube1);
					log.debug(payLoad1);
				}
			}
		}
		
		if (processArguments.get("cube2") instanceof JSONObject && yFromParameter.contentEquals("y")) {
			for (String fromType : processArguments.getJSONObject("cube2").keySet()) {
				if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("cube2").getString("from_node");
					cube2 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getString("id");
					temporalStartCube2 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(0);
					temporalEndCube2 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(1);
					payLoad2 = storedPayLoads.getString(dataNode);
					log.debug(payLoad2);
				}
			}
		}				
		overlapResolver =  mergeProcess.getJSONObject(endMergeNode).getString("process_id");
		}
		catch (Exception e) {					
		}
		
		int noOfDimsCube1 = 0;
		int noOfDimsCube2 = 0;
		Collection coll1 = collectionMap.get(cube1);
		Collection coll2 = collectionMap.get(cube2);
		
		noOfDimsCube1 = coll1.getCubeColonDimensions().size();
		noOfDimsCube2 = coll2.getCubeColonDimensions().size();
		
		boolean dimsXY = false;
		boolean dimsEN = false;
		JSONObject dimAxisName = null;
		if(coll1.getCubeColonDimensions().containsKey("E") || coll1.getCubeColonDimensions().containsKey("N")) {
			dimsEN = true;
		}else if(coll1.getCubeColonDimensions().containsKey("X") || coll1.getCubeColonDimensions().containsKey("Y")) {
			dimsXY = true;
		}
		log.debug(temporalStartCube1);
		log.debug(temporalEndCube1);
		log.debug(temporalStartCube2);
		log.debug(temporalEndCube2);
		if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && !temporalStartCube2.equals(temporalEndCube2) && !payLoad2.contains("condense") && !payLoad2.contains("coverage")) {
			log.debug("Time Series Cubes");
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
			
			if (overlapResolver.equals( "add")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"+"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "subtract")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"-"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "multiply")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"*"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "divide")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"/"+"("+payLoad2+"))");
			}
			wcpsPayLoad=wcpsMergepayLoad;
			wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process PayLoad is : " + wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));					
		}
		
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && !temporalStartCube2.equals(temporalEndCube2) && !payLoad2.contains("condense") && payLoad2.contains("coverage")) {
			log.debug("Time Series Cubes");
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
			
			if (overlapResolver.equals( "add")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"+"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "subtract")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"-"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "multiply")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"*"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "divide")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"/"+"("+payLoad2+"))");
			}
			wcpsPayLoad=wcpsMergepayLoad;
			wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process PayLoad is : " + wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));					
		}
		
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && !temporalStartCube2.equals(temporalEndCube2) && payLoad2.contains("condense") && !payLoad2.contains("coverage") && payLoad1.contains("coverage")) {
			log.debug("Cubes are of same dimension after condensing too");
			String timeImageCrsDomain = Pattern.compile(" X"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" Y"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" E"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" N"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(",").matcher(timeImageCrsDomain).replaceAll("");
			String XImageCrsDomain = Pattern.compile(" DATE"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			XImageCrsDomain = Pattern.compile(" Y"+"\\(.*?\\)").matcher(XImageCrsDomain).replaceAll("");
			XImageCrsDomain = Pattern.compile(" N"+"\\(.*?\\)").matcher(XImageCrsDomain).replaceAll("");
			XImageCrsDomain = Pattern.compile(",").matcher(XImageCrsDomain).replaceAll("");
			String YImageCrsDomain = Pattern.compile(" DATE"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			YImageCrsDomain = Pattern.compile(" X"+"\\(.*?\\)").matcher(YImageCrsDomain).replaceAll("");
			YImageCrsDomain = Pattern.compile(" E"+"\\(.*?\\)").matcher(YImageCrsDomain).replaceAll("");
			YImageCrsDomain = Pattern.compile(",").matcher(YImageCrsDomain).replaceAll("");					
			
			String payLoad1Merge = payLoad1.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\sDATE"+"\\(.*?\\)", " DATE\\(\\$T" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyOfCurrentProcess + "\\)");
			    String payLoad2Merge = payLoad2.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyOfCurrentProcess + "\\)");
							
			log.debug(timeImageCrsDomain);
			log.debug(XImageCrsDomain);
			log.debug(YImageCrsDomain);
			log.debug(payLoad1Merge);
			log.debug(payLoad2Merge);
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
			
			if (overlapResolver.equals( "add")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "+" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "+" + "("+payLoad2Merge+"))");
				}						
			}
			if (overlapResolver.equals( "subtract")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "-" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "-" + "("+payLoad2Merge+"))");
				}
			}
			if (overlapResolver.equals( "multiply")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "*" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "*" + "("+payLoad2Merge+"))");
				}
			}
			if (overlapResolver.equals( "divide")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "/" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "/" + "("+payLoad2Merge+"))");
				}
			}
			wcpsPayLoad=wcpsMergepayLoad;
			wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process PayLoad is : " + wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));					
		}
		
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && !temporalStartCube2.equals(temporalEndCube2) && payLoad2.contains("condense") && !payLoad2.contains("coverage") && !payLoad1.contains("coverage")) {
			log.debug("Cubes are of same dimension after condensing too");
			String timeImageCrsDomain = Pattern.compile(" X"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" Y"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" E"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" N"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(",").matcher(timeImageCrsDomain).replaceAll("");
			String XImageCrsDomain = Pattern.compile(" DATE"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			XImageCrsDomain = Pattern.compile(" Y"+"\\(.*?\\)").matcher(XImageCrsDomain).replaceAll("");
			XImageCrsDomain = Pattern.compile(" N"+"\\(.*?\\)").matcher(XImageCrsDomain).replaceAll("");
			XImageCrsDomain = Pattern.compile(",").matcher(XImageCrsDomain).replaceAll("");
			String YImageCrsDomain = Pattern.compile(" DATE"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			YImageCrsDomain = Pattern.compile(" X"+"\\(.*?\\)").matcher(YImageCrsDomain).replaceAll("");
			YImageCrsDomain = Pattern.compile(" E"+"\\(.*?\\)").matcher(YImageCrsDomain).replaceAll("");
			YImageCrsDomain = Pattern.compile(",").matcher(YImageCrsDomain).replaceAll("");
			
			String payLoad1Merge = payLoad1.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\sDATE"+"\\(.*?\\)", " DATE\\(\\$T" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyOfCurrentProcess + "\\)");
			String payLoad2Merge = payLoad2.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyOfCurrentProcess + "\\)");
			
			log.debug(timeImageCrsDomain);
			log.debug(XImageCrsDomain);
			log.debug(YImageCrsDomain);
			log.debug(payLoad1Merge);
			log.debug(payLoad2Merge);
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
													
			if (overlapResolver.equals("add")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + " + " + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + " + " + "("+payLoad2Merge+"))");
				}						
				log.debug(overlapResolver);
			}
			if (overlapResolver.equals("subtract")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "-" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "-" + "("+payLoad2Merge+"))");
				}
			}
			if (overlapResolver.equals("multiply")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "*" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "*" + "("+payLoad2Merge+"))");
				}
			}
			if (overlapResolver.equals("divide")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "/" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "/" + "("+payLoad2Merge+"))");
				}
			}
			log.debug(wcpsMergepayLoad.toString());
			wcpsPayLoad=wcpsMergepayLoad;
			wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process PayLoad is : " + wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));					
		}
		
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && !temporalStartCube2.equals(temporalEndCube2) && payLoad2.contains("condense") && payLoad2.contains("coverage") && payLoad1.contains("coverage")) {
			log.debug("Cubes are of same dimension after condensing too");
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
			
			if (overlapResolver.equals( "add")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"+"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "subtract")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"-"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "multiply")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"*"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "divide")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"/"+"("+payLoad2+"))");
			}
			wcpsPayLoad=wcpsMergepayLoad;
			wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process PayLoad is : " + wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));					
		}			
		
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && !temporalStartCube2.equals(temporalEndCube2) && payLoad2.contains("condense") && payLoad2.contains("coverage") && !payLoad1.contains("coverage")) {
			log.debug("Cubes are of same dimension after condensing too");
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
			
			if (overlapResolver.equals( "add")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"+"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "subtract")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"-"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "multiply")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"*"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "divide")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"/"+"("+payLoad2+"))");
			}
			wcpsPayLoad=wcpsMergepayLoad;
			wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process PayLoad is : " + wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));					
		}
		
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && temporalStartCube2.equals(temporalEndCube2) && !payLoad2.contains("condense") && !payLoad1.contains("coverage")) {
			String timeImageCrsDomain = Pattern.compile(" X"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" Y"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" E"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" N"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(",").matcher(timeImageCrsDomain).replaceAll("");
			String XImageCrsDomain = Pattern.compile(" DATE"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			XImageCrsDomain = Pattern.compile(" Y"+"\\(.*?\\)").matcher(XImageCrsDomain).replaceAll("");
			XImageCrsDomain = Pattern.compile(" N"+"\\(.*?\\)").matcher(XImageCrsDomain).replaceAll("");
			XImageCrsDomain = Pattern.compile(",").matcher(XImageCrsDomain).replaceAll("");
			String YImageCrsDomain = Pattern.compile(" DATE"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			YImageCrsDomain = Pattern.compile(" X"+"\\(.*?\\)").matcher(YImageCrsDomain).replaceAll("");
			YImageCrsDomain = Pattern.compile(" E"+"\\(.*?\\)").matcher(YImageCrsDomain).replaceAll("");
			YImageCrsDomain = Pattern.compile(",").matcher(YImageCrsDomain).replaceAll("");					
			
			String payLoad1Merge = payLoad1.replaceAll("\\sDATE"+"\\(.*?\\)", " DATE\\(\\$T" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyOfCurrentProcess + "\\)");
			String payLoad2Merge = payLoad2.replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyOfCurrentProcess + "\\)");
			log.debug(timeImageCrsDomain);
			log.debug(XImageCrsDomain);
			log.debug(YImageCrsDomain);
			log.debug(payLoad1Merge);
			log.debug(payLoad2Merge);
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
			
			if (overlapResolver.equals( "add")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "+" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "+" + "("+payLoad2Merge+"))");
				}						
			}
			if (overlapResolver.equals( "subtract")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "-" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "-" + "("+payLoad2Merge+"))");
				}
			}
			if (overlapResolver.equals( "multiply")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "*" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "*" + "("+payLoad2Merge+"))");
				}
			}
			if (overlapResolver.equals( "divide")) {
				if (dimsXY) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + "/" + "("+payLoad2Merge+"))");
				}
				else if(dimsEN) {
					wcpsMergepayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + "/" + "("+payLoad2Merge+"))");
				}
			}
			wcpsPayLoad=wcpsMergepayLoad;
			wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process PayLoad is : " + wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		}
		
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && temporalStartCube2.equals(temporalEndCube2) && !payLoad2.contains("condense") && payLoad1.contains("coverage") && payLoad1.contains("condense")) {
			String payLoad2Merge = payLoad2.replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyofCube1 + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyofCube1 + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyofCube1 + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyofCube1 + "\\)");
			
			log.debug(payLoad2Merge);
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
			
			if (overlapResolver.equals( "add")) {
				if (dimsXY) {
					wcpsMergepayLoad.append("" + payLoad1 + "+("+payLoad2Merge+")");
					}
				else if(dimsEN) {
					wcpsMergepayLoad.append("" + payLoad1 + "+("+payLoad2Merge+")");
					}						
			}
			if (overlapResolver.equals( "subtract")) {
				if (dimsXY) {
					wcpsMergepayLoad.append("" + payLoad1 + "-("+payLoad2Merge+")");
					}
				else if(dimsEN) {
					wcpsMergepayLoad.append("" + payLoad1 + "-("+payLoad2Merge+")");
					}
			}
			if (overlapResolver.equals( "multiply")) {
				if (dimsXY) {
					wcpsMergepayLoad.append("" + payLoad1 + "*("+payLoad2Merge+")");
					}
				else if(dimsEN) {
					wcpsMergepayLoad.append("" + payLoad1 + "*("+payLoad2Merge+")");
					}
			}
			if (overlapResolver.equals( "divide")) {
				if (dimsXY) {
					wcpsMergepayLoad.append("" + payLoad1 + "/("+payLoad2Merge+")");
					}
				else if(dimsEN) {
					wcpsMergepayLoad.append("" + payLoad1 + "/("+payLoad2Merge+")");
					}
			}
			wcpsPayLoad=wcpsMergepayLoad;
			wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process PayLoad is : " + wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		}
		
		else if (noOfDimsCube1==noOfDimsCube2 && temporalStartCube1.equals(temporalEndCube1) && temporalStartCube2.equals(temporalEndCube2)) {
			log.debug("Time Series Cubes");
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
			
			if (overlapResolver.equals( "add")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"+"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "subtract")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"-"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "multiply")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"*"+"("+payLoad2+"))");
			}
			if (overlapResolver.equals( "divide")) {
				wcpsMergepayLoad.append("(("+payLoad1+")"+"/"+"("+payLoad2+"))");
			}
			wcpsPayLoad=wcpsMergepayLoad;
			wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process PayLoad is : " + wcpsMergepayLoad.toString());
			log.debug("Merge Cubes Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		}
	}
	
	private void builMask(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsMaskpayLoad = new StringBuilder("");
		double replacement = 0;
		JSONObject processArguments = processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		StringBuilder wcpsStringBuilderMaskThresPayload = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad1 = null;
		String payLoad2 = null;
		String cube1 = null;
		String cube2 = null;
		String temporalStartCube1 = null;
		String temporalEndCube1 = null;
		String temporalStartCube2 = null;
		String temporalEndCube2 = null;
		String nodeKeyofCube1 = null;
		
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
					payLoad1 = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");							
					nodeKeyofCube1 = processArguments.getJSONObject("data").getString("from_node");
					temporalStartCube1 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(0);
					temporalEndCube1 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(1);
					cube1 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getString("id");
					payLoad1 = storedPayLoads.getString(dataNode);
					//payLoadCRS = createFilteredCollectionString("$cube"+cube1+getFilterCollectionNode(dataNode), cube1);
					log.debug(payLoad1);
				}
			}
		}
		if (processArguments.get("mask") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("mask").keySet()) {
				if (fromType.equals("from_argument") && processArguments.getJSONObject("mask").getString("from_argument").equals("data")) {
					payLoad2 = wcpsPayLoad.toString();							
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("mask").getString("from_node");
					cube2 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getString("id");
					temporalStartCube2 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(0);
					temporalEndCube2 = processGraph.getJSONObject(getFilterCollectionNode(dataNode)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(1);
					payLoad2 = storedPayLoads.getString(dataNode);
				}
			}
		}				
		
		int noOfDimsCube1 = 0;
		int noOfDimsCube2 = 0;
		Collection coll1 = collectionMap.get(cube1);
		Collection coll2 = collectionMap.get(cube2);
		
		noOfDimsCube1 = coll1.getCubeColonDimensions().size();
		noOfDimsCube2 = coll2.getCubeColonDimensions().size();
		
		log.debug(temporalStartCube1);
		log.debug(temporalEndCube1);
		log.debug(temporalStartCube2);
		log.debug(temporalEndCube2);
						
		if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && !temporalStartCube2.equals(temporalEndCube2) && payLoad2.contains("coverage") && payLoad1.contains("coverage") && payLoad1.contains("condense")) {
			String payLoad2MergeMask = payLoad2.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess);
			
			try {
				replacement = processArguments.getDouble("replacement");
				wcpsMaskpayLoad.append("(" + payLoad1 + "*" + "(not("+payLoad2.replaceAll("\\$pm", "\\$rm")+")"+")" + " + " + "(("+payLoad2MergeMask+")"+"*"+replacement+"))");
				wcpsPayLoad=wcpsMaskpayLoad;
			}catch(Exception e) {
				wcpsMaskpayLoad.append("(" + payLoad1 + "*" + "(not("+payLoad2.replaceAll("\\$pm", "\\$rm")+")"+")");
				wcpsPayLoad=wcpsMaskpayLoad;
			}
			
			wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsMaskpayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMaskpayLoad.toString());
			log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
			log.debug("Mask Process PayLoad is : ");
		}
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && temporalStartCube2.equals(temporalEndCube2) && !payLoad2.contains("condense") && !payLoad1.contains("coverage") && !payLoad1.contains("condense")) {
			String timeImageCrsDomain = Pattern.compile(" X"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" Y"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" E"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(" N"+"\\(.*?\\)").matcher(timeImageCrsDomain).replaceAll("");
			timeImageCrsDomain = Pattern.compile(",").matcher(timeImageCrsDomain).replaceAll("");
			String XImageCrsDomain = Pattern.compile(" DATE"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			XImageCrsDomain = Pattern.compile(" Y"+"\\(.*?\\)").matcher(XImageCrsDomain).replaceAll("");
			XImageCrsDomain = Pattern.compile(" N"+"\\(.*?\\)").matcher(XImageCrsDomain).replaceAll("");
			XImageCrsDomain = Pattern.compile(",").matcher(XImageCrsDomain).replaceAll("");
			String YImageCrsDomain = Pattern.compile(" DATE"+"\\(.*?\\)").matcher(payLoad1).replaceAll("");
			YImageCrsDomain = Pattern.compile(" X"+"\\(.*?\\)").matcher(YImageCrsDomain).replaceAll("");
			YImageCrsDomain = Pattern.compile(" E"+"\\(.*?\\)").matcher(YImageCrsDomain).replaceAll("");
			YImageCrsDomain = Pattern.compile(",").matcher(YImageCrsDomain).replaceAll("");					
			
			String payLoad1Merge = payLoad1.replaceAll("\\sDATE"+"\\(.*?\\)", " DATE\\(\\$T" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyOfCurrentProcess + "\\)");				
			String payLoad2Merge = payLoad2.replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyOfCurrentProcess + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyOfCurrentProcess + "\\)");
			String payLoad2MergeMask = payLoad2Merge.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess);
			
			log.debug(timeImageCrsDomain);
			log.debug(XImageCrsDomain);
			log.debug(YImageCrsDomain);
			log.debug(payLoad1Merge);
			log.debug(payLoad2Merge);
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);					
			
			try {
				replacement = processArguments.getDouble("replacement");						
				wcpsMaskpayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", X)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", Y)) values (" + payLoad1Merge + " * " + "(not("+payLoad2Merge.replaceAll("\\$pm", "\\$rm")+")"+"))" + " + " + "(("+payLoad2MergeMask.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess)+")"+" * "+replacement+")");
				wcpsPayLoad=wcpsMaskpayLoad;
			}catch(Exception e) {
				wcpsMaskpayLoad.append(" coverage merge" + nodeKeyOfCurrentProcess + " over $T" + nodeKeyOfCurrentProcess + " t(imageCrsDomain(" +timeImageCrsDomain+ ", DATE)), $X" + nodeKeyOfCurrentProcess + " x(imageCrsDomain("+XImageCrsDomain+", E)), $Y" + nodeKeyOfCurrentProcess + " y(imageCrsDomain(" +YImageCrsDomain+ ", N)) values (" + payLoad1Merge + " * " + "(not("+payLoad2Merge.replaceAll("\\$pm", "\\$rm")+")"+"))");
				wcpsPayLoad=wcpsMaskpayLoad;
			}
			
			wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsMaskpayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMaskpayLoad.toString());
			log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
			log.debug("Mask Process PayLoad is : ");
		}
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && !temporalStartCube2.equals(temporalEndCube2) && !payLoad2.contains("condense") && payLoad1.contains("coverage") && payLoad1.contains("condense")) {
			log.debug(payLoad2);
			String payLoad2MergeMask = payLoad2.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess);
			
			try {
				replacement = processArguments.getDouble("replacement");
				wcpsMaskpayLoad.append("(" + payLoad1 + "*" + "(not("+payLoad2.replaceAll("\\$pm", "\\$rm")+")"+")" + " + " + "(("+payLoad2MergeMask+")"+"*"+replacement+"))");
				wcpsPayLoad=wcpsMaskpayLoad;
			}catch(Exception e) {
				wcpsMaskpayLoad.append("(" + payLoad1 + "*" + "(not("+payLoad2.replaceAll("\\$pm", "\\$rm")+")"+")");
				wcpsPayLoad=wcpsMaskpayLoad;
			}
			
			wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsMaskpayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMaskpayLoad.toString());
			log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
			log.debug("Mask Process PayLoad is : ");
		}
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && !temporalStartCube2.equals(temporalEndCube2) && !payLoad2.contains("condense") && payLoad1.contains("coverage") && !payLoad1.contains("condense")) {
			try {
				replacement = processArguments.getDouble("replacement");
				wcpsMaskpayLoad.append("(" + payLoad1 + "*(not("+payLoad2.replaceAll("\\$pm", "\\$rm")+")"+")" + " + " + "(("+payLoad2.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess)+")"+"*"+replacement+"))");
				wcpsPayLoad=wcpsMaskpayLoad;
			}catch(Exception e) {
				wcpsMaskpayLoad.append("(" + payLoad1 + "*(not("+payLoad2.replaceAll("\\$pm", "\\$rm")+")"+")");
				wcpsPayLoad=wcpsMaskpayLoad;
			}
			
			wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsMaskpayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMaskpayLoad.toString());
			log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
			log.debug("Mask Process PayLoad is : ");
		}
		else if (noOfDimsCube1==noOfDimsCube2 && !temporalStartCube1.equals(temporalEndCube1) && temporalStartCube2.equals(temporalEndCube2) && payLoad2.contains("condense") && !payLoad2.contains("coverage") && payLoad1.contains("coverage") && payLoad1.contains("condense")) {
			String payLoad2Merge = payLoad2.replaceAll("\\sY"+"\\(.*?\\)", " Y\\(\\$Y" + nodeKeyofCube1 + "\\)").replaceAll("\\sX"+"\\(.*?\\)", " X\\(\\$X" + nodeKeyofCube1 + "\\)").replaceAll("\\sN"+"\\(.*?\\)", " N\\(\\$Y" + nodeKeyofCube1 + "\\)").replaceAll("\\sE"+"\\(.*?\\)", " E\\(\\$X" + nodeKeyofCube1 + "\\)");
			String payLoad2MergeMask = payLoad2Merge.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess);
			
			log.debug(payLoad2Merge);					
			log.debug("Cube1 : " + payLoad1);
			log.debug("Cube2 : " + payLoad2);
			
			try {
				replacement = processArguments.getDouble("replacement");
				wcpsMaskpayLoad.append("" + payLoad1 + "*(not("+payLoad2Merge.replaceAll("\\$pm", "\\$rm")+"))" + "+("+payLoad2MergeMask.replaceAll("\\$pm", "\\$pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess)+")*" + replacement +")");					
				wcpsPayLoad=wcpsMaskpayLoad;
			}catch(Exception e) {
				wcpsMaskpayLoad.append("" + payLoad1 + "*(not("+payLoad2Merge.replaceAll("\\$pm", "\\$rm")+"))");					
				wcpsPayLoad=wcpsMaskpayLoad;
			}
			
			wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsMaskpayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMaskpayLoad.toString());
			log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
			log.debug("Mask Process PayLoad is : ");
		}
	}
	
	private void buildResampleCubeTemporal(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsResamplepayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderResample = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String collectionID = null;
		String targetCollectionID = null;
		String temporalStartCube1 = null;
		String temporalEndCube1 = null;
		double resSource = 0;
		double resTarget = 0;
		if (processArguments.get("data") instanceof JSONObject) {
			temporalStartCube1 = processGraph.getJSONObject(getFilterCollectionNode(nodeKeyOfCurrentProcess)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(0);
			temporalEndCube1 = processGraph.getJSONObject(getFilterCollectionNode(nodeKeyOfCurrentProcess)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(1);
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
					//collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		if (processArguments.get("target") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("target").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("target").getString("from_parameter").equals("target")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("target").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					targetCollectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
				}
			}
		}
		String xAxis = null;
		String yAxis = null;
		String xLow = null;
		String yLow = null;
		String xHigh = null;
		String yHigh = null;
		String tempAxis = null;
		String tempLow = null;
		String tempHigh = null;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			if(filter.getAxis().contains(collectionID)) {
				String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
				if (axisUpperCase.equals("N") || axisUpperCase.equals("Y") || axisUpperCase.equals("LAT")) {
					yAxis = filter.getAxis().replace("_"+ collectionID, "");
					yLow = filter.getLowerBound();
					yHigh = filter.getUpperBound();
				}
				if (axisUpperCase.equals("E") || axisUpperCase.equals("X") || axisUpperCase.equals("LONG")) {
					xAxis = filter.getAxis().replace("_"+ collectionID, "");
					xLow = filter.getLowerBound();
					xHigh = filter.getUpperBound();
				}
				if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
					tempAxis = filter.getAxis().replace("_"+ collectionID, "");
					tempLow = filter.getLowerBound();
					tempHigh = filter.getUpperBound();
				}
			}
		}
		
		try {
			URL url = new URL(wcpsEndpoint + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionID);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
			}
					
			Element coverageDescElement = rootNode.getChild("CoverageDescription", defaultNS);
			Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
			Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
			String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
			String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			//String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");
			
			for(int a = 0; a < axis.length; a++) {
				if(axis[a].equals("DATE")  || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time") || axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
					boolean isDate = true;
					try {
						Integer isDateInteger = Integer.parseInt(minValues[a].replaceAll("\"", ""));
						isDate = false;
					}
					catch(Exception e) {
						
					}
					if (isDate) {
					String[] taxis = null;
					try {
						List<Element> tList = rootNode.getChild("CoverageDescription", defaultNS).getChild("domainSet", gmlNS).getChild("RectifiedGrid", gmlNS).getChildren("offsetVector", gmlNS);
						taxis = tList.get(a).getValue().split(" ");								
						resSource = Double.parseDouble(taxis[0]);
				    }catch(Exception e) {
				    	log.warn("Irregular Axis :" + e.getMessage());						    	
				    }
					}
					else {								
						String[] taxis = null;
						try {
							List<Element> tList = rootNode.getChild("CoverageDescription", defaultNS).getChild("domainSet", gmlNS).getChild("RectifiedGrid", gmlNS).getChildren("offsetVector", gmlNS);
							taxis = tList.get(a).getValue().split(" ");
							//TODO Adjust resolution with the exact unit of Time Dimension
							resSource = Double.parseDouble(taxis[0]);
					    }catch(Exception e) {
					    	log.warn("Irregular Axis :" + e.getMessage());
					    }
					}
				}
			}
		}
		catch (MalformedURLException e) {
		} 
    	catch (IOException e) {
    	}		    	    
    	catch (JDOMException e) {
    	}
		
		try {
			URL url = new URL(wcpsEndpoint + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + targetCollectionID);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
			}
					
			Element coverageDescElement = rootNode.getChild("CoverageDescription", defaultNS);
			Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
			Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
			String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
			String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			//String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");
			
			for(int a = 0; a < axis.length; a++) {
				if(axis[a].equals("DATE")  || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time") || axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
					boolean isDate = true;
					try {
						Integer isDateInteger = Integer.parseInt(minValues[a].replaceAll("\"", ""));
						isDate = false;
					}
					catch(Exception e) {								
					}
					if (isDate) {
						String[] taxis = null;
						try {
							List<Element> tList = rootNode.getChild("CoverageDescription", defaultNS).getChild("domainSet", gmlNS).getChild("RectifiedGrid", gmlNS).getChildren("offsetVector", gmlNS);
							taxis = tList.get(a).getValue().split(" ");									
							resTarget = Double.parseDouble(taxis[0]);
						}catch(Exception e) {
							log.warn("Irregular Axis :" + e.getMessage());						    	
						}
					}
					
					else {								
						String[] taxis = null;
						try {
							List<Element> tList = rootNode.getChild("CoverageDescription", defaultNS).getChild("domainSet", gmlNS).getChild("RectifiedGrid", gmlNS).getChildren("offsetVector", gmlNS);
							taxis = tList.get(a).getValue().split(" ");
							//TODO Adjust resolution with the exact unit of Time Dimension
							resTarget = Double.parseDouble(taxis[0]);
						}catch(Exception e) {
							log.warn("Irregular Axis :" + e.getMessage());
						}
					}
				}
			}
		}
		catch (MalformedURLException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());		
		} catch (IOException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());		
		} catch (JDOMException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());		
		}				
		wcpsResamplepayLoad.append(createResampleTemporalCubeWCPSString(nodeKeyOfCurrentProcess, payLoad, resSource, resTarget, xAxis, xLow, xHigh, yAxis, yLow, yHigh, tempAxis, tempLow, tempHigh, temporalStartCube1, temporalEndCube1));
		wcpsPayLoad=wcpsResamplepayLoad;
		wcpsStringBuilder = wcpsStringBuilderResample.append(wcpsResamplepayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsResamplepayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Resample Temoral Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildResampleCubeSpatial(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsResamplepayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderResample = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String collectionID = null;
		String targetCollectionID = null;
		String temporalStartCube1 = null;
		String temporalEndCube1 = null;
		if (processArguments.get("data") instanceof JSONObject) {
			temporalStartCube1 = processGraph.getJSONObject(getFilterCollectionNode(nodeKeyOfCurrentProcess)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(0);
			temporalEndCube1 = processGraph.getJSONObject(getFilterCollectionNode(nodeKeyOfCurrentProcess)).getJSONObject("arguments").getJSONArray("temporal_extent").getString(1);
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
					//collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		if (processArguments.get("target") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("target").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("target").getString("from_parameter").equals("target")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("target").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					targetCollectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
				}
			}
		}
		String xAxis = null;
		String yAxis = null;
		String xLow = null;
		String yLow = null;
		String xHigh = null;
		String yHigh = null;
		String tempAxis = null;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();
			if(axis.contains(collectionID)) {
				String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
				if (axisUpperCase.equals("N") || axisUpperCase.equals("Y") || axisUpperCase.equals("LAT")) {
					yAxis = axis.replace("_"+ collectionID, "");
					yLow = filter.getLowerBound();
					yHigh = filter.getUpperBound();
				}
				if (axisUpperCase.equals("E") || axisUpperCase.equals("X") || axisUpperCase.equals("LONG")) {
					xAxis = axis.replace("_"+ collectionID, "");
					xLow = filter.getLowerBound();
					xHigh = filter.getUpperBound();
				}
				if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
					tempAxis = axis.replace("_"+ collectionID, "");
					
				}
			}
		}
		
		String resSource = null;
		try {
			URL url = new URL(wcpsEndpoint
					+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionID);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
			}					
			log.debug("root node info: " + rootNode.getName());

			Boolean bandsMeta = false;
			Element metadataElement = null;
			try {
				metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS).getChild("Extension", gmlNS).getChild("covMetadata", gmlNS);
			}catch(Exception e) {
			}
			List<Element> bandsList = null;
			try {
				bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
				bandsMeta = true;
			}catch(Exception e) {
			}
			
			if (bandsMeta) {
				try {
					for(int c = 0; c < bandsList.size(); c++) {						
						Element band = bandsList.get(c);					
						try {
							resSource = band.getChildText("gsd");
						}catch(Exception e) {
						}
					}
				}catch(Exception e) {
				}
			}
		}
		catch (MalformedURLException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());		
		} catch (IOException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());		
		} catch (JDOMException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());		
		}
		
		String resTarget = null;
		try {
			URL url = new URL(wcpsEndpoint
					+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + targetCollectionID);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
			}					
			log.debug("root node info: " + rootNode.getName());

			Boolean bandsMeta = false;
			Element metadataElement = null;
			try {
				metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS).getChild("Extension", gmlNS).getChild("covMetadata", gmlNS);
			}catch(Exception e) {
			}
			List<Element> bandsList = null;
			try {
				bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
				bandsMeta = true;
			}catch(Exception e) {
			}
			if (bandsMeta) {
				try {
					for(int c = 0; c < bandsList.size(); c++) {						
						Element band = bandsList.get(c);					
						try {
							resTarget = band.getChildText("gsd");
						}catch(Exception e) {
						}
					}
				}catch(Exception e) {
				}
			}
		}
		catch (MalformedURLException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());		
		} catch (IOException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());		
		} catch (JDOMException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());		
		}
		
		wcpsResamplepayLoad.append(createResampleSpatialCubeWCPSString(nodeKeyOfCurrentProcess, payLoad, resSource, resTarget, xAxis, xLow, xHigh, yAxis, yLow, yHigh, tempAxis, temporalStartCube1, temporalEndCube1));
		wcpsPayLoad=wcpsResamplepayLoad;
		wcpsStringBuilder = wcpsStringBuilderResample.append(wcpsResamplepayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsResamplepayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Resample Spatial Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildRunUDF(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		this.withUDF =  true;
		StringBuilder wcpsUDFpayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderUDFPayload = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments = processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		
		if (processArguments.getString("runtime").toLowerCase().equals("python")) {
			if (processArguments.get("data") instanceof JSONObject) {
				for (String fromType : processArguments.getJSONObject("data").keySet()) {
					if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
						payLoad = wcpsPayLoad.toString();
					}
					else if (fromType.equals("from_node")) {
						String dataNode = processArguments.getJSONObject("data").getString("from_node");
						payLoad = storedPayLoads.getString(dataNode);
					}
				}
			}
		}
		if (processArguments.getString("runtime").toLowerCase().equals("r")) {
			if (processArguments.get("data") instanceof JSONObject) {
				for (String fromType : processArguments.getJSONObject("data").keySet()) {
					if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
						payLoad = wcpsPayLoad.toString();
					}
					else if (fromType.equals("from_node")) {
						String dataNode = processArguments.getJSONObject("data").getString("from_node");
						payLoad = storedPayLoads.getString(dataNode);
					}
				}
			}
		}
		wcpsUDFpayLoad.append(payLoad);
		wcpsPayLoad=wcpsUDFpayLoad;
		String saveUDFPayload = wcpsStringBuilderUDFPayload.append(wcpsUDFpayLoad.toString()).toString();
		StringBuilder wcpsStringBuilderSaveUDFResult = new StringBuilder("");
		wcpsStringBuilderSaveUDFResult.append(createUDFReturnResultWCPSString(saveUDFPayload));
		wcpsStringBuilder = wcpsStringBuilderSaveUDFResult;
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsUDFpayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("UDF Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildRunUDFExternally(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsUDFpayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderUDFPayload = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		
		if (processArguments.getJSONObject("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		if (processArguments.getJSONObject("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}				
		wcpsUDFpayLoad.append(payLoad);
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsUDFpayLoad.toString());
		String saveUDFPayload = wcpsStringBuilderUDFPayload.append(wcpsUDFpayLoad.toString()).toString();
		StringBuilder wcpsStringBuilderSaveUDFResult = new StringBuilder("");
		wcpsStringBuilderSaveUDFResult.append(createUDFReturnResultWCPSString(saveUDFPayload));
		wcpsStringBuilder = wcpsStringBuilderSaveUDFResult;
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("UDF Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildFilterBBox(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsFilterBboxpayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderFilterBboxPayload = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");						
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		wcpsFilterBboxpayLoad.append(payLoad);
		wcpsPayLoad=wcpsFilterBboxpayLoad;
		wcpsStringBuilder=wcpsStringBuilderFilterBboxPayload.append(wcpsFilterBboxpayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsFilterBboxpayLoad.toString());
		log.debug("Filter Bounding Box Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildFilterTemporal(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsFilterDatepayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderFilterDatePayload = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");							
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		wcpsFilterDatepayLoad.append(payLoad);
		wcpsPayLoad=wcpsFilterDatepayLoad;
		wcpsStringBuilder=wcpsStringBuilderFilterDatePayload.append(wcpsFilterDatepayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsFilterDatepayLoad.toString());
		log.debug("Filter Temporal Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildFilterBands(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsFilterpayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderFilterPayload = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String collectionID = null;
		String collectionVar = null;
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_paratmeter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
					collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		String filterString = payLoad;
		filterString = filterString.substring(collectionVar.length());
		JSONArray currentProcessBands = currentProcessArguments.getJSONArray("bands");
		String bandfromIndex = currentProcessBands.getString(0);
		String bandName = null;
						
		try {
			URL url = new URL(wcpsEndpoint
					+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionID);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
			}

			Boolean bandsMeta = false;
			Element metadataElement = null;
			try {
				metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS).getChild("Extension", gmlNS).getChild("covMetadata", gmlNS);
			}catch(Exception e) {
			}
			List<Element> bandsList = null;
			try {
				bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
				bandsMeta = true;
			}catch(Exception e) {
			}
			
			if (bandsMeta) {
				try {
					for(int c = 0; c < bandsList.size(); c++) {
						String bandCommonName = null;
						Element band = bandsList.get(c);
						try {
							bandCommonName = band.getChildText("common_name");
							if (bandCommonName.equals(bandfromIndex)) {
								bandName = band.getChildText("name");
								break;
							}
							else {
								bandName = bandfromIndex;
							}
						}catch(Exception e) {
							bandName = bandfromIndex;									
						}
					}
				}catch(Exception e) {
				}
			}
		}
		catch (MalformedURLException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());

		} catch (IOException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());

		} catch (JDOMException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());

		}
		
		wcpsFilterpayLoad.append(createBandSubsetString(collectionVar, bandName, filterString));
		wcpsPayLoad=wcpsFilterpayLoad;
		wcpsStringBuilder=wcpsStringBuilderFilterPayload.append(wcpsFilterpayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsFilterpayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Filter Bands Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildFilterPolygon(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsFilterPolygonpayLoad = new StringBuilder("clip(");
		StringBuilder wcpsStringBuilderFilterPolygonPayload = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		StringBuilder stringBuilderPoly = new StringBuilder();
		stringBuilderPoly.append("POLYGON((");
		for (int f = 0; f < filtersPolygon.size(); f++) {
			Filter filter = filtersPolygon.get(f);					
			String low = filter.getLowerBound();
			String high = filter.getUpperBound();					
			stringBuilderPoly.append(low);					
			if (high != null && !(high.equals(low))) {
				stringBuilderPoly.append(" ");
				stringBuilderPoly.append(high);
			}
			if (f < filtersPolygon.size() - 1) {
				stringBuilderPoly.append(",");
			}
		}
		stringBuilderPoly.append("))");
		wcpsFilterPolygonpayLoad.append(payLoad + "," + stringBuilderPoly.toString() + ")");
		wcpsPayLoad=wcpsFilterPolygonpayLoad;
		wcpsStringBuilder=wcpsStringBuilderFilterPolygonPayload.append(wcpsFilterPolygonpayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsFilterPolygonpayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Filter Polygon Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));		
	}
	
	private void buildIf(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsIFpayLoad = new StringBuilder("");
		String payLoad = null;
		String acceptPayLoad = null;
		String rejectPayLoad = null;
		double accept = 0;
		double reject = 0;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		
		if (processArguments.get("value") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("value").keySet()) {
				if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("value").getString("from_node");
					payLoad = storedPayLoads.getString(dataNode);
					log.debug("IF Process : ");
					if (processArguments.get("accept") instanceof JSONObject) {
						String acceptDataNode = processArguments.getJSONObject("accept").getString("from_node");
						acceptPayLoad = storedPayLoads.getString(acceptDataNode);
						log.debug("Accept Payload : " + acceptPayLoad);
					}
					else {
						accept = processArguments.getDouble("accept");
						log.debug("Accept Payload : " + accept);
					}
					if (processArguments.get("reject") instanceof JSONObject) {
						String rejectDataNode = processArguments.getJSONObject("reject").getString("from_node");
						rejectPayLoad = storedPayLoads.getString(rejectDataNode);
						log.debug("Reject Payload : " + rejectPayLoad);
					}
					else {
						reject = processArguments.getDouble("reject");
						log.debug("Reject Payload : " + reject);
					}
					
				}
			}
		}
		
		if (processArguments.get("accept") instanceof JSONObject) {
			if (processArguments.get("reject") instanceof JSONObject) {
				wcpsIFpayLoad.append("("+payLoad+"*"+acceptPayLoad+"+"+"(not "+payLoad.replaceAll("pm", "pm" + nodeKeyOfCurrentProcess).replaceAll("merge", "merge" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess)+")*"+rejectPayLoad+")");
			}
			else {
				wcpsIFpayLoad.append("("+payLoad+"*"+acceptPayLoad+"+"+"(not "+payLoad.replaceAll("pm", "pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess)+")*"+reject+")");
			}					
		}
		else {
			if (processArguments.get("reject") instanceof JSONObject) {
				wcpsIFpayLoad.append("("+payLoad+"*"+accept+"+"+"(not "+payLoad.replaceAll("pm", "pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess)+")*"+rejectPayLoad+")");
			}
			else {
				wcpsIFpayLoad.append("("+payLoad+"*"+accept+"+"+"(not "+payLoad.replaceAll("pm", "pm" + nodeKeyOfCurrentProcess).replaceAll("\\$T", "\\$T" + nodeKeyOfCurrentProcess).replaceAll("\\$Y", "\\$Y" + nodeKeyOfCurrentProcess).replaceAll("\\$X", "\\$X" + nodeKeyOfCurrentProcess).replaceAll("\\$N", "\\$N" + nodeKeyOfCurrentProcess).replaceAll("\\$E", "\\$E" + nodeKeyOfCurrentProcess)+")*"+reject+")");
			}	
		}
		
		wcpsPayLoad=wcpsIFpayLoad;
		StringBuilder wcpsStringBuilderMaskThresPayload = basicWCPSStringBuilder(varPayLoad.toString());
		wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsIFpayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsIFpayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("IF Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildArrayFilter(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsArrayFilterpayLoad = new StringBuilder("");
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
					varPayLoad.append(" $filterArray"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$qm") + " " + processArguments.getString("comparator") + " " + processArguments.getString("threshold")+",");
					varPayLoad.append(" $payLoad"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$rm")+",");
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					payLoad = storedPayLoads.getString(dataNode);
					log.debug("Array Filterd Process : ");
					varPayLoad.append(" $filterArray"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$qm") + " " + processArguments.getString("comparator") + " " + processArguments.getString("threshold")+",");
					varPayLoad.append(" $payLoad"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$rm")+",");
				}
			}
		}
		
		wcpsArrayFilterpayLoad.append("(($filterArray"+nodeKeyOfCurrentProcess+")"+"*$payLoad"+nodeKeyOfCurrentProcess+")");
		wcpsPayLoad=wcpsArrayFilterpayLoad;
		
		StringBuilder wcpsStringBuilderMaskThresPayload = basicWCPSStringBuilder(varPayLoad.toString());
		wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsArrayFilterpayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsArrayFilterpayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Array Filterd Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildNormalizedDifference(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsNormDiffpayLoad = new StringBuilder("((double)");
		StringBuilder wcpsStringBuilderNormDiff = basicWCPSStringBuilder(varPayLoad.toString());
		JSONObject bandArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String band1 = null;
		String band2 = null;
		if (bandArguments.get("x") instanceof JSONObject) {
			for (String fromType : bandArguments.getJSONObject("x").keySet()) {
				if (fromType.equals("from_parameter") && bandArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					band1 = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = bandArguments.getJSONObject("x").getString("from_node");
					band1 = storedPayLoads.getString(dataNode);							
				}
			}
		}
		if (bandArguments.get("y") instanceof JSONObject) {
			for (String fromType : bandArguments.getJSONObject("y").keySet()) {
				if (fromType.equals("from_parameter") && bandArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					band2 = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = bandArguments.getJSONObject("y").getString("from_node");
					band2 = storedPayLoads.getString(dataNode);							
				}
			}
		}
		wcpsNormDiffpayLoad.append(band2 + " - " + band1 + ") / ((double)" + band2 + " + " + band1 + ")");
		wcpsPayLoad=wcpsNormDiffpayLoad;
		wcpsStringBuilder=wcpsStringBuilderNormDiff.append(wcpsNormDiffpayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsNormDiffpayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Normalized Difference Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildNDVI(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsNDVIpayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderNDVI = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String collectionID = null;
		String collectionVar = null;
		String redBand = null;
		String nirBand = null;
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
					collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
					payLoad = storedPayLoads.getString(dataNode);							
				}
			}
		}
		try {
			redBand = processArguments.getString("red");
		}catch(Exception e) {
		}
		try {
			redBand = processArguments.getString("nir");
		}catch(Exception e) {
		}
		if (redBand==null && nirBand==null) {
			for (int a = 0; a < aggregates.size(); a++) {
				if (aggregates.get(a).getOperator().equals("NDVI_"+collectionID)) {
					wcpsNDVIpayLoad.append(createNDVIWCPSString(payLoad, collectionVar, aggregates.get(a)));
					wcpsPayLoad=wcpsNDVIpayLoad;
					wcpsStringBuilder=wcpsStringBuilderNDVI.append(wcpsNDVIpayLoad.toString());
					storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsNDVIpayLoad.toString());
					log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
					log.debug("NDVI Process PayLoad is : ");
					log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
				}
			}
		}
		else {
			String filterString1 = payLoad.substring(collectionVar.length());
			String red1 = createBandSubsetString(collectionVar, redBand, filterString1);
			String nir1 = createBandSubsetString(collectionVar, nirBand, filterString1);
			StringBuilder stringBuilder1 = new StringBuilder("((double)");
			stringBuilder1.append(nir1 + " - " + red1);
			stringBuilder1.append(") / ((double)");
			stringBuilder1.append(nir1 + " + " + red1);
			stringBuilder1.append(")");
			wcpsNDVIpayLoad.append(stringBuilder1.toString());
			wcpsPayLoad=wcpsNDVIpayLoad;
			wcpsStringBuilder=wcpsStringBuilderNDVI.append(wcpsNDVIpayLoad.toString());
			storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsNDVIpayLoad.toString());
			log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
			log.debug("NDVI Process PayLoad is : ");
			log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
		}
	}
	
	private void buildApply(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsApplypayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderApply = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String collectionID = null;
		String collectionVar = null;
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
					collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
					payLoad = storedPayLoads.getString(dataNode);							
				}
			}
		}
		
		String filterString = payLoad;
		filterString = filterString.substring(collectionVar.length());
		WCPSApplyFunc applyfunc = new WCPSApplyFunc();
		wcpsApplypayLoad.append("(" + applyfunc.createApplyWCPSString(wcpsEndpoint, openEOEndpoint, processGraph, nodeKeyOfCurrentProcess, payLoad, filterString, collectionVar, collectionID) + ")");
		wcpsPayLoad=wcpsApplypayLoad;
		wcpsStringBuilder = wcpsStringBuilderApply.append(wcpsApplypayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsApplypayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Apply Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildRecuceDimension(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsReducepayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderReduce = basicWCPSStringBuilder(varPayLoad.toString());
		String dimension = currentProcessArguments.getString("dimension");
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String collectionID = null;
		String collectionVar = null;
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
					collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
					payLoad = storedPayLoads.getString(dataNode);							
				}
			}
		}
		String filterString = payLoad;
		filterString = filterString.substring(collectionVar.length());
		WCPSReduceFunc reducerfunc = new WCPSReduceFunc();
		wcpsReducepayLoad.append(reducerfunc.createReduceWCPSString(filters, aggregates, wcpsEndpoint, openEOEndpoint, processGraph, nodeKeyOfCurrentProcess, payLoad, filterString, collectionVar, collectionID, dimension));
		wcpsPayLoad=wcpsReducepayLoad;
		wcpsStringBuilder = wcpsStringBuilderReduce.append(wcpsReducepayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsReducepayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Reduce Process PayLoad is : " + wcpsPayLoad.toString());
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildLinearScaleCube(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsScalepayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderScale = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		wcpsScalepayLoad.append(createLinearScaleCubeWCPSString(nodeKeyOfCurrentProcess, payLoad));
		wcpsPayLoad=wcpsScalepayLoad;
		wcpsStringBuilder = wcpsStringBuilderScale.append(wcpsScalepayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsScalepayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Linear Scale Cube Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildLinearStretchCube(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsStretchpayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderStretch = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					payLoad = storedPayLoads.getString(dataNode);							
				}
			}
		}
		wcpsStretchpayLoad.append(createLinearStretchCubeWCPSString(nodeKeyOfCurrentProcess, payLoad));
		wcpsPayLoad=wcpsStretchpayLoad;
		wcpsStringBuilder = wcpsStringBuilderStretch.append(wcpsStretchpayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsStretchpayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Linear Stretch Cube Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildResampleSpatial(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsResamplepayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderResample = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String collectionID = null;
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
					//collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		String xAxis = null;
		String yAxis = null;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();
			if(axis.contains(collectionID)) {
				String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
				if (axisUpperCase.equals("N") || axisUpperCase.equals("Y") || axisUpperCase.equals("LAT")) {
					yAxis = axis.replace("_"+ collectionID, "");
				}
				if (axisUpperCase.equals("E") || axisUpperCase.equals("X") || axisUpperCase.equals("LONG")) {
					xAxis = axis.replace("_"+ collectionID, "");
				}
			}
		}				
		wcpsResamplepayLoad.append(createResampleSpatialWCPSString(nodeKeyOfCurrentProcess, payLoad, xAxis, yAxis));
		wcpsPayLoad=wcpsResamplepayLoad;
		wcpsStringBuilder = wcpsStringBuilderResample.append(wcpsResamplepayLoad.toString());
		storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsResamplepayLoad.toString());
		log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
		log.debug("Resample Spatial Process PayLoad is : ");
		log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
	}
	
	private void buildSaveResult(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		String savePayload = wcpsStringBuilder.toString();
		StringBuilder wcpsStringBuilderSaveResult = new StringBuilder("");
		log.info("Collection Dims : " + collDims2D);
		wcpsStringBuilderSaveResult.append(createReturnResultWCPSString(nodeKeyOfCurrentProcess, savePayload, collDims2D));
		wcpsStringBuilder = wcpsStringBuilderSaveResult;
	}
	
	private void buildAggregateTime(JSONObject currentProcessArguments, StringBuilder wcpsPayLoad, StringBuilder varPayLoad, JSONObject storedPayLoads, boolean collDims2D, String nodeKeyOfCurrentProcess) {
		StringBuilder wcpsTempAggpayLoad = new StringBuilder("");
		StringBuilder wcpsStringBuilderTempAgg = basicWCPSStringBuilder(varPayLoad.toString());
		String payLoad = null;
		JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
		String collectionID = null;
		String collectionVar = null;
		if (processArguments.get("data") instanceof JSONObject) {
			for (String fromType : processArguments.getJSONObject("data").keySet()) {
				if (fromType.equals("from_parameter") && processArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
					payLoad = wcpsPayLoad.toString();
				}
				else if (fromType.equals("from_node")) {
					String dataNode = processArguments.getJSONObject("data").getString("from_node");
					String collectionNodeKey = getFilterCollectionNode(dataNode);
					collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
					collectionVar = "$cube"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
					payLoad = storedPayLoads.getString(dataNode);
				}
			}
		}
		String tempAxis = null;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();
			if(axis.contains(collectionID)) {
				String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();				
				if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
					tempAxis = axis.replace("_"+ collectionID, "");
				}
			}
		}
		for (int a = 0; a < aggregates.size(); a++) {
			if (aggregates.get(a).getAxis().equals(tempAxis+"_"+collectionID+nodeKeyOfCurrentProcess)) {
				wcpsTempAggpayLoad.append(createTempAggWCPSString(nodeKeyOfCurrentProcess, collectionVar, collectionID, aggregates.get(a), tempAxis));
				//String replaceDate = Pattern.compile(tempAxis+"\\(.*?\\)").matcher(wcpsPayLoad).replaceAll(tempAxis+"\\(\\$pm\\)");
				String replaceDate = payLoad.replaceAll(tempAxis+"\\(.*?\\)", tempAxis+"\\(\\$pm\\"+ nodeKeyOfCurrentProcess +")");
				StringBuilder wcpsAggBuilderMod = new StringBuilder("");
				wcpsAggBuilderMod.append(replaceDate);
				wcpsTempAggpayLoad.append(wcpsAggBuilderMod);
				wcpsPayLoad=wcpsTempAggpayLoad;
				wcpsStringBuilder=wcpsStringBuilderTempAgg.append(wcpsTempAggpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsTempAggpayLoad.toString());
				log.debug("Process Stored for Node " + nodeKeyOfCurrentProcess + " : " + storedPayLoads.get(nodeKeyOfCurrentProcess));
				log.debug("Max/Min Time Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
		}
	}
	
	private JSONArray sortNodesArray() {
		JSONArray nodesArray = new JSONArray();
		JSONArray nodesSortedArray = new JSONArray();
		
		String saveNode = getSaveNode();
		JSONArray saveNodeAsArray = new JSONArray();
		
		saveNodeAsArray.put(saveNode);
		
		// Add the node Key of last Save_result node
		nodesArray.put(saveNodeAsArray);
		
		// Join the nodeKey in the list by going backwards in the ProcessGraph
		for (int n = 0; n < nodesArray.length(); n++) {
			for (int a = 0; a < nodesArray.getJSONArray(n).length(); a++) {
				JSONArray fromNodeOfProcess = getFromNodeOfCurrentKey(nodesArray.getJSONArray(n).getString(a));
				if (fromNodeOfProcess.length()>0) {
					nodesArray.put(fromNodeOfProcess);
				}
				// Add the Datacube nodeKey in the sorted List
				else if (fromNodeOfProcess.length()==0) {
					nodesSortedArray.put(nodesArray.getJSONArray(n).getString(a));
				}
			}
		}
		
		// Remove the nodeKeys that are duplicates
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}
		
		nodesArray.remove(nodesArray.length()-1);
		
		// Add the nodeKeys in correct order into the sorted Array
		for (int i = nodesArray.length()-1; i>0; i--) {
			if (nodesArray.getJSONArray(i).length()>0) {
				for (int a = 0; a < nodesArray.getJSONArray(i).length(); a++) {
					nodesSortedArray.put(nodesArray.getJSONArray(i).getString(a));
				}
			}
		}
		
		// Add the node Key of last Save_result node
		nodesSortedArray.put(saveNode);
		
		// Remove the nodeKeys that are duplicates
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}
		
		return nodesSortedArray;
	}

	private void build() {
		StringBuilder wcpsPayLoad = new StringBuilder("");
		StringBuilder varPayLoad = new StringBuilder("");
		JSONObject storedPayLoads = new JSONObject();
		//String collectionVar = "$c";
		
		log.debug(processGraph.toString());
		
		// Sort nodes in correct execution order
		JSONArray nodesSortedArray = sortNodesArray();
		
//		for (int c = 1; c <= collectionIDs.size(); c++) {
//			log.debug(collectionIDs.get(c - 1).getName());
//			String collectionID = processGraph.getJSONObject(collectionIDs.get(c - 1).getName()).getJSONObject("arguments").getString("id");
//			wcpsStringBuilder.append("$cube" + collectionID + collectionIDs.get(c - 1).getName() + " in (" + collectionID + ")");
//			if (c < collectionIDs.size()) {
//				wcpsStringBuilder.append(", ");
//			}
//		}		
		// Names of processes in correct order
		JSONArray processesSequence = new JSONArray();
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			processesSequence.put(processGraph.getJSONObject(nodesSortedArray.getString(i)).getString("process_id"));
		}
		
		log.debug("Process Graph's Nodes Sequence is : ");
		log.debug(nodesSortedArray);
		log.debug("Process Graph's Processes Sequence is : ");
		log.debug(processesSequence);
		log.debug("Executing Processes : " + nodesSortedArray);
		
		// Execute the Processes that are independent of their order in the Process Graph
		for(int a = 0; a<nodesSortedArray.length()-1; a++) {
			String nodeKeyOfCurrentProcess = nodesSortedArray.getString(a);
			String currentProcessID = processGraph.getJSONObject(nodeKeyOfCurrentProcess).getString("process_id");
			log.debug("Executing Process : " + currentProcessID);
			executeProcesses(currentProcessID, nodeKeyOfCurrentProcess);
		}
		wcpsStringBuilder = basicWCPSStringBuilder(varPayLoad.toString());
		boolean collDims2D = false;
		
		// Start build WCPS Queries according to correct order of Processes in the ProcessGraph
		myLoop:		for(int i = 0; i < nodesSortedArray.length(); i++) {
			String nodeKeyOfCurrentProcess = nodesSortedArray.getString(i);
			JSONObject currentProcess = processGraph.getJSONObject(nodeKeyOfCurrentProcess);
			String currentProcessID = currentProcess.getString("process_id");			
			JSONObject currentProcessArguments = currentProcess.getJSONObject("arguments");
			log.debug("Building WCPS Query for : " + currentProcessID);			
			
			if (currentProcessID.equals("load_collection")) {
				buildLoadCollection(currentProcessArguments, wcpsPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("merge_cubes")) {
				buildMergeCubes(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("mask")) {
				builMask(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);				
			}			
			if (currentProcessID.equals("resample_cube_temporal")) {
				buildResampleCubeTemporal(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("resample_cube_spatial")) {
				buildResampleCubeSpatial(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("run_udf")) {
				buildRunUDF(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
				break myLoop;
			}
			if (currentProcessID.equals("run_udf_externally")) {
				buildRunUDFExternally(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
				break myLoop;
			}
			if (currentProcessID.equals("filter_bbox")) {
				buildFilterBBox(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("filter_temporal")) {
            	buildFilterTemporal(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("filter_bands")) {
				buildFilterBands(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
//			if (currentProcessID.equals("mask_colored")) {
//				//TODO check why this was commented
//				log.warn("mask colored is currently untested and experimental");
//				buildMaskColored(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
//			}
			if (currentProcessID.equals("if")) {
				buildIf(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("array_filter")) {
				buildArrayFilter(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}			
			if (currentProcessID.equals("normalized_difference")) {
				buildNormalizedDifference(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("ndvi")) {
				buildNDVI(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
//			if (currentProcessID.equals("filter_polygon")) {
//				//TODO check why this was commented
//				log.warn("filter polygon is currently untested and experimental");
//				buildFilterPolygon(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
//			}
			if (currentProcessID.contains("_time")) {
				buildAggregateTime(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
//			if (currentProcessID.equals("lt")) {
//				//TODO check why this was commented
//				buildLt(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
//			}
//			if (currentProcessID.equals("gt")) {
//				//TODO check why this was commented
//				buildGt(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
//			}
			if (currentProcessID.equals("apply")) {
				buildApply(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("reduce_dimension")) {
				buildRecuceDimension(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("linear_scale_cube")) {
				buildLinearScaleCube(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("linear_stretch_cube")) {
				buildLinearStretchCube(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}			
			if (currentProcessID.equals("resample_spatial")) {
				buildResampleSpatial(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
			if (currentProcessID.equals("save_result")) {
				buildSaveResult(currentProcessArguments, wcpsPayLoad, varPayLoad, storedPayLoads, collDims2D, nodeKeyOfCurrentProcess);
			}
		}
	}
	
	private String createReturnResultWCPSString(String returnResultNodeKey, String payload, Boolean collDims2D) {
		StringBuilder resultBuilder = new StringBuilder("");
		resultBuilder.append(payload);
		log.info("Collection Dims : " + collDims2D);
		if (this.outputFormat.equals("netcdf")) {
			if (collDims2D) {
				resultBuilder.append(", \"" + this.outputFormat + "\" ," + "\"{ \\\"transpose\\\": [0,1] }\"" + ")");
			}
			else if (!collDims2D) {
				resultBuilder.append(", \"" + this.outputFormat + "\" ," + "\"{ \\\"transpose\\\": [1,2] }\"" + ")");
			}			
		}
		else {
			resultBuilder.append(", \"" + this.outputFormat + "\" )");
		}
		log.debug("Save payload : ");
		log.debug(resultBuilder);
		return resultBuilder.toString();
	}
	
	private String createUDFReturnResultWCPSString(String payload) {
		StringBuilder resultBuilder = new StringBuilder("");
		resultBuilder.append(payload);
		resultBuilder.append(", \"" + "gml" + "\" )");
		log.debug("Save UDF payload : ");
		log.debug(resultBuilder);
		return resultBuilder.toString();
	}
	
	private String createResampleSpatialWCPSString(String resampleNodeKey, String payload, String xAxis, String yAxis) {
		int projectionEPSGCode = 0;
		String wcps_endpoint = openEOEndpoint;
		
		try {
			projectionEPSGCode = processGraph.getJSONObject(resampleNodeKey).getJSONObject("arguments").getInt("projection");
		}catch(JSONException e) {
			log.error("no epsg code was detected!");
		}
		if(projectionEPSGCode == 0) {
			return "";
		}
		StringBuilder resampleBuilder = new StringBuilder("crsTransform(" );
		//TODO read the name of the spatial coordinate axis from describeCoverage or filter elements in order to correctly apply (E,N), (lat,lon) or X,Y depending on coordinate system
		resampleBuilder.append(payload);
		resampleBuilder.append(" ,{"
				+ xAxis +":\"" + wcps_endpoint + "/def/crs/EPSG/0/" + projectionEPSGCode + "\","
				+ yAxis +":\"" + wcps_endpoint + "/def/crs/EPSG/0/" + projectionEPSGCode + "\""
				+ "}, {})");
		return resampleBuilder.toString();
	}
	
	private String createResampleTemporalCubeWCPSString(String resampleNodeKey, String payload, double resSource, double resTarget, String xAxis, String xLow, String xHigh, String yAxis, String yLow, String yHigh, String tempAxis, String tempLow, String tempHigh, String temporalStartCube1, String temporalEndCube1) {
		//TODO Remove the extra adding of half the resolution in the scale range for Dates when the Rasdaman fixes the issue of shifting
		double res = resSource/resTarget;
		DateFormat toDateNewFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		long tempLowUnix = 0;
		long tempHighUnix = 0;
		long temporalStartCube1Unix = 0;
		long temporalEndCube1Unix = 0;
		try {
			tempLowUnix = toDateNewFormat.parse(tempLow).getTime() / 1000L;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			tempHighUnix = toDateNewFormat.parse(tempHigh).getTime() / 1000L;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			temporalStartCube1Unix = toDateNewFormat.parse(temporalStartCube1).getTime() / 1000L;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			temporalEndCube1Unix = toDateNewFormat.parse(temporalEndCube1).getTime() / 1000L;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long tempScaleUnix = Math.round(((tempHighUnix-tempLowUnix)*res)+tempLowUnix);
		Date tempScaleDate =  new java.util.Date(tempScaleUnix*1000L);
		String tempScale = toDateNewFormat.format(tempScaleDate);
		log.debug(tempLowUnix);
		log.debug(tempHighUnix);
		log.debug(tempScaleUnix);
		log.debug(res);
		if (res<1) {
		temporalStartCube1Unix = Math.round(temporalStartCube1Unix - resTarget/2);
		Date tempStartCube1date =  new java.util.Date(temporalStartCube1Unix*1000L);
		temporalStartCube1 = toDateNewFormat.format(tempStartCube1date);
		
		temporalEndCube1Unix = Math.round(temporalEndCube1Unix - resTarget/2);
		Date tempEndCube1date =  new java.util.Date(temporalEndCube1Unix*1000L);
		temporalEndCube1 = toDateNewFormat.format(tempEndCube1date);
		}
		else if (res>1) {
			temporalStartCube1Unix = Math.round(temporalStartCube1Unix + resTarget);
			Date tempStartCube1date =  new java.util.Date(temporalStartCube1Unix*1000L);
			temporalStartCube1 = toDateNewFormat.format(tempStartCube1date);
			
			temporalEndCube1Unix = Math.round(temporalEndCube1Unix + resTarget);
			Date tempEndCube1date =  new java.util.Date(temporalEndCube1Unix*1000L);
			temporalEndCube1 = toDateNewFormat.format(tempEndCube1date);
			}
		StringBuilder resampleBuilder = new StringBuilder("");
		if (!payload.contains("scale")) {
			resampleBuilder.append("scale(").append(payload.replaceAll(tempAxis + "\\(" + '"' + ".*?\\)", tempAxis + "\\(" + '"' + temporalStartCube1 + '"' + ":" + '"' + temporalEndCube1 + '"' + "\\)")).append(" ,{"
					+ tempAxis + "(" + '"' + tempLow + '"' + ":" + '"' + tempScale + '"' + ")" + ","
					+ xAxis + "(" + xLow + ":" + xHigh + ")" + ","
					+ yAxis + "(" + yLow + ":" + yHigh + ")" + ""
					+ "})");
//			resampleBuilder.append(" ,{"
//			+ xAxis + ":" +"\"CRS:" + wcps_endpoint + "/def/crs/EPSG/0/" + projectionEPSGCode + "\"" + "(" + xLow + ":" + xScale + ")" + ","
//			+ yAxis + ":" +"\"CRS:" + wcps_endpoint + "/def/crs/EPSG/0/" + projectionEPSGCode + "\"" + "(" + yLow + ":" + yScale + ")" + ""
//			+ "})");
		}
		else if (payload.contains("scale")) {
			log.debug(payload);
			log.debug(tempLow + " " +tempHigh);
			log.debug(temporalStartCube1 + " " + tempScale);			
			resampleBuilder.append(payload.replaceAll(tempAxis + "\\(" + '"' + ".*?\\)", tempAxis + "\\(" + '"' + temporalStartCube1 + '"' + ":" + '"' + temporalEndCube1 + '"' + "\\)").replaceAll("\\{" + tempAxis + "\\(.*?\\)", "\\{" + tempAxis + "\\(" + '"' + tempLow + '"' + ":" + '"' + tempScale + '"' + "\\)"));
		}
		return resampleBuilder.toString();
	}
	
	private String createResampleSpatialCubeWCPSString(String resampleNodeKey, String payload, String resSource, String resTarget, String xAxis, String xLow, String xHigh, String yAxis, String yLow, String yHigh, String tempAxis, String temporalStartCube1, String temporalEndCube1) {
		//TODO Remove the extra adding of half the resolution in the scale range for Dates when the Rasdaman fixes the issue of shifting
		double res = Double.parseDouble(resSource)/Double.parseDouble(resTarget);
		log.debug(xLow + " " + xHigh);
		log.debug(yLow + " " + yHigh);
		double xScale = (Double.parseDouble(xHigh)-Double.parseDouble(xLow))*res+Double.parseDouble(xLow);
		double yScale = (Double.parseDouble(yHigh)-Double.parseDouble(yLow))*res+Double.parseDouble(yLow);
		
		String xLow1 = null;
		String xHigh1 = null;
		String yLow1 = null;
		String yHigh1 = null;
		StringBuilder resampleBuilder = new StringBuilder("scale(" );
		if (res<1) {
			xLow1 = Double.toString(Double.parseDouble(xLow) - Double.parseDouble(resTarget)/2);
			yLow1 = Double.toString(Double.parseDouble(yLow) + Double.parseDouble(resTarget)/2);
			xHigh1 = Double.toString(Double.parseDouble(xHigh) - Double.parseDouble(resTarget)/2);
			yHigh1 = Double.toString(Double.parseDouble(yHigh) + Double.parseDouble(resTarget)/2);
		}
		else if (res>1) {
			xLow1 = Double.toString(Double.parseDouble(xLow) + Double.parseDouble(resSource));
			yLow1 = Double.toString(Double.parseDouble(yLow) - Double.parseDouble(resSource));
			xHigh1 = Double.toString(Double.parseDouble(xHigh) + Double.parseDouble(resSource));
			yHigh1 = Double.toString(Double.parseDouble(yHigh) - Double.parseDouble(resSource));
		}
		log.debug("Shifted X : " + xLow1 + " " + xHigh1);
		log.debug("Shifted Y : " + yLow1 + " " + yHigh1);
		if (temporalStartCube1.equals(temporalEndCube1)) {
//		try {
//			projectionEPSGCode = processGraph.getJSONObject(resampleNodeKey).getJSONObject("arguments").getInt("projection");
//		}catch(JSONException e) {
//			log.error("no epsg code was detected!");
//		}		
		//TODO read the name of the spatial coordinate axis from describeCoverage or filter elements in order to correctly apply (E,N), (lat,lon) or X,Y depending on coordinate system
		resampleBuilder.append(payload.replaceAll(xAxis + "\\(" + xLow + ":" + xHigh + "\\)",xAxis + "\\(" + xLow1 + ":" + xHigh1 + "\\)").replaceAll(yAxis + "\\(" + yLow + ":" + yHigh + "\\)",yAxis + "\\(" + yLow1 + "\\:" + yHigh1 + "\\)"));
		resampleBuilder.append(" ,{"
				+ xAxis + "(" + xLow + ":" + xScale + ")" + ","
				+ yAxis + "(" + yLow + ":" + yScale + ")" + ""
				+ "})");
//		resampleBuilder.append(" ,{"
//				+ xAxis + ":" +"\"CRS:" + wcps_endpoint + "/def/crs/EPSG/0/" + projectionEPSGCode + "\"" + "(" + xLow + ":" + xScale + ")" + ","
//				+ yAxis + ":" +"\"CRS:" + wcps_endpoint + "/def/crs/EPSG/0/" + projectionEPSGCode + "\"" + "(" + yLow + ":" + yScale + ")" + ""
//				+ "})");
		}
		else if (!temporalStartCube1.equals(temporalEndCube1)) {
//			try {
//				projectionEPSGCode = processGraph.getJSONObject(resampleNodeKey).getJSONObject("arguments").getInt("projection");
//			}catch(JSONException e) {
//				log.error("no epsg code was detected!");
//			}
			//TODO read the name of the spatial coordinate axis from describeCoverage or filter elements in order to correctly apply (E,N), (lat,lon) or X,Y depending on coordinate system
			
			resampleBuilder.append(payload.replaceAll(xAxis + "\\(" + xLow + ":" + xHigh + "\\)",xAxis + "\\(" + xLow1 + ":" + xHigh1 + "\\)").replaceAll(yAxis + "\\(" + yLow + ":" + yHigh + "\\)",yAxis + "\\(" + yLow1 + ":" + yHigh1 + "\\)"));
			resampleBuilder.append(" ,{"
					+ tempAxis + "(" + '"' + temporalStartCube1 + '"' + ":" + '"' + temporalEndCube1 + '"' + ")" + ","
					+ xAxis + "(" + xLow + ":" + xScale + ")" + ","
					+ yAxis + "(" + yLow + ":" + yScale + ")" + ""
					+ "})");			
//			resampleBuilder.append(" ,{"
//			+ xAxis + ":" +"\"CRS:" + wcps_endpoint + "/def/crs/EPSG/0/" + projectionEPSGCode + "\"" + "(" + xLow + ":" + xScale + ")" + ","
//			+ yAxis + ":" +"\"CRS:" + wcps_endpoint + "/def/crs/EPSG/0/" + projectionEPSGCode + "\"" + "(" + yLow + ":" + yScale + ")" + ""
//			+ "})");			
			
		}
		return resampleBuilder.toString();
	}

	private String createLinearScaleCubeWCPSString(String linearScaleNodeKey, String payLoad) {
		JSONObject scaleArgumets = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments");
		double inputMin = 0;
		double inputMax = 0;
		double outputMin = 0;
		double outputMax = 1;
		inputMin = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("inputMin");
		inputMax = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("inputMax");

		for (String outputMinMax : scaleArgumets.keySet()) {
			if (outputMinMax.contentEquals("outputMin")) {
				outputMin = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("outputMin");		       
			}
			else if (outputMinMax.contentEquals("outputMax")) {
				outputMax = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("outputMax");
			}		
		}

		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(payLoad + ")");
		String stretchString = stretchBuilder.toString();
		StringBuilder stretchBuilderExtend = new StringBuilder("(unsigned char)(");
		stretchBuilderExtend.append("(" + stretchString + " + " + (-inputMin) + ")");
		stretchBuilderExtend.append("*("+ outputMax + "/" + (inputMax - inputMin) + ")");
		stretchBuilderExtend.append(" + " + outputMin + ")");

		return stretchBuilderExtend.toString();
	}

	private String createLinearStretchCubeWCPSString(String linearScaleNodeKey, String payLoad) {
		double min = 0;
		double max = 1;
		JSONObject scaleArgumets = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments");

		for (String outputMinMax : scaleArgumets.keySet()) {
			if (outputMinMax.contentEquals("min")) {
				min = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("min");		       
			}
			else if (outputMinMax.contentEquals("max")) {
				max = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("max");
			}
		}

		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(payLoad + ")");
		String stretchString = stretchBuilder.toString();
		String stretch1 = stretchString.replace("$pm", "$pm1");
		String stretch2 = stretchString.replace("$pm", "$pm2");
		String stretch3 = stretchString.replace("$pm", "$pm3");
		String stretch4 = stretchString.replace("$pm", "$pm4");
		StringBuilder stretchBuilderExtend = new StringBuilder("(unsigned char)(");
		stretchBuilderExtend.append(stretch1 + " - " + "min" + stretch2 + ")*((" + max + "-" + min + ")" + "/(max" + stretch3 + "-min" + stretch4 + ")) + 0");

		return stretchBuilderExtend.toString();
	}

	/**
	 * Helper Method to create a string describing an arbitrary filtering as defined
	 * from the process graph
	 * 
	 * @param collectionName
	 * @return
	 */
	private String createFilteredCollectionString(String collectionVar, String collectionID) {
		StringBuilder stringBuilder = new StringBuilder(collectionVar);
		stringBuilder.append("[");
		int noOfFilters = 0;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();			
			if(axis.contains(collectionID)) {
				noOfFilters = noOfFilters+1;
			}
		}
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();
			
			if(axis.contains(collectionID)) {
				axis = axis.replace("_"+ collectionID, "");
				String axisUpperCase = axis.toUpperCase();
				String low = filter.getLowerBound();
				String high = filter.getUpperBound();
				stringBuilder.append(" " + axis + "(");
				if ((axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) && !low.contains("$cube")) {
					stringBuilder.append("\"");
				}
				stringBuilder.append(low);
				if ((axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) && !low.contains("$cube")) {
					stringBuilder.append("\"");
				}
				if (high != null && !(high.equals(low))) {
					stringBuilder.append(":");
					if (axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) {
						stringBuilder.append("\"");
					}
					stringBuilder.append(high);
					if (axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) {
						stringBuilder.append("\"");
					}
				}
				stringBuilder.append(")");
				noOfFilters = noOfFilters-1;
				if (noOfFilters > 0) {
					stringBuilder.append(",");
				}
			}
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}	
	
	/**
	 * Helper Method to create a string describing a single dimension filter as
	 * defined from the process graph
	 * 
	 * @param collectionName
	 * @return
	 */
	private String createFilteredCollectionString(String collectionVar, String collectionID, Filter filter) {
		try {
			StringBuilder stringBuilder = new StringBuilder(collectionVar);
			stringBuilder.append("[");
			String axis = filter.getAxis();
			if(axis.contains(collectionID)) {
			axis = axis.replace("_"+ collectionID, "");
			String axisUpperCase = axis.toUpperCase();
			String low = filter.getLowerBound();
			String toDate = filter.getUpperBound();
//			DateFormat toDateNewFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//			Date toDateNew;			
//			try {
//				toDateNew = toDateNewFormat.parse(toDate);
//				toDateNew.setTime(toDateNew.getTime() - 1);
//				toDate = toDateNewFormat.format(toDateNew);
//				log.debug("To Date"+toDate);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			String high = toDate;
			stringBuilder.append(" " + axis + "(");
			if ((axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) && !low.contains("$cube")) {
				stringBuilder.append("\"");
			}
			stringBuilder.append(low);
			if ((axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) && !low.contains("$cube")) {
				stringBuilder.append("\"");
			}
			if (high != null && !(high.equals(low))) {
				stringBuilder.append(":");
				if (axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) {
					stringBuilder.append("\"");
				}
				stringBuilder.append(high);
				if (axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) {
					stringBuilder.append("\"");
				}
			}
			stringBuilder.append(")");
		}
			stringBuilder.append("]");
			return stringBuilder.toString();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private String createNDVIWCPSString(String filterString, String collectionName, Aggregate ndviAggregate) {
		String redBandName = ndviAggregate.getParams().get(0);
		String nirBandName = ndviAggregate.getParams().get(1);
		filterString = filterString.substring(collectionName.length());
		String red = createBandSubsetString(collectionName, redBandName, filterString);
		String nir = createBandSubsetString(collectionName, nirBandName, filterString);
		StringBuilder stringBuilder = new StringBuilder("((double)");
		stringBuilder.append(nir + " - " + red);
		stringBuilder.append(") / ((double)");
		stringBuilder.append(nir + " + " + red);
		stringBuilder.append(")");
		//filters.removeAllElements();
		return stringBuilder.toString();
	}

	private String createTempAggWCPSString(String reduceNodeKey, String collectionVar, String collectionID, Aggregate tempAggregate, String tempAxis) {
		String operator = tempAggregate.getOperator();
		Filter tempFilter = null;
		for (Filter filter : this.filters) {
			log.debug("Filter Axis is : ");
			log.debug(filter.getAxis());
			log.debug("Collection ID is : ");
			log.debug(collectionID);
			String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
			if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
				tempFilter = filter;
				log.debug("TempHigh"+tempFilter.getUpperBound());
				log.debug("Temporal Axis is : ");
				log.debug(tempFilter);
			}
		}
		log.debug("Filters are : ");
		log.debug(filters);
		log.debug("Temporal filter is : ");
		log.debug(tempFilter);
		if (tempFilter != null) {
			StringBuilder stringBuilder = new StringBuilder("condense ");
			stringBuilder.append(operator + " over $pm" + reduceNodeKey + " t (imageCrsDomain(");
			stringBuilder.append(createFilteredCollectionString(collectionVar, collectionID, tempFilter) + ",");
			stringBuilder.append(tempAxis + ")) using ");
			//this.filters.remove(tempFilter);
			//this.filters.add(new Filter(axis, "$pm"));
			return stringBuilder.toString();
		} else {
			for (Filter filter : this.filters) {
				System.err.println(filter.getAxis());
			}
			// TODO this error needs to be communicated to end user
			// meaning no appropriate filter found for running the condense operator in
			// temporal axis.
			return "";
		}
	}
	
	private String createBandSubsetString(String collectionName, String bandName, String subsetString) {
		StringBuilder stringBuilder = new StringBuilder(collectionName);
		stringBuilder.append(subsetString);
		stringBuilder.append(".");
		stringBuilder.append(bandName);		
		return stringBuilder.toString();
	}
	
	// TODO Check this function if its complete and correct ?
	private void createPolygonFilter(JSONObject argsObject, int srs, String coll) {
		String wcps_endpoint = openEOEndpoint;
		
		double polygonArrayLong = 0;
		double polygonArrayLat = 0;

		if (argsObject.getString("type").equals("Polygon")) {
			for (Object argsKey : argsObject.keySet()) {
				String argsKeyStr = (String) argsKey;
				if (argsKeyStr.equals("coordinates")) {
					JSONArray polygonArray = argsObject.getJSONArray(argsKeyStr).getJSONArray(0);
					for (int a = 0; a < polygonArray.length(); a++) {
						polygonArrayLong = polygonArray.getJSONArray(a).getDouble(0);
						polygonArrayLat = polygonArray.getJSONArray(a).getDouble(0);
						Collection collection = collectionMap.get(coll);
						
						double westlower = collection.getExtent().getSpatial().getBbox().get(0).get(0).doubleValue();
						double eastupper = collection.getExtent().getSpatial().getBbox().get(0).get(2).doubleValue();
						double southlower = collection.getExtent().getSpatial().getBbox().get(0).get(1).doubleValue();
						double northupper = collection.getExtent().getSpatial().getBbox().get(0).get(3).doubleValue();

						if (polygonArrayLong < westlower) {
							polygonArrayLong = westlower;
						}

						if (polygonArrayLong > eastupper) {
							polygonArrayLong = eastupper;
						}

						if (polygonArrayLat > northupper) {
							polygonArrayLat = northupper;
						}

						if (polygonArrayLat < southlower) {
							polygonArrayLat = southlower;
						}

						SpatialReference src = new SpatialReference();
						src.ImportFromEPSG(4326);
						SpatialReference dst = new SpatialReference();
						dst.ImportFromEPSG(srs);				

						CoordinateTransformation tx = new CoordinateTransformation(src, dst);
						double[] c1 = null;				
						c1 = tx.TransformPoint(polygonArrayLat, polygonArrayLong);

						polygonArrayLong = c1[0];
						polygonArrayLat = c1[1];

						log.debug("Polygon Long : ");
						log.debug(polygonArrayLat);
						log.debug("Polygon Lat : ");
						log.debug(polygonArrayLat);
						this.filtersPolygon.add(new Filter("Poly"+a, Double.toString(polygonArrayLong), Double.toString(polygonArrayLat)));
					}
				}
			}
		}
	}

	/**
	 * returns constructed query as String object
	 * 
	 * @return String WCPS query
	 */
	public String getWCPSString() {
		log.debug("The following WCPS query was requested: ");
		log.debug(wcpsStringBuilder.toString());
		return wcpsStringBuilder.toString();
	}

	// Get the Node key of the save_result process
	private String getSaveNode() {
		for (String processNodeKey : processGraph.keySet()) {			
			JSONObject processNode = processGraph.getJSONObject(processNodeKey);
			String processID = processNode.getString("process_id");
			if (processID.equals("save_result")) {				
				log.debug("Save Result Process Node key found is: " + processNodeKey);
				String format = getFormatFromSaveResultNode(processNode);
				try {
					//this.outputFormat = ConvenienceHelper.getMimeTypeFromOutput(format);
					this.outputFormat = ConvenienceHelper.getRasTypeFromOutput(format);
				} catch (JSONException | IOException e) {
					log.error("Error while parsing outputformat from process graph: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for( StackTraceElement element: e.getStackTrace()) {
						builder.append(element.toString()+"\n");
					}
					log.error(builder.toString());
				}
				return processNodeKey;
			}
		}
		return null;
	}

	// Always add the new parameter name added in 'arguments' field if recently defined by openEO API for some process
	private JSONArray getFromNodeOfCurrentKey(String currentNode){
		JSONObject nextNodeName = new JSONObject();
		JSONArray fromNodes = new JSONArray();
		String nextFromNode = null;
		JSONObject currentNodeProcessArguments =  processGraph.getJSONObject(currentNode).getJSONObject("arguments");
		for (String argumentsKey : currentNodeProcessArguments.keySet()) {
			if (argumentsKey.contentEquals("data")) {
				if (currentNodeProcessArguments.get("data") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("data").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				else if (currentNodeProcessArguments.get("data") instanceof JSONArray) {
					JSONArray reduceData = currentNodeProcessArguments.getJSONArray("data");
					for(int a = 0; a < reduceData.length(); a++) {
						if (reduceData.get(a) instanceof JSONObject) {
							for (String fromKey : reduceData.getJSONObject(a).keySet()) {
								if (fromKey.contentEquals("from_node")) {
									nextFromNode = reduceData.getJSONObject(a).getString("from_node");
									fromNodes.put(nextFromNode);
								}
							}
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("mask")) {
				if (currentNodeProcessArguments.get("mask") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("mask").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("mask").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			
			else if (argumentsKey.contentEquals("accept")) {
				if (currentNodeProcessArguments.get("accept") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("accept").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("accept").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			
			else if (argumentsKey.contentEquals("reject")) {
				if (currentNodeProcessArguments.get("reject") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("reject").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("reject").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			
			else if (argumentsKey.contentEquals("value")) {
				if (currentNodeProcessArguments.get("value") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("value").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("value").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("x")) {
				if (currentNodeProcessArguments.get("x") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("x").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("x").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("y")) {
				if (currentNodeProcessArguments.get("y") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("y").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("y").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("cube1")) {
				if (currentNodeProcessArguments.get("cube1") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("cube1").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("cube1").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("cube2")) {
				if (currentNodeProcessArguments.get("cube2") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("cube2").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("cube2").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
		}
		return fromNodes;		
	}

	// Get output format of save_result Process
	private String getFormatFromSaveResultNode(JSONObject saveResultNode) {
		JSONObject saveResultArguments = saveResultNode.getJSONObject("arguments");
		String format = saveResultArguments.getString("format");
		return format;
	}
	
	// Execute the sort order independent processes to create filters and aggregates
	private void executeProcesses(String processID, String processNodeKey) {
		JSONObject processNode = processGraph.getJSONObject(processNodeKey);
		if (processID.equals("load_collection")) {
			String collectionID = null;
			JSONObject loadCollectionNode = processGraph.getJSONObject(processNodeKey);
			JSONObject loadCollectionNodeArguments = loadCollectionNode.getJSONObject("arguments");			
			collectionID = (String) loadCollectionNodeArguments.get("id");
			Collection col = new Collection();
			col.setId(processNodeKey);
			collectionIDs.add(col);
			Collection collection = collectionMap.get(collectionID);
			
			int srs = getSRSFromCollection(collection);
			
			JSONArray processDataCubeTempExt = new JSONArray();
			JSONObject spatialExtentNode = new JSONObject();
			createDateRangeFilterFromArgs(processDataCubeTempExt, collectionID, true);
			createBoundingBoxFilterFromArgs(loadCollectionNodeArguments, srs, collectionID, true);
			
			for (String argumentKey : loadCollectionNodeArguments.keySet()) {
				if (argumentKey.equals("spatial_extent")) {
					if (!loadCollectionNodeArguments.isNull(argumentKey)) {
						spatialExtentNode = loadCollectionNodeArguments.getJSONObject("spatial_extent");
						log.debug("Currently working on Spatial Extent: ");
						log.debug(spatialExtentNode.toString(4));
						createBoundingBoxFilterFromArgs(loadCollectionNodeArguments, srs, collectionID, false);
					}
				}
				if (argumentKey.equals("temporal_extent")) {
					if (!loadCollectionNodeArguments.isNull(argumentKey)) {
						processDataCubeTempExt = (JSONArray) loadCollectionNodeArguments.get("temporal_extent");					
						log.debug("Currently working on Temporal Extent: ");
						log.debug(processDataCubeTempExt.toString(4));
						createDateRangeFilterFromArgs(processDataCubeTempExt, collectionID, false);
					}
				}
			}
		}	
		// Convenience functions from old code
		else if (processID.contains("_time")) {
			log.debug(processNode);
			String fromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");
			String collectionNodeKey = getFilterCollectionNode(fromNode);
			String collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
			createTemporalAggregate(processID, collectionID, processNodeKey);
		}
		else if (processID.contains("reduce_dimension")) {
			String dimension = processNode.getJSONObject("arguments").getString("dimension");
			String fromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");
			String collectionNodeKey = getFilterCollectionNode(fromNode);
			String collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
			Collection collection = collectionMap.get(collectionID);
			String temporalAxis = null;
			//TODO check if this can further be simplified ...
			for (String tempAxis1 : collection.getCubeColonDimensions().keySet()) {
				String tempAxis1UpperCase = tempAxis1.toUpperCase();
				if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
					temporalAxis = tempAxis1;
				}
			}
			if (dimension.equals("t") || dimension.equals("temporal") || dimension.contentEquals(temporalAxis)) {
				JSONObject reducer = processNode.getJSONObject("arguments").getJSONObject("reducer").getJSONObject("process_graph");
				for (String nodeKey : reducer.keySet()) {					
					String processName = reducer.getJSONObject(nodeKey).getString("process_id");
					createReduceTemporalAggregate(processName, collectionID, processNodeKey);
				}
			}
		}
		// Convenience functions from old code
		else if (processID.equals("ndvi")) {
			JSONObject processAggregate = processGraph.getJSONObject(processNodeKey);			    
			String collectionNode = getFilterCollectionNode(processNodeKey);
			String collection = processGraph.getJSONObject(collectionNode).getJSONObject("arguments").getString("id");			
			createNDVIAggregateFromProcess(processAggregate, collection);
		}
		else if (processID.equals("filter_temporal")) {
			String filterCollectionNodeKey = null;
			String filterTempNodeKey = processNodeKey;
			String filterTempfromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");			
			filterCollectionNodeKey = getFilterCollectionNode(filterTempfromNode);
			JSONObject loadCollectionNode = processGraph.getJSONObject(filterCollectionNodeKey).getJSONObject("arguments");
			String coll = (String) loadCollectionNode.get("id");
			JSONObject processFilter = processGraph.getJSONObject(filterTempNodeKey);
			JSONObject processFilterArguments = processFilter.getJSONObject("arguments");
			JSONArray extentArray = new JSONArray();			
			extentArray = (JSONArray) processFilterArguments.get("extent");
			createDateRangeFilterFromArgs(extentArray, coll, false);
		}
		else if (processID.equals("filter_bbox")) {
			String filterCollectionNodeKey = null;
			String filterBboxNodeKey = processNodeKey;
			String filterBboxfromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");			
			filterCollectionNodeKey = getFilterCollectionNode(filterBboxfromNode);
			JSONObject loadCollectionNode = processGraph.getJSONObject(filterCollectionNodeKey).getJSONObject("arguments");			
			String collectionID = (String) loadCollectionNode.get("id");
			JSONObject processFilter = processGraph.getJSONObject(filterBboxNodeKey);
			JSONObject processFilterArguments = processFilter.getJSONObject("arguments");
			Collection collection = collectionMap.get(collectionID);
			
			int srs = getSRSFromCollection(collection);
			if (srs > 0) {
				createBoundingBoxFilterFromArgs(processFilterArguments, srs, collectionID, false);
			}
		}		
//		else if (processID.equals("filter_polygon")) {
//			String filterCollectionNodeKey = null;
//			String filterPolygonNodeKey = processNodeKey;
//			String filterPolygonfromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");			
//			filterCollectionNodeKey = getFilterCollectionNode(filterPolygonfromNode);
//			JSONObject loadCollectionNode = processGraph.getJSONObject(filterCollectionNodeKey).getJSONObject("arguments");			
//			String coll = (String) loadCollectionNode.get("id");
//			JSONObject processFilter = processGraph.getJSONObject(filterPolygonNodeKey);
//			JSONObject processFilterArguments = processFilter.getJSONObject("arguments").getJSONObject("polygons");
//			int srs = 0;
//			JSONObject jsonresp = null;
//			try {
//				jsonresp = readJsonFromUrl(openEOEndpoint + "/collections/" + coll);
//			} catch (JSONException e) {
//				log.error("An error occured: " + e.getMessage());
//				StringBuilder builder = new StringBuilder();
//				for (StackTraceElement element : e.getStackTrace()) {
//					builder.append(element.toString() + "\n");
//				}
//				log.error(builder.toString());
//			} catch (IOException e) {
//				log.error("An error occured: " + e.getMessage());
//				StringBuilder builder = new StringBuilder();
//				for (StackTraceElement element : e.getStackTrace()) {
//					builder.append(element.toString() + "\n");
//				}
//				log.error(builder.toString());
//			}
//			for (String dimX : jsonresp.getJSONObject("cube:dimensions").keySet()) {
//				if (dimX.contentEquals("X") || dimX.contentEquals("E") || dimX.contentEquals("Lon") || dimX.contentEquals("Long")) {
//					srs = ((JSONObject) jsonresp.getJSONObject("cube:dimensions")).getJSONObject(dimX).getInt("reference_system");
//				}
//			}			
//			if (srs > 0) {
//				log.debug("Polygon Extent is : " + processFilterArguments.getJSONArray("coordinates"));
//				createPolygonFilter(processFilterArguments, srs, coll);
//				log.debug("Polygon Filters are : ");
//				log.debug(filtersPolygon);
//			}
//		}
	}

	// Get load_collection node of current process node
	private String getFilterCollectionNode(String fromNode) {
		String filterCollectionNodeKey = null;		
		JSONObject loadCollectionNodeKeyArguments = processGraph.getJSONObject(fromNode).getJSONObject("arguments");
		for (String argumentsKey : loadCollectionNodeKeyArguments.keySet()) {
				if (argumentsKey.contentEquals("id")) {
					filterCollectionNodeKey = fromNode;
				}
				else if (argumentsKey.contentEquals("data")) {
					String filterfromNode = loadCollectionNodeKeyArguments.getJSONObject("data").getString("from_node");					
					filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
				}
				else if (argumentsKey.contentEquals("cube1")) {
					String filterfromNode = loadCollectionNodeKeyArguments.getJSONObject("cube1").getString("from_node");					
					filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
				}
				else if (argumentsKey.contentEquals("x")) {
					String filterfromNode = loadCollectionNodeKeyArguments.getJSONObject("x").getString("from_node");					
					filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
				}
				else if (argumentsKey.contentEquals("value")) {
					String filterfromNode = loadCollectionNodeKeyArguments.getJSONObject("value").getString("from_node");					
					filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
				}
			}		
		return filterCollectionNodeKey;
	}
	
	private void createDateRangeFilterFromArgs(JSONArray extentArray, String collectionID, Boolean tempNull) {
		String fromDate = null;
		String toDate = null;
		JSONObject extent;
		String wcps_endpoint = openEOEndpoint;
		
		if (tempNull) {
			JSONObject jsonresp = null;
			Collection collection = collectionMap.get(collectionID);
			DimensionTemporal temporalDimension = null;
			for(Dimension currentDimension: collection.getCubeColonDimensions().values()) {
				if(currentDimension.getType() == Dimension.TypeEnum.TEMPORAL) {
					temporalDimension = (DimensionTemporal) currentDimension;
				}
			}
			String templower = null;
			String tempupper = null;
			try {
//				templower = collection.getCubeColonDimensions().get(0).toString();
				templower = temporalDimension.getExtent().get(0).toString();
				tempupper = temporalDimension.getExtent().get(1).toString();
				log.debug(" TempUpper : "+ tempupper + " TempLower : " + templower);
				if (templower.contentEquals("null")) {
					templower = null;
				}
				if (tempupper.contentEquals("null")) {
					tempupper = null;
				}
			}			
			catch (JSONException e) {
				log.error("An error occured: " + e.getMessage());
			}
			log.debug("Temporal Extent is: ");			
			
			if ((templower != null && tempupper != null)) {
				log.debug("Temporal Extent is: |" + templower + "|:|" + tempupper + "|");
				if(LocalDateTime.parse(templower.replace("Z", "")).equals(LocalDateTime.parse(tempupper.replace("Z", "")))) {
					tempupper = null;
					log.debug("Dates are identical. To date is set to null!");
				}
				Filter dateFilter = null;
				List<SimpleDateFormat> knownPatterns = new ArrayList<SimpleDateFormat>();
				knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'"));
				knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
				knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd"));
				Date toDateNew;
				if (tempupper != null) {
					for (SimpleDateFormat pattern : knownPatterns) {
					    try {
					    	toDateNew = pattern.parse(tempupper);
							toDateNew.setTime(toDateNew.getTime() - 1);
							tempupper = pattern.format(toDateNew);
							log.debug("To Date :"+tempupper);

					    } catch (ParseException pe) {
					    	log.error("couldn't parse date: " + pe.getMessage());
					    }
					}
				}
				for (Filter filter : this.filters) {
					String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
					if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
						dateFilter = filter;
					}
				}
				this.filters.remove(dateFilter);
				String tempAxis = null;
				for (String tempAxis1 : jsonresp.getJSONObject("cube:dimensions").keySet()) {
					String tempAxis1UpperCase = tempAxis1.toUpperCase();
					if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
						tempAxis = tempAxis1;
					}
				}
				this.filters.add(new Filter(tempAxis+"_"+collectionID, templower, tempupper));				
			}
		}
		
		else {
			String extentlower = extentArray.get(0).toString();
			String extentupper = extentArray.get(1).toString();

			Collection collection = collectionMap.get(collectionID);
			
			List<OffsetDateTime> temporal = collection.getExtent().getTemporal().getInterval().get(0);
			String templower = temporal.get(0).toString();
			String tempupper = temporal.get(1).toString();
			log.debug("Temporal Extent is: ");
			log.debug(temporal);
			if (extentlower.compareTo(templower) < 0) {
				fromDate = temporal.get(0).toString();
				if (fromDate.contentEquals("null")) {
					fromDate = null;
				}
			}
			else {
				fromDate = extentArray.get(0).toString();
			}
			if (extentupper.compareTo(tempupper) > 0) {
				toDate = temporal.get(1).toString();
				if (toDate.contentEquals("null")) {
					toDate = null;
				}
			}
			else {
				toDate = extentArray.get(1).toString();
			}
			if (fromDate != null && toDate != null) {
				log.debug("Temporal Extent is: |" + fromDate + "|:|" + toDate + "|");
				//TODO make sure that the parsing here is not causing date time exception!
				if(LocalDateTime.parse(fromDate.replace("Z", "")).equals(LocalDateTime.parse(toDate.replace("Z", "")))) {
					toDate = null;
					log.debug("Dates are identical. To date is set to null!");
				}
				Filter dateFilter = null;
//				DateFormat toDateNewFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//				Date toDateNew;
//				if (toDate != null) {
//				try {
//					toDateNew = toDateNewFormat.parse(toDate);
//					toDateNew.setTime(toDateNew.getTime() - 1);
//					toDate = toDateNewFormat.format(toDateNew);
//					log.debug("To Date :"+toDate);
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				}
				for (Filter filter : this.filters) {
					String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
					if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
						dateFilter = filter;
					}
				}
				this.filters.remove(dateFilter);
				String temporalDimensionName = null;
				for(String dimensionName: collection.getCubeColonDimensions().keySet()) {
					Dimension currentDimension = collection.getCubeColonDimensions().get(dimensionName);
					if(currentDimension.getType() == Dimension.TypeEnum.TEMPORAL) {
						temporalDimensionName = dimensionName;
					}
				}
				log.debug("To Date : "+toDate);
				this.filters.add(new Filter(temporalDimensionName+"_"+collectionID, fromDate, toDate));
				
			}
		}
	}

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

//	private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
//		log.debug("Trying to read JSON from the following URL : ");
//		log.debug(url);
//		InputStream is = new URL(url).openStream();
//		try {
//			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//			String jsonText = readAll(rd);
//			JSONObject json = new JSONObject(jsonText);
//			return json;
//		} finally {
//			is.close();
//		}
//	}

	private void createBoundingBoxFilterFromArgs(JSONObject argsObject, int srs, String collectionID, Boolean spatNull) {
		String left = null;
		String right = null;
		String top = null;
		String bottom = null;
		double resSource = 0;
		URL url;
		String wcps_endpoint = openEOEndpoint;
		
		try {
			url = new URL(wcpsEndpoint
					+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionID);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
			}					
			log.debug("root node info: " + rootNode.getName());

			Boolean bandsMeta = false;
			Element metadataElement = null;
			try {
				metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS).getChild("Extension", gmlNS).getChild("covMetadata", gmlNS);
			}catch(Exception e) {
			}
			List<Element> bandsList = null;
			try {
				bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
				bandsMeta = true;
			}catch(Exception e) {
			}
			if (bandsMeta) {
				try {
					for(int c = 0; c < bandsList.size(); c++) {						
						Element band = bandsList.get(c);					
						try {
							resSource = Double.parseDouble(band.getChildText("gsd"));
						}catch(Exception e) {
						}
					}
				}catch(Exception e) {
				}
			}
		}
		catch (MalformedURLException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());		
		} catch (IOException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());		
		} catch (JDOMException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());		
		}
		if (spatNull) {
			
			Collection collection = collectionMap.get(collectionID);
			
			double westlower = collection.getExtent().getSpatial().getBbox().get(0).get(0).doubleValue()+0.00001;
			double eastupper = collection.getExtent().getSpatial().getBbox().get(0).get(2).doubleValue()-0.00001;
			double southlower = collection.getExtent().getSpatial().getBbox().get(0).get(1).doubleValue()+0.00001;;
			double northupper = collection.getExtent().getSpatial().getBbox().get(0).get(3).doubleValue()-0.00001;
			left = Double.toString(westlower);
			right = Double.toString(eastupper);
			top = Double.toString(northupper);
			bottom = Double.toString(southlower).toString();
			SpatialReference src = new SpatialReference();
			src.ImportFromEPSG(4326);
			SpatialReference dst = new SpatialReference();
			dst.ImportFromEPSG(srs);
			log.debug("SRS is :" + srs);			
			CoordinateTransformation tx = new CoordinateTransformation(src, dst);
			double[] c1 = null;
			double[] c2 = null;
			c1 = tx.TransformPoint(Double.parseDouble(bottom), Double.parseDouble(left));
			c2 = tx.TransformPoint(Double.parseDouble(top), Double.parseDouble(right));
			if (srs==3035) {
				left = Double.toString(c1[1]);
				bottom = Double.toString(c1[0]);
				right = Double.toString(c2[1]);
				top = Double.toString(c2[0]);
			}
			else {
				left = Double.toString(c1[0]);
				bottom = Double.toString(c1[1]);
				right = Double.toString(c2[0]);
				top = Double.toString(c2[1]);
			}
			if (resSource!=0) {
				try {
					url = new URL(wcpsEndpoint
							+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionID);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					SAXBuilder builder = new SAXBuilder();
					Document capabilititesDoc = builder.build(conn.getInputStream());
					List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
					Element rootNode = capabilititesDoc.getRootElement();
					Namespace defaultNS = rootNode.getNamespace();
					Namespace gmlNS = null;
					for (int n = 0; n < namespaces.size(); n++) {
						Namespace current = namespaces.get(n);
						if(current.getPrefix().equals("gml")) {
							gmlNS = current;
						}
					}					
					log.debug("root node info: " + rootNode.getName());
							
					Element coverageDescElement = rootNode.getChild("CoverageDescription", defaultNS);
					Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
					Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
					String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
					String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");
					String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
					int xIndex = 0;
				    int yIndex = 0;
					
					for(int a = 0; a < axis.length; a++) {
				    	log.debug(axis[a]);
						if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long")){
							xIndex = a;
						}
						if(axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
							yIndex = a;
						}
					}
					log.debug("Left: "+left);
					log.debug("Right: "+right);
					log.debug("Bottom: "+bottom);
					log.debug("Top: "+top);
					log.debug("MinX: "+minValues[xIndex]);
					log.debug("MaxX: "+maxValues[xIndex]);
					log.debug("MinY: "+minValues[yIndex]);
					log.debug("MaxY: "+maxValues[yIndex]);
					left = Double.toString(Double.parseDouble(minValues[xIndex]) + resSource*( Math.round(((Double.parseDouble(left) - Double.parseDouble(minValues[xIndex]))/resSource))));
					right = Double.toString(Double.parseDouble(maxValues[xIndex]) - resSource*(Math.round(((Double.parseDouble(maxValues[xIndex]) - Double.parseDouble(right))/resSource))));
					bottom = Double.toString(Double.parseDouble(minValues[yIndex]) + resSource*(Math.round(((Double.parseDouble(bottom) - Double.parseDouble(minValues[yIndex]))/resSource))));
					top = Double.toString(Double.parseDouble(maxValues[yIndex]) - resSource*(Math.round(((Double.parseDouble(maxValues[yIndex]) - Double.parseDouble(top))/resSource))));					
			}
			catch (MalformedURLException e) {
				log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());
				
			} catch (IOException e) {
				log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());
				
			} catch (JDOMException e) {
				log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
				
			}
			}
			log.debug("WEST: "+left);
			log.debug("SOUTH: "+bottom);
			log.debug("EAST: "+right);
			log.debug("NORTH: "+top);
			String spatAxisX = null;
			String spatAxisY = null;			
			if (left != null && right != null && top != null && bottom != null) {
				Filter eastFilter = null;
				Filter westFilter = null;
				for (Filter filter : this.filters) {
					String axis = filter.getAxis();
					String axisUpperCase = axis.replace("_"+ collectionID, "").toUpperCase();
					if (axisUpperCase.equals("E") || axisUpperCase.equals("LONG") || axisUpperCase.equals("LON") || axisUpperCase.equals("LONGITUDE") || axisUpperCase.equals("X")) {
						eastFilter = filter;
					}
					else if (axisUpperCase.equals("N") || axisUpperCase.equals("LAT") || axisUpperCase.equals("LATITUDE") || axisUpperCase.equals("Y")) {
						westFilter = filter;
					}
				}
				this.filters.remove(eastFilter);
				this.filters.remove(westFilter);
				for (String spatAxis : collection.getCubeColonDimensions().keySet()) {	
					String spatAxisUpperCase = spatAxis.toUpperCase();
					if (spatAxisUpperCase.contentEquals("E") || spatAxisUpperCase.contentEquals("LONG") || spatAxisUpperCase.equals("LON") || spatAxisUpperCase.equals("LONGITUDE") || spatAxisUpperCase.contentEquals("X")) {
						spatAxisX = spatAxis;
					}
					else if (spatAxisUpperCase.contentEquals("N") || spatAxisUpperCase.contentEquals("LAT") || spatAxisUpperCase.equals("LATITUDE") || spatAxisUpperCase.contentEquals("Y")) {
						spatAxisY = spatAxis;
					}
				}
				this.filters.add(new Filter(spatAxisX+"_"+collectionID, left, right));
				this.filters.add(new Filter(spatAxisY+"_"+collectionID, bottom, top));
			} else {
				log.error("No spatial information could be found in process!");
			}
		}
		else {
//			JSONObject jsonresp = null;
			Collection collection = collectionMap.get(collectionID);
			String spatAxisX = null;
			String spatAxisY = null;
			for (Object argsKey : argsObject.keySet()) {
				String argsKeyStr = (String) argsKey;
				if (argsKeyStr.equals("extent") || argsKeyStr.equals("spatial_extent")) {
					JSONObject extentObject = (JSONObject) argsObject.get(argsKeyStr);
					for (Object extentKey : extentObject.keySet()) {
						String extentKeyStr = extentKey.toString();

						double westlower = collection.getExtent().getSpatial().getBbox().get(0).get(0).doubleValue();
						double eastupper = collection.getExtent().getSpatial().getBbox().get(0).get(2).doubleValue();
						double southlower = collection.getExtent().getSpatial().getBbox().get(0).get(1).doubleValue();
						double northupper = collection.getExtent().getSpatial().getBbox().get(0).get(3).doubleValue();
						
						double leftlower = 0;
						double rightupper = 0;
						double topupper = 0;
						double bottomlower = 0;

						if (extentKeyStr.equals("west")) {
							left = "" + extentObject.get(extentKeyStr).toString();
							leftlower = Double.parseDouble(left);
							if (leftlower < westlower) {
								left = Double.toString(westlower);
							}
						} else if (extentKeyStr.equals("east")) {
							right = "" + extentObject.get(extentKeyStr).toString();
							rightupper = Double.parseDouble(right);
							if (rightupper > eastupper) {							
								right = Double.toString(eastupper);
							}
						} else if (extentKeyStr.equals("north")) {
							top = "" + extentObject.get(extentKeyStr).toString();
							topupper = Double.parseDouble(top);
							if (topupper > northupper) {							
								top = Double.toString(northupper);
							}
						} else if (extentKeyStr.equals("south")) {
							bottom = "" + extentObject.get(extentKeyStr);
							bottomlower = Double.parseDouble(bottom);
							if (bottomlower < southlower) {							
								bottom = Double.toString(southlower);
							}
						}
					}
					SpatialReference src = new SpatialReference();
					src.ImportFromEPSG(4326);
					SpatialReference dst = new SpatialReference();
					dst.ImportFromEPSG(srs);
					log.debug("SRS is : " + srs);
					CoordinateTransformation tx = new CoordinateTransformation(src, dst);
					double[] c1 = null;
					double[] c2 = null;
					c1 = tx.TransformPoint(Double.parseDouble(bottom), Double.parseDouble(left));
					c2 = tx.TransformPoint(Double.parseDouble(top), Double.parseDouble(right));
					//TODO include other CRS exceptions of different axis order
					if (srs==3035) {
						left = Double.toString(c1[1]);
						bottom = Double.toString(c1[0]);
						right = Double.toString(c2[1]);
						top = Double.toString(c2[0]);
					}
					else {
						left = Double.toString(c1[0]);
						bottom = Double.toString(c1[1]);
						right = Double.toString(c2[0]);
						top = Double.toString(c2[1]);
					}
				}
			}
			if (resSource!=0) {
				try {
					url = new URL(wcpsEndpoint
							+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionID);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					SAXBuilder builder = new SAXBuilder();
					Document capabilititesDoc = builder.build(conn.getInputStream());
					List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
					Element rootNode = capabilititesDoc.getRootElement();
					Namespace defaultNS = rootNode.getNamespace();
					Namespace gmlNS = null;
					for (int n = 0; n < namespaces.size(); n++) {
						Namespace current = namespaces.get(n);
						if(current.getPrefix().equals("gml")) {
							gmlNS = current;
						}
					}					
					log.debug("root node info: " + rootNode.getName());
							
					Element coverageDescElement = rootNode.getChild("CoverageDescription", defaultNS);
					Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
					Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
					String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
					String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");
					String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
					int xIndex = 0;
				    int yIndex = 0;
					
					for(int a = 0; a < axis.length; a++) {
				    	log.debug(axis[a]);
						if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long")){
							xIndex = a;
						}
						if(axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
							yIndex = a;
						}
					}
					log.debug("Left: "+left);
					log.debug("Right: "+right);
					log.debug("Bottom: "+bottom);
					log.debug("Top: "+top);
					log.debug("MinX: "+minValues[xIndex]);
					log.debug("MaxX: "+maxValues[xIndex]);
					log.debug("MinY: "+minValues[yIndex]);
					log.debug("MaxY: "+maxValues[yIndex]);
					left = Double.toString(Double.parseDouble(minValues[xIndex]) + resSource*( Math.round(((Double.parseDouble(left) - Double.parseDouble(minValues[xIndex]))/resSource))));
					right = Double.toString(Double.parseDouble(maxValues[xIndex]) - resSource*(Math.round(((Double.parseDouble(maxValues[xIndex]) - Double.parseDouble(right))/resSource))));
					bottom = Double.toString(Double.parseDouble(minValues[yIndex]) + resSource*(Math.round(((Double.parseDouble(bottom) - Double.parseDouble(minValues[yIndex]))/resSource))));
					top = Double.toString(Double.parseDouble(maxValues[yIndex]) - resSource*(Math.round(((Double.parseDouble(maxValues[yIndex]) - Double.parseDouble(top))/resSource))));					
			}
			catch (MalformedURLException e) {
				log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());
				
			} catch (IOException e) {
				log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());
				
			} catch (JDOMException e) {
				log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
			}
			}
			log.debug("WEST: "+left);
			log.debug("EAST: "+right);
			log.debug("SOUTH: "+bottom);			
			log.debug("NORTH: "+top);
			if (left != null && right != null && top != null && bottom != null) {
				Filter eastFilter = null;
				Filter westFilter = null;
				for (Filter filter : this.filters) {
					String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
					if (axisUpperCase.equals("E") || axisUpperCase.equals("LONG") || axisUpperCase.equals("LON") || axisUpperCase.equals("LONGITUDE") || axisUpperCase.equals("X")) {
						eastFilter = filter;
					}
					else if (axisUpperCase.equals("N") || axisUpperCase.equals("LAT") || axisUpperCase.equals("LATITUDE") || axisUpperCase.equals("Y")) {
						westFilter = filter;
					}
				}
				this.filters.remove(eastFilter);
				this.filters.remove(westFilter);
				for (String spatAxis : collection.getCubeColonDimensions().keySet()) {	
					String spatAxisUpperCase = spatAxis.toUpperCase();
					if (spatAxisUpperCase.contentEquals("E") || spatAxisUpperCase.contentEquals("LONG") || spatAxisUpperCase.equals("LON") || spatAxisUpperCase.equals("LONGITUDE") || spatAxisUpperCase.contentEquals("X")) {
						spatAxisX = spatAxis;
					}
					else if (spatAxisUpperCase.contentEquals("N") || spatAxisUpperCase.contentEquals("LAT") || spatAxisUpperCase.equals("LATITUDE") || spatAxisUpperCase.contentEquals("Y")) {
						spatAxisY = spatAxis;
					}
				}
				this.filters.add(new Filter(spatAxisX+"_"+collectionID, left, right));
				this.filters.add(new Filter(spatAxisY+"_"+collectionID, bottom, top));				
			} else {
				log.error("No spatial information could be found in process!");
			}			
		}
	}

	private void createReduceTemporalAggregate(String processName, String collectionID, String processNodeKey) {
		String aggregateType = processName;
		Vector<String> params = new Vector<String>();
		String tempAxis = null;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();			
			if(axis.contains(collectionID)) {
				String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();				
				if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
					tempAxis = axis.replace("_"+ collectionID, "");
				}
			}
		}
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals(tempAxis+"_"+collectionID)) {
				params.add(filter.getLowerBound());
				params.add(filter.getUpperBound());
			}
		}
		log.debug("Temporal Aggregate added!");
		aggregates.add(new Aggregate(new String(tempAxis+"_"+collectionID+processNodeKey), aggregateType, params));
	}

	private void createTemporalAggregate(String processName, String collectionID, String processNodeKey) {
		String aggregateType = processName.split("_")[0];
		Vector<String> params = new Vector<String>();
		String tempAxis = null;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();			
			if(axis.contains(collectionID)) {
				String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
				if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
					tempAxis = axis.replace("_"+ collectionID, "");
				}
			}
		}
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals(tempAxis+"_"+collectionID)) {
				params.add(filter.getLowerBound());
				params.add(filter.getUpperBound());
			}
		}
		log.debug("Temporal Aggregate added!");
		aggregates.add(new Aggregate(new String(tempAxis+"_"+collectionID+processNodeKey), aggregateType, params));
	}

	private void createNDVIAggregateFromProcess(JSONObject argsObject, String collectionID) {
		String red = null;
		String nir = null;
				
		try {
			URL url = new URL(wcpsEndpoint
					+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionID);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
			}

			Boolean bandsMeta = false;
			Element metadataElement = null;
			try {
				metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS).getChild("Extension", gmlNS).getChild("covMetadata", gmlNS);
			}catch(Exception e) {
			}
			List<Element> bandsList = null;
			try {
				bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
				bandsMeta = true;
			}catch(Exception e) {
			}
			if (bandsMeta) {
				try {
					for(int c = 0; c < bandsList.size(); c++) {
						String bandCommonName = null;
						Element band = bandsList.get(c);
						try {
							bandCommonName = band.getChildText("common_name");
						}catch(Exception e) {
						}
						if (bandCommonName.equals("red")) {
							red = band.getChildText("name");
						}
						else if (bandCommonName.equals("nir")) {
							nir = band.getChildText("name");
						}
					}
				}catch(Exception e) {
				}
			}
		}
		catch (MalformedURLException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());

		} catch (IOException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());

		} catch (JDOMException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());

		}

		Vector<String> params = new Vector<String>();
		params.add(red);
		params.add(nir);
		if (red != null && nir != null) {
			log.debug("Feature Aggregate added!");
			aggregates.add(new Aggregate(new String("feature_"+collectionID), new String("NDVI_"+collectionID), params));
		}
	}
	
	private int getSRSFromCollection(Collection collection) {
		int srs = 0;
		for(Dimension dimension: collection.getCubeColonDimensions().values()) {
			if(dimension.getType() == Dimension.TypeEnum.SPATIAL) {
				srs = ((DimensionSpatial) dimension).getReferenceSystem();
				break;
			}
		}
		log.debug("srs is: " + srs);
		return srs;
	}
}
