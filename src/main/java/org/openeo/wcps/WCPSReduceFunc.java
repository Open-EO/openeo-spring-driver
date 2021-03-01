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
import org.openeo.wcps.domain.Collection;
import org.openeo.wcps.domain.Filter;

public class WCPSReduceFunc {
	Logger log = LogManager.getLogger();
	private JSONObject processGraph;
	private String openEOEndpoint;
	private String wcpsEndpoint;
	// Create WCPS query for all Reduce Process Callback Functions
		String createReduceWCPSString(Vector<Filter> filters, Vector<Aggregate> aggregates, String wcpsEndpoint, String openEOEndpoint, JSONObject openEOGraph, String reduceNodeKey, String payLoad, String filterString, String collectionVar, String collectionID, String dimension) {
			String reduceBuilderExtend = null;
			this.processGraph = openEOGraph;
			this.openEOEndpoint = openEOEndpoint;
			this.wcpsEndpoint = wcpsEndpoint;
			JSONObject reduceProcesses = processGraph.getJSONObject(reduceNodeKey).getJSONObject("arguments").getJSONObject("reducer").getJSONObject("process_graph");
			JSONObject reducerPayLoads = new JSONObject();
			JSONArray reduceNodesArray = new JSONArray();
			String endReducerNode = null;
			JSONArray endReducerNodeAsArray = new JSONArray();
			
			for (String reducerKey : reduceProcesses.keySet()) {
				JSONObject reducerProcess =  reduceProcesses.getJSONObject(reducerKey);
				for (String reducerField : reducerProcess.keySet()) {
					if (reducerField.equals("result")) {
						Boolean resultFlag = reducerProcess.getBoolean("result");
						if (resultFlag) {
							endReducerNode = reducerKey;
							endReducerNodeAsArray.put(endReducerNode);
							log.debug("End Reducer Process is : " + reduceProcesses.getJSONObject(endReducerNode).getString("process_id"));
						}
					}
				}
			}		
			
			JSONArray reduceNodesSortedArray = new JSONArray();
			reduceNodesArray.put(endReducerNodeAsArray);
			for (int n = 0; n < reduceNodesArray.length(); n++) {
				for (int a = 0; a < reduceNodesArray.getJSONArray(n).length(); a++) {
					JSONArray fromNodeOfReducers = getReducerFromNodes(reduceNodesArray.getJSONArray(n).getString(a), reduceProcesses);
					if (fromNodeOfReducers.length()>0) {
					reduceNodesArray.put(fromNodeOfReducers);
					}
					else if (fromNodeOfReducers.length()==0) {
						reduceNodesSortedArray.put(reduceNodesArray.getJSONArray(n).getString(a));
					}
				}
			}
			
			for (int i = 0; i < reduceNodesSortedArray.length(); i++) {
				for (int j = i + 1 ; j < reduceNodesSortedArray.length(); j++) {
					if (reduceNodesSortedArray.get(i).equals(reduceNodesSortedArray.get(j))) {
						reduceNodesSortedArray.remove(j);
					}
				}
			}
			
			reduceNodesArray.remove(reduceNodesArray.length()-1);
			for (int i = reduceNodesArray.length()-1; i>0; i--) {
				if (reduceNodesArray.getJSONArray(i).length()>0) {				
					for (int a = 0; a < reduceNodesArray.getJSONArray(i).length(); a++) {
						reduceNodesSortedArray.put(reduceNodesArray.getJSONArray(i).getString(a));
					}
				}
			}
			
			reduceNodesSortedArray.put(endReducerNode);
			for (int i = 0; i < reduceNodesSortedArray.length(); i++) {
				for (int j = i + 1 ; j < reduceNodesSortedArray.length(); j++) {
					if (reduceNodesSortedArray.get(i).equals(reduceNodesSortedArray.get(j))) {
						reduceNodesSortedArray.remove(j);
					}
				}
			}
					
			JSONArray reduceProcessesSequence = new JSONArray();
			for (int i = 0; i < reduceNodesSortedArray.length(); i++) {
				reduceProcessesSequence.put(reduceProcesses.getJSONObject(reduceNodesSortedArray.getString(i)).getString("process_id"));
			}
			
			log.debug("Reducer's Nodes Sequence is : ");
			log.debug(reduceNodesSortedArray);
			log.debug("Reducer's Processes Sequence is : ");
			log.debug(reduceProcessesSequence);

			for (int r = 0; r < reduceNodesSortedArray.length(); r++) {
				String nodeKey = reduceNodesSortedArray.getString(r);
				String name = reduceProcesses.getJSONObject(nodeKey).getString("process_id");
				
				if (name.equals("array_element")) {
					JSONObject arrayData =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					int arrayIndex = arrayData.getInt("index");
					if ( arrayData.get("data") instanceof JSONObject) {
						for (String fromType : arrayData.getJSONObject("data").keySet()) {
							if (fromType.equals("from_parameter") && arrayData.getJSONObject("data").getString("from_parameter").equals("data")) {
								reduceBuilderExtend = createBandWCPSString(collectionID, arrayIndex, reduceNodeKey, filterString, collectionVar);
							}
							else if (fromType.equals("from_node")) {
								reduceBuilderExtend = createBandWCPSString(collectionID, arrayIndex, reduceNodeKey, filterString, collectionVar);
							}
						}
					}
					else {
						reduceBuilderExtend = arrayData.getJSONArray("data").getString(arrayIndex);
					}
					
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);				
					log.debug("Array Element Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("count")) {
					String x = null;
					JSONObject countArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					if (countArguments.get("data") instanceof JSONObject) {
						for (String fromType : countArguments.getJSONObject("data").keySet()) {
							if (fromType.equals("from_parameter") && countArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = countArguments.getJSONObject("data").getString("from_node");
								String countPayLoad = reducerPayLoads.getString(dataNode);
								x = countPayLoad;
							}
						}
					}
					else {
						x = String.valueOf(countArguments.getJSONArray("data"));
					}
					reduceBuilderExtend = createCountWCPSString(x);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Count Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("mean")) {
					String meanPayLoad = null;
					JSONObject meanArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					if (meanArguments.get("data") instanceof JSONObject) {
						for (String fromType : meanArguments.getJSONObject("data").keySet()) {
							if (fromType.equals("from_parameter") && meanArguments.getJSONObject("data").getString("from_parameter").equals("data")) {
								meanPayLoad = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = meanArguments.getJSONObject("data").getString("from_node");
								meanPayLoad = reducerPayLoads.getString(dataNode);
							}						
						}
					}
					else if (meanArguments.get("data") instanceof JSONArray) {
						meanPayLoad = String.valueOf(meanArguments.getJSONArray("data"));
					}
					reduceBuilderExtend = createMeanWCPSString(filters, aggregates, reduceNodeKey, meanPayLoad, reduceProcesses, dimension, collectionVar, collectionID);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Mean Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("min")) {
					String minPayLoad = null;
					JSONObject minArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					if (minArguments.get("data") instanceof JSONObject) {
						for (String fromType : minArguments.getJSONObject("data").keySet()) {
							if (fromType.equals("from_parameter") && minArguments.getJSONObject("data").getString("from_parameter").equals("data")) {							
								minPayLoad = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = minArguments.getJSONObject("data").getString("from_node");
								minPayLoad = reducerPayLoads.getString(dataNode);
							}
						}
					}
					else if (minArguments.get("data") instanceof JSONArray) {
						minPayLoad = String.valueOf(minArguments.getJSONArray("data"));
					}
					log.debug("Min PayLoad" + minPayLoad);
					reduceBuilderExtend = createMinWCPSString(filters, aggregates, reduceNodeKey, minPayLoad, reduceProcesses, dimension, collectionVar, collectionID);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Min Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("max")) {
					String maxPayLoad = null;
					JSONObject maxArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					String dataNode = null;
					if (maxArguments.get("data") instanceof JSONObject) {
						for (String fromType : maxArguments.getJSONObject("data").keySet()) {
							if (fromType.equals("from_parameter") && maxArguments.getJSONObject("data").getString("from_parameter").equals("data")) {							
								maxPayLoad = payLoad;
							}
							else if (fromType.equals("from_node")) {
								dataNode = maxArguments.getJSONObject("data").getString("from_node");
								maxPayLoad = reducerPayLoads.getString(dataNode);
							}
						}
					}
					else if (maxArguments.get("data") instanceof JSONArray) {
						maxPayLoad = String.valueOf(maxArguments.getJSONArray("data"));
					}
					reduceBuilderExtend = createMaxWCPSString(filters, aggregates, reduceNodeKey, maxPayLoad, reduceProcesses, dimension, collectionVar, collectionID);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Max Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("and")) {
					JSONObject andArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					JSONArray andArrayreturn = new JSONArray();				
					
					if (andArguments.get("x") instanceof JSONObject) {
						for (String fromType : andArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && andArguments.getJSONObject("x").getString("from_parameter").equals("data")) {						
								andArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = andArguments.getJSONObject("x").getString("from_node");
								String andPayLoad = reducerPayLoads.getString(dataNode);
								andArrayreturn.put(andPayLoad);
							}			
						}
					}
					else if (andArguments.get("x") instanceof Boolean) {
						andArrayreturn.put(andArguments.getBoolean("x"));
					}
					
					if (andArguments.get("y") instanceof JSONObject) {
						for (String fromType : andArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && andArguments.getJSONObject("y").getString("from_parameter").equals("data")) {						
								andArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = andArguments.getJSONObject("y").getString("from_node");
								String andPayLoad = reducerPayLoads.getString(dataNode).replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
								andArrayreturn.put(andPayLoad);
							}
						}
					}
					else if (andArguments.get("y") instanceof Boolean) {
						andArrayreturn.put(andArguments.getBoolean("y"));
					}
					
					reduceBuilderExtend = createANDWCPSString(andArrayreturn);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("AND Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("or")) {
					JSONObject orArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					JSONArray orArrayreturn = new JSONArray();
									
					if (orArguments.get("x") instanceof JSONObject) {
						for (String fromType : orArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && orArguments.getJSONObject("x").getString("from_parameter").equals("data")) {						
								orArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = orArguments.getJSONObject("x").getString("from_node");
								String orPayLoad = reducerPayLoads.getString(dataNode);
								orArrayreturn.put(orPayLoad);
							}			
						}
					}
					else if (orArguments.get("x") instanceof Boolean) {
						orArrayreturn.put(orArguments.getBoolean("x"));
					}
					
					if (orArguments.get("y") instanceof JSONObject) {
						for (String fromType : orArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && orArguments.getJSONObject("y").getString("from_parameter").equals("data")) {						
								orArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = orArguments.getJSONObject("y").getString("from_node");
								String orPayLoad = reducerPayLoads.getString(dataNode);
								orPayLoad = orPayLoad.replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
								orArrayreturn.put(orPayLoad);
							}
						}
					}
					else if (orArguments.get("y") instanceof Boolean) {
						orArrayreturn.put(orArguments.getBoolean("y"));
					}
					
					reduceBuilderExtend = createORWCPSString(orArrayreturn);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("OR Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("xor")) {
					JSONObject xorArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					JSONArray xorArrayreturn = new JSONArray();
					
					if (xorArguments.get("x") instanceof JSONObject) {
						for (String fromType : xorArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && xorArguments.getJSONObject("x").getString("from_parameter").equals("data")) {						
								xorArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = xorArguments.getJSONObject("x").getString("from_node");
								String xorPayLoad = reducerPayLoads.getString(dataNode);
								xorArrayreturn.put(xorPayLoad);
							}			
						}
					}
					else if (xorArguments.get("x") instanceof Boolean) {
						xorArrayreturn.put(xorArguments.getBoolean("x"));
					}
					
					if (xorArguments.get("y") instanceof JSONObject) {
						for (String fromType : xorArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && xorArguments.getJSONObject("y").getString("from_parameter").equals("data")) {						
								xorArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = xorArguments.getJSONObject("y").getString("from_node");
								String xorPayLoad = reducerPayLoads.getString(dataNode).replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
								xorArrayreturn.put(xorPayLoad);
							}
						}
					}
					else if (xorArguments.get("y") instanceof Boolean) {
						xorArrayreturn.put(xorArguments.getBoolean("y"));
					}
					
					reduceBuilderExtend = createXORWCPSString(xorArrayreturn);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("XOR Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("multiply")) {
					JSONObject productArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					JSONArray productArrayreturn = new JSONArray();
					
					if (productArguments.get("x") instanceof JSONObject) {
						for (String fromType : productArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && productArguments.getJSONObject("x").getString("from_parameter").equals("data")) {						
								productArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = productArguments.getJSONObject("x").getString("from_node");
								String productPayLoad = reducerPayLoads.getString(dataNode);
								productArrayreturn.put(productPayLoad);
							}			
						}
					}
					else {
						productArrayreturn.put(productArguments.get("x"));
					}
					
					if (productArguments.get("y") instanceof JSONObject) {
						for (String fromType : productArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && productArguments.getJSONObject("y").getString("from_parameter").equals("data")) {						
								productArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = productArguments.getJSONObject("y").getString("from_node");
								String productPayLoad = reducerPayLoads.getString(dataNode).replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
								productArrayreturn.put(productPayLoad);
							}
						}
					}
					else {
						productArrayreturn.put(productArguments.get("y"));
					}
					
					reduceBuilderExtend = createProductWCPSString(productArrayreturn);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Product Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("add")) {
					JSONObject sumArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					JSONArray sumArrayreturn = new JSONArray();
					
					if (sumArguments.get("x") instanceof JSONObject) {
						for (String fromType : sumArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && sumArguments.getJSONObject("x").getString("from_parameter").equals("data")) {						
								sumArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = sumArguments.getJSONObject("x").getString("from_node");
								String sumPayLoad = reducerPayLoads.getString(dataNode);
								sumArrayreturn.put(sumPayLoad);
							}			
						}
					}
					else {
						sumArrayreturn.put(sumArguments.get("x"));
					}
					
					if (sumArguments.get("y") instanceof JSONObject) {
						for (String fromType : sumArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && sumArguments.getJSONObject("y").getString("from_parameter").equals("data")) {						
								sumArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = sumArguments.getJSONObject("y").getString("from_node");
								String sumPayLoad = reducerPayLoads.getString(dataNode).replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
								sumArrayreturn.put(sumPayLoad);
							}
						}
					}
					else {
						sumArrayreturn.put(sumArguments.get("y"));
					}
					
					reduceBuilderExtend = createSumWCPSString(sumArrayreturn);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Sum Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("subtract")) {
					JSONObject subtractArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					JSONArray subtractArrayreturn = new JSONArray();
					
					if (subtractArguments.get("x") instanceof JSONObject) {
						for (String fromType : subtractArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && subtractArguments.getJSONObject("x").getString("from_parameter").equals("data")) {						
								subtractArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = subtractArguments.getJSONObject("x").getString("from_node");
								String subtractPayLoad = reducerPayLoads.getString(dataNode);
								subtractArrayreturn.put(subtractPayLoad);
							}			
						}
					}
					else {
						subtractArrayreturn.put(subtractArguments.get("x"));
					}
					
					if (subtractArguments.get("y") instanceof JSONObject) {
						for (String fromType : subtractArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && subtractArguments.getJSONObject("y").getString("from_parameter").equals("data")) {						
								subtractArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = subtractArguments.getJSONObject("y").getString("from_node");
								String subtractPayLoad = reducerPayLoads.getString(dataNode).replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
								subtractArrayreturn.put(subtractPayLoad);
							}
						}
					}
					else {
						subtractArrayreturn.put(subtractArguments.get("y"));
					}
					
					reduceBuilderExtend = createSubtractWCPSString(subtractArrayreturn);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Subtract Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("divide")) {
					JSONObject divideArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					JSONArray divideArrayreturn = new JSONArray();
					
					if (divideArguments.get("x") instanceof JSONObject) {
						for (String fromType : divideArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && divideArguments.getJSONObject("x").getString("from_parameter").equals("data")) {						
								divideArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = divideArguments.getJSONObject("x").getString("from_node");
								String dividePayLoad = reducerPayLoads.getString(dataNode);
								divideArrayreturn.put(dividePayLoad);
							}			
						}
					}
					else {
						divideArrayreturn.put(divideArguments.get("x"));
					}
					
					if (divideArguments.get("y") instanceof JSONObject) {
						for (String fromType : divideArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && divideArguments.getJSONObject("y").getString("from_parameter").equals("data")) {						
								divideArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = divideArguments.getJSONObject("y").getString("from_node");
								String dividePayLoad = reducerPayLoads.getString(dataNode).replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
								divideArrayreturn.put(dividePayLoad);
							}
						}
					}
					else {
						divideArrayreturn.put(divideArguments.get("y"));
					}
					
					reduceBuilderExtend = createDivideWCPSString(divideArrayreturn);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Divide Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.contains("linear_scale_range")) {
					String x = null;
					JSONObject linearScaleRangeArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					for (String argType : linearScaleRangeArguments.keySet()) {
						if ((argType.equals("x")) && linearScaleRangeArguments.get(argType) instanceof JSONObject) {
							for (String fromType : linearScaleRangeArguments.getJSONObject(argType).keySet()) {
								if (fromType.equals("from_parameter") && linearScaleRangeArguments.getJSONObject(argType).getString("from_parameter").equals("data")) {
									x = payLoad;								
								}
								else if (fromType.equals("from_node")) {
									String dataNode = linearScaleRangeArguments.getJSONObject(argType).getString("from_node");
									String linearScaleRangePayLoad = reducerPayLoads.getString(dataNode);
									x = linearScaleRangePayLoad;
								}
							}
						}
						else if (argType.equals("x") && linearScaleRangeArguments.get(argType) instanceof Double) {						
							x = String.valueOf(linearScaleRangeArguments.getDouble("x"));
						}
					}
					reduceBuilderExtend = createLinearScaleRangeWCPSString(nodeKey, x, reduceProcesses);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Linear Scale Range Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("absolute")) {
					String x = null;
					JSONObject absArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					
					if (absArguments.get("x") instanceof JSONObject) {
						for (String fromType : absArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && absArguments.getJSONObject("x").getString("from_parameter").equals("data")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = absArguments.getJSONObject("x").getString("from_node");
								String absPayLoad = reducerPayLoads.getString(dataNode);
								x = absPayLoad;
							}						
						}
					}
					else if (absArguments.get("x") instanceof Double) {
						x = String.valueOf(absArguments.getDouble("x"));
					}
					
					reduceBuilderExtend = createAbsWCPSString(x);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Absolute Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("not")) {
					String x = null;
					JSONObject notArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					if (notArguments.get("x") instanceof JSONObject) {
						for (String fromType : notArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && notArguments.getJSONObject("x").getString("from_parameter").equals("data")) {						
								x=payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = notArguments.getJSONObject("x").getString("from_node");
								String notPayLoad = reducerPayLoads.getString(dataNode);
								x=notPayLoad;
							}			
						}
					}
					else if (notArguments.get("x") instanceof Boolean) {
						x = String.valueOf(notArguments.getBoolean("x"));
					}
					
					reduceBuilderExtend = createNotWCPSString(x);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("NOT Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));				
				}
				
				if (name.equals("log")) {
					String x = null;
					JSONObject logArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					for (String argType : logArguments.keySet()) {
						if ((argType.equals("x")) && logArguments.get(argType) instanceof JSONObject) {
							for (String fromType : logArguments.getJSONObject(argType).keySet()) {
								if (fromType.equals("from_parameter") && logArguments.getJSONObject(argType).getString("from_parameter").equals("data")) {
									x = payLoad;
								}
								else if (fromType.equals("from_node")) {
									String dataNode = logArguments.getJSONObject(argType).getString("from_node");
									String logPayLoad = reducerPayLoads.getString(dataNode);
									x = logPayLoad;
								}						
							}
						}
						else if (argType.equals("x") && logArguments.get(argType) instanceof Double) {
							x = String.valueOf(logArguments.getDouble("x"));
						}
					}
					reduceBuilderExtend = createLogWCPSString(x);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Log Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("ln")) {
					String x = null;
					JSONObject logNArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					for (String argType : logNArguments.keySet()) {
						if ((argType.equals("x")) && logNArguments.get(argType) instanceof JSONObject) {
							for (String fromType : logNArguments.getJSONObject(argType).keySet()) {
								if (fromType.equals("from_parameter") && logNArguments.getJSONObject(argType).getString("from_parameter").equals("data")) {
									x = payLoad;
								}
								else if (fromType.equals("from_node")) {
									String dataNode = logNArguments.getJSONObject(argType).getString("from_node");
									String logNPayLoad = reducerPayLoads.getString(dataNode);
									x = logNPayLoad;
								}						
							}
						}
						else if (argType.equals("x") && logNArguments.get(argType) instanceof Double) {
							x = String.valueOf(logNArguments.getDouble("x"));
						}
					}
					reduceBuilderExtend = createLogNWCPSString(x);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Natural Log Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("sqrt")) {
					String x = null;
					JSONObject sqrtArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					for (String argType : sqrtArguments.keySet()) {
						if ((argType.equals("x")) && sqrtArguments.get(argType) instanceof JSONObject) {
							for (String fromType : sqrtArguments.getJSONObject(argType).keySet()) {
								if (fromType.equals("from_parameter") && sqrtArguments.getJSONObject(argType).getString("from_parameter").equals("data")) {
									x = payLoad;
								}
								else if (fromType.equals("from_node")) {
									String dataNode = sqrtArguments.getJSONObject(argType).getString("from_node");
									String sqrtPayLoad = reducerPayLoads.getString(dataNode);
									x = sqrtPayLoad;
								}						
							}
						}
						else if (argType.equals("x") && sqrtArguments.get(argType) instanceof Double) {
							x = String.valueOf(sqrtArguments.getDouble("x"));
						}
					}
					reduceBuilderExtend = createSqrtWCPSString(x);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Square Root Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("power")) {
					String base = null;
					JSONObject powArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					for (String argType : powArguments.keySet()) {
						if ((argType.equals("base")) && powArguments.get(argType) instanceof JSONObject) {
							for (String fromType : powArguments.getJSONObject(argType).keySet()) {
								if (fromType.equals("from_parameter") && powArguments.getJSONObject(argType).getString("from_parameter").equals("data")) {
									base = payLoad;
								}
								else if (fromType.equals("from_node")) {
									String dataNode = powArguments.getJSONObject(argType).getString("from_node");
									String powPayLoad = reducerPayLoads.getString(dataNode);
									base = powPayLoad;
								}						
							}
						}
						else if (argType.equals("x") && powArguments.get(argType) instanceof Double) {
							base = String.valueOf(powArguments.getDouble("base"));
						}
					}
					reduceBuilderExtend = createPowWCPSString(nodeKey, base, reduceProcesses);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Power Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("exp")) {
					String p = null;
					JSONObject expArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					for (String argType : expArguments.keySet()) {
						if ((argType.equals("p")) && expArguments.get(argType) instanceof JSONObject) {
							for (String fromType : expArguments.getJSONObject(argType).keySet()) {
								if (fromType.equals("from_parameter") && expArguments.getJSONObject(argType).getString("from_parameter").equals("data")) {
									p = payLoad;
								}
								else if (fromType.equals("from_node")) {
									String dataNode = expArguments.getJSONObject(argType).getString("from_node");
									String expPayLoad = reducerPayLoads.getString(dataNode);
									p = expPayLoad;
								}
							}
						}
						else if (argType.equals("x") && expArguments.get(argType) instanceof Double) {
							p = String.valueOf(expArguments.getDouble("p"));
						}
					}
					reduceBuilderExtend = createExpWCPSString(p);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Exponential Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("pi")) {
					reduceBuilderExtend = createPiWCPSString();
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Pi Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				if (name.equals("e")) {
					reduceBuilderExtend = createEulerNumWCPSString();
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Euler's Constant Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("sin")||name.equals("cos")||name.equals("tan")||name.equals("sinh")||name.equals("cosh")||name.equals("tanh")||name.equals("arcsin")||name.equals("arccos")||name.equals("arctan")) {
					String x = null;
					JSONObject trigArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					for (String argType : trigArguments.keySet()) {
						if ((argType.equals("x")) && trigArguments.get(argType) instanceof JSONObject) {
							for (String fromType : trigArguments.getJSONObject(argType).keySet()) {
								if (fromType.equals("from_parameter") && trigArguments.getJSONObject(argType).getString("from_parameter").equals("data")) {
									x = payLoad;
								}
								else if (fromType.equals("from_node")) {
									String dataNode = trigArguments.getJSONObject(argType).getString("from_node");
									String trigPayLoad = reducerPayLoads.getString(dataNode);
									x = trigPayLoad;
								}
							}
						}
						else if (argType.equals("x") && trigArguments.get(argType) instanceof Double) {
							x = String.valueOf(trigArguments.getDouble("x"));
						}
					}
					reduceBuilderExtend = createTrigWCPSString(nodeKey, x, reduceProcesses, name);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Trigonometric Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("gte")) {
					String x = null;
					String y = null;
					JSONObject gteArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					
					if (gteArguments.get("x") instanceof JSONObject) {
						for (String fromType : gteArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && gteArguments.getJSONObject("x").getString("from_parameter").equals("data")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeX = gteArguments.getJSONObject("x").getString("from_node");
								String gtePayLoadX = reducerPayLoads.getString(dataNodeX);
								x = gtePayLoadX;
							}						
						}
					}
					else {
						x = String.valueOf(gteArguments.getDouble("x"));
					}
					if (gteArguments.get("y") instanceof JSONObject) {
						for (String fromType : gteArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && gteArguments.getJSONObject("y").getString("from_parameter").equals("data")) {
								y = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeY = gteArguments.getJSONObject("y").getString("from_node");
								String gtePayLoadY = reducerPayLoads.getString(dataNodeY);
								y = gtePayLoadY;
							}
						}
					}
					else {
						y = String.valueOf(gteArguments.getDouble("y"));
					}
					
					reduceBuilderExtend = createGreatThanEqWCPSString(x, y);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Greater Than Equal Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("gt")) {
					String x = null;
					String y = null;
					JSONObject gtArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					
					if (gtArguments.get("x") instanceof JSONObject) {
						for (String fromType : gtArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && gtArguments.getJSONObject("x").getString("from_parameter").equals("data")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeX = gtArguments.getJSONObject("x").getString("from_node");
								String gtPayLoadX = reducerPayLoads.getString(dataNodeX);
								x = gtPayLoadX;
							}						
						}
					}
					else {
						x = String.valueOf(gtArguments.getDouble("x"));
					}
					if (gtArguments.get("y") instanceof JSONObject) {
						for (String fromType : gtArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && gtArguments.getJSONObject("y").getString("from_parameter").equals("data")) {
								y = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeY = gtArguments.getJSONObject("y").getString("from_node");
								String gtPayLoadY = reducerPayLoads.getString(dataNodeY);
								y = gtPayLoadY;
							}						
						}
					}
					else {
						y = String.valueOf(gtArguments.getDouble("y"));
					}
					
					reduceBuilderExtend = createGreatThanWCPSString(x, y);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Greater Than Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("lte")) {
					String x = null;
					String y = null;
					JSONObject lteArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					
					if (lteArguments.get("x") instanceof JSONObject) {
						for (String fromType : lteArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && lteArguments.getJSONObject("x").getString("from_parameter").equals("data")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeX = lteArguments.getJSONObject("x").getString("from_node");
								String ltePayLoadX = reducerPayLoads.getString(dataNodeX);
								x = ltePayLoadX;
							}						
						}
					}
					else {
						x = String.valueOf(lteArguments.getDouble("x"));
					}
					if (lteArguments.get("y") instanceof JSONObject) {
						for (String fromType : lteArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && lteArguments.getJSONObject("y").getString("from_parameter").equals("data")) {
								y = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeY = lteArguments.getJSONObject("y").getString("from_node");
								String ltePayLoadY = reducerPayLoads.getString(dataNodeY);
								y = ltePayLoadY;
							}						
						}
					}
					else {
						y = String.valueOf(lteArguments.getDouble("y"));
					}
					
					reduceBuilderExtend = createLessThanEqWCPSString(x, y);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Less Than Equal Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("lt")) {
					String x = null;
					String y = null;
					JSONObject ltArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					
					if (ltArguments.get("x") instanceof JSONObject) {
						for (String fromType : ltArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && ltArguments.getJSONObject("x").getString("from_parameter").equals("data")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeX = ltArguments.getJSONObject("x").getString("from_node");
								String ltPayLoadX = reducerPayLoads.getString(dataNodeX);
								x = ltPayLoadX;
							}						
						}
					}
					else {
						x = String.valueOf(ltArguments.getDouble("x"));
					}
					if (ltArguments.get("y") instanceof JSONObject) {
						for (String fromType : ltArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && ltArguments.getJSONObject("y").getString("from_parameter").equals("data")) {
								y = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeY = ltArguments.getJSONObject("y").getString("from_node");
								String ltPayLoadY = reducerPayLoads.getString(dataNodeY);
								y = ltPayLoadY;
							}						
						}
					}
					else {
						y = String.valueOf(ltArguments.getInt("y"));
					}
					
					reduceBuilderExtend = createLessThanWCPSString(x, y);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Less Than Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("neq")) {
					String x = null;
					String y = null;
					JSONObject neqArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					
					if (neqArguments.get("x") instanceof JSONObject) {
						for (String fromType : neqArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && neqArguments.getJSONObject("x").getString("from_parameter").equals("data")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeX = neqArguments.getJSONObject("x").getString("from_node");
								String neqPayLoadX = reducerPayLoads.getString(dataNodeX);
								x = neqPayLoadX;
							}						
						}
					}
					else {
						x = String.valueOf(neqArguments.getDouble("x"));
					}
					if (neqArguments.get("y") instanceof JSONObject) {
						for (String fromType : neqArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && neqArguments.getJSONObject("y").getString("from_parameter").equals("data")) {
								y = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeY = neqArguments.getJSONObject("y").getString("from_node");
								String neqPayLoadY = reducerPayLoads.getString(dataNodeY);
								y = neqPayLoadY;
							}						
						}
					}
					else {
						y = String.valueOf(neqArguments.getDouble("y"));
					}
					
					reduceBuilderExtend = createNotEqWCPSString(x, y);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Not Equal Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
				
				if (name.equals("eq")) {
					String x = null;
					String y = null;
					JSONObject eqArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
					
					if (eqArguments.get("x")  instanceof JSONObject) {
						for (String fromType : eqArguments.getJSONObject("x").keySet()) {
							if (fromType.equals("from_parameter") && eqArguments.getJSONObject("x").getString("from_parameter").equals("data")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeX = eqArguments.getJSONObject("x").getString("from_node");
								String eqPayLoadX = reducerPayLoads.getString(dataNodeX);
								x = eqPayLoadX;
							}						
						}
					}
					else {
						x = String.valueOf(eqArguments.getDouble("x"));
					}
					if (eqArguments.get("y") instanceof JSONObject) {
						for (String fromType : eqArguments.getJSONObject("y").keySet()) {
							if (fromType.equals("from_parameter") && eqArguments.getJSONObject("y").getString("from_parameter").equals("data")) {
								y = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNodeY = eqArguments.getJSONObject("y").getString("from_node");
								String eqPayLoadY = reducerPayLoads.getString(dataNodeY);
								y = eqPayLoadY;
							}						
						}
					}
					else {
						y = String.valueOf(eqArguments.getDouble("y"));
					}
					
					reduceBuilderExtend = createEqWCPSString(x, y);
					reducerPayLoads.put(nodeKey, reduceBuilderExtend);
					log.debug("Equal Process PayLoad is : ");
					log.debug(reducerPayLoads.get(nodeKey));
				}
			}
			return reduceBuilderExtend;
		}
		
		private String createEqWCPSString(String x, String y) {
			StringBuilder stretchBuilder = new StringBuilder("(");				
			stretchBuilder.append(x + " = " + y +")");
			return stretchBuilder.toString();
		}
		private String createNotEqWCPSString(String x, String y) {
			StringBuilder stretchBuilder = new StringBuilder("(");				
			stretchBuilder.append(x + " != " + y +")");		
			return stretchBuilder.toString();
		}
		private String createLessThanWCPSString(String x, String y) {
			StringBuilder stretchBuilder = new StringBuilder("(");				
			stretchBuilder.append(x + " < " + y +")");		
			return stretchBuilder.toString();
		}
		private String createLessThanEqWCPSString(String x, String y) {
			StringBuilder stretchBuilder = new StringBuilder("(");				
			stretchBuilder.append(x + " <= " + y +")");		
			return stretchBuilder.toString();
		}
		private String createGreatThanWCPSString(String x, String y) {
			StringBuilder stretchBuilder = new StringBuilder("(");				
			stretchBuilder.append(x + " > " + y +")");		
			return stretchBuilder.toString();
		}
		private String createGreatThanEqWCPSString(String x, String y) {
			StringBuilder stretchBuilder = new StringBuilder("(");				
			stretchBuilder.append(x + " >= " + y +")");		
			return stretchBuilder.toString();
		}
		private String createNotWCPSString(String payLoad) {
			String stretchString = null;
			StringBuilder stretchBuilder = new StringBuilder("");
			stretchBuilder.append("not " + payLoad);
			stretchString = stretchBuilder.toString();

			return stretchString;
		}
		private String createLogNWCPSString(String payLoad) {
			String stretchString = null;
			StringBuilder stretchBuilder = new StringBuilder("");
			stretchBuilder.append("ln(" + payLoad + ")");
			stretchString = stretchBuilder.toString();

			return stretchString;
		}
		private String createLogWCPSString(String payLoad) {
			String stretchString = null;
			StringBuilder stretchBuilder = new StringBuilder("");
			stretchBuilder.append("log(" + payLoad + ")");
			stretchString = stretchBuilder.toString();			

			return stretchString;
		}
		private String createExpWCPSString(String payLoad) {
			String stretchString = null;
					StringBuilder stretchBuilder = new StringBuilder("");
					stretchBuilder.append("exp(" + payLoad + ")");
					stretchString = stretchBuilder.toString();			
			
			return stretchString;
		}
		private String createPowWCPSString(String powNodeKey, String payLoad, JSONObject reduceProcesses) {
			String stretchString = null;
			JSONObject powArguments = reduceProcesses.getJSONObject(powNodeKey).getJSONObject("arguments");		
			double pow = powArguments.getDouble("p");
			StringBuilder stretchBuilder = new StringBuilder("");
			stretchBuilder.append("pow(" + payLoad + "," + pow + ")");
			stretchString = stretchBuilder.toString();			

			return stretchString;
		}
		private String createSqrtWCPSString(String payLoad) {		
			String stretchString = null;		
			StringBuilder stretchBuilder = new StringBuilder("");
			stretchBuilder.append("sqrt(" + payLoad + ")");
			stretchString = stretchBuilder.toString();

			return stretchString;
		}
		private String createAbsWCPSString(String payLoad) {
			String stretchString = null;
			StringBuilder stretchBuilder = new StringBuilder("");
			stretchBuilder.append("abs(" + payLoad + ")");
			stretchString = stretchBuilder.toString();

			return stretchString;
		}
		private String createPiWCPSString() {
			return String.valueOf(Math.PI);
		}
		private String createEulerNumWCPSString() {
			return String.valueOf(Math.E);
		}
		
		private String createBandWCPSString(String collectionID, int arrayIndex, String reduceNodeKey, String filterString, String collectionVar) {
			StringBuilder stretchBuilder = new StringBuilder("");
			String fromNodeOfReduce = processGraph.getJSONObject(reduceNodeKey).getJSONObject("arguments").getJSONObject("data").getString("from_node");
			fromNodeOfReduce = getFilterCollectionNode(fromNodeOfReduce);
			JSONObject fromProcess = processGraph.getJSONObject(fromNodeOfReduce);
			
			if (fromProcess.getString("process_id").equals("load_collection")) {
				String bandfromIndex = fromProcess.getJSONObject("arguments").getJSONArray("bands").getString(arrayIndex);
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
				stretchBuilder.append(createBandSubsetString(collectionVar, bandName, filterString));		
			}
			return stretchBuilder.toString();
		}

		private String createANDWCPSString(JSONArray andArrayreturn) {
			StringBuilder stretchBuilder = new StringBuilder("(" + andArrayreturn.get(0));
			for (int f = 1; f < andArrayreturn.length(); f++) {
				stretchBuilder.append(" and " + andArrayreturn.get(f));
			}
			stretchBuilder.append(")");
			return stretchBuilder.toString();
		}

		private String createORWCPSString(JSONArray orArrayreturn) {
			StringBuilder stretchBuilder = new StringBuilder("(" + orArrayreturn.get(0));
			for (int f = 1; f < orArrayreturn.length(); f++) {
				stretchBuilder.append(" or " + orArrayreturn.get(f));
			}
			stretchBuilder.append(")");
			return stretchBuilder.toString();
		}

		private String createXORWCPSString(JSONArray xorArrayreturn) {
			StringBuilder stretchBuilder = new StringBuilder("(" + xorArrayreturn.get(0));
			for (int f = 1; f < xorArrayreturn.length(); f++) {
				stretchBuilder.append(" xor " + xorArrayreturn.get(f));
			}
			stretchBuilder.append(")");
			return stretchBuilder.toString();
		}

		private String createProductWCPSString(JSONArray productArrayreturn) {
			StringBuilder stretchBuilder = new StringBuilder("("+productArrayreturn.get(0));
			for (int f = 1; f < productArrayreturn.length(); f++) {
				stretchBuilder.append(" * "+productArrayreturn.get(f));
			}
			stretchBuilder.append(")");
			return stretchBuilder.toString();
		}
		
		private String createSumWCPSString(JSONArray sumArrayreturn) {
			StringBuilder stretchBuilder = new StringBuilder("("+sumArrayreturn.get(0));
			for (int f = 1; f < sumArrayreturn.length(); f++) {
				stretchBuilder.append(" + "+sumArrayreturn.get(f));
			}
			stretchBuilder.append(")");
			return stretchBuilder.toString();
		}

		private String createSubtractWCPSString(JSONArray subtractArrayreturn) {
			StringBuilder stretchBuilder = new StringBuilder("("+subtractArrayreturn.get(0));
			for (int f = 1; f < subtractArrayreturn.length(); f++) {
				stretchBuilder.append(" - "+subtractArrayreturn.get(f));
			}
			stretchBuilder.append(")");
			return stretchBuilder.toString();
		}

		private String createDivideWCPSString(JSONArray divideArrayreturn) {
			StringBuilder stretchBuilder = new StringBuilder("("+divideArrayreturn.get(0));
			for (int f = 1; f < divideArrayreturn.length(); f++) {
				stretchBuilder.append(" / "+divideArrayreturn.get(f));
			}
			stretchBuilder.append(")");
			return stretchBuilder.toString();
		}
		
		private String createCountWCPSString(String payLoad) {
			String stretchString = null;
			StringBuilder stretchBuilder = new StringBuilder("count(");
			stretchBuilder.append(payLoad + ")");
			stretchString = stretchBuilder.toString();
			
			return stretchString;
		}

		private String createMeanWCPSString(Vector<Filter> filters, Vector<Aggregate> aggregates, String reduceNodeKey, String payLoad, JSONObject reduceProcesses, String dimension, String collectionVar, String collectionID) {
			String stretchString = null;
			StringBuilder stretchBuilder = new StringBuilder("");
			String wcps_endpoint = openEOEndpoint;
			
			JSONObject jsonresp = null;
			try {
				jsonresp = readJsonFromUrl(wcps_endpoint + "/collections/" + collectionID);
			} catch (JSONException e) {
				log.error("An error occured: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			} catch (IOException e) {
				log.error("An error occured: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			}
			
			String temporalAxis = null;
			for (String tempAxis1 : jsonresp.getJSONObject("cube:dimensions").keySet()) {
				String tempAxis1UpperCase = tempAxis1.toUpperCase();
				if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
					temporalAxis = tempAxis1;
				}
			}
			
			if (dimension.contains("spectral") || dimension.contains("bands")) {
				stretchBuilder.append("avg(" + payLoad + ")");    	    
				stretchString = stretchBuilder.toString();
			}
			else if (dimension.equals("t") || dimension.equals("temporal") || dimension.contentEquals(temporalAxis)) {
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
					log.debug("Aggregate is : ");
					log.debug(aggregates.get(a).getAxis() + "Operator " + aggregates.get(a).getOperator());
					log.debug("TempAxis " + tempAxis + " " + collectionID);
					if (aggregates.get(a).getAxis().equals(tempAxis+"_"+collectionID+reduceNodeKey)) {
						stretchBuilder.append(createMeanTempAggWCPSString(filters, aggregates, reduceNodeKey, collectionVar, collectionID, aggregates.get(a), payLoad, tempAxis));
//						String replaceDate = wcpsPayLoad.toString().replaceAll(tempAxis+"\\(.*?\\)", tempAxis+"\\(\\$pm\\)");
//						StringBuilder wcpsAggBuilderMod = new StringBuilder("");
//						wcpsAggBuilderMod.append(meanDateRange1);
//						stretchBuilder.append(wcpsAggBuilderMod);
						stretchString=stretchBuilder.toString();
					}
				}
			}
			return stretchString;
		}

		private String createMaxWCPSString(Vector<Filter> filters, Vector<Aggregate> aggregates, String reduceNodeKey, String payLoad, JSONObject reduceProcesses, String dimension, String collectionVar, String collectionID) {
			String stretchString = null;
			StringBuilder stretchBuilder = new StringBuilder("");
			String wcps_endpoint = openEOEndpoint;		
			JSONObject jsonresp = null;
			try {
				jsonresp = readJsonFromUrl(wcps_endpoint + "/collections/" + collectionID);
			} catch (JSONException e) {
				log.error("An error occured: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			} catch (IOException e) {
				log.error("An error occured: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			}
			
			String temporalAxis = null;
			for (String tempAxis1 : jsonresp.getJSONObject("cube:dimensions").keySet()) {
				String tempAxis1UpperCase = tempAxis1.toUpperCase();
				if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
					temporalAxis = tempAxis1;
				}
			}
			
			if (dimension.contains("spectral") || dimension.contains("bands")) {
				stretchBuilder.append("max(" + payLoad + ")");    	    
				stretchString = stretchBuilder.toString();
			}
			else if (dimension.equals("t") || dimension.equals("temporal") || dimension.contentEquals(temporalAxis)) {
				log.debug("Reduce Dimension : " + temporalAxis);
				log.debug(payLoad);
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
				log.debug(aggregates.size());
				for (int a = 0; a < aggregates.size(); a++) {
					log.debug("Aggregate Axis " + aggregates.get(a).getAxis());
					log.debug("Aggregate Operator " + aggregates.get(a).getOperator());
					log.debug("Reduce Node " + reduceNodeKey);
					if (aggregates.get(a).getAxis().equals(tempAxis+"_"+collectionID+reduceNodeKey)) {
						stretchBuilder.append(createTempAggWCPSString(filters, aggregates, reduceNodeKey, collectionVar, collectionID, aggregates.get(a), tempAxis));
						log.debug(stretchString);
						String replaceDate = Pattern.compile(tempAxis+"\\(.*?\\)").matcher(payLoad).replaceAll(tempAxis+"\\(\\$pm\\"+ reduceNodeKey + ")");
						//String replaceDate = wcpsPayLoad.toString().replaceAll(tempAxis+"\\(.*?\\)", tempAxis+"\\(\\$pm\\)");
						StringBuilder wcpsAggBuilderMod = new StringBuilder("");
						wcpsAggBuilderMod.append(replaceDate);
						stretchBuilder.append(wcpsAggBuilderMod);
						stretchString=stretchBuilder.toString();
					}
				}
				log.debug(stretchString);
			}
			return stretchString;
		}

		private String createMinWCPSString(Vector<Filter> filters, Vector<Aggregate> aggregates, String reduceNodeKey, String payLoad, JSONObject reduceProcesses, String dimension, String collectionVar, String collectionID) {
			String stretchString = null;
			StringBuilder stretchBuilder = new StringBuilder("");
			String wcps_endpoint = openEOEndpoint;
			
			JSONObject jsonresp = null;
			try {
				jsonresp = readJsonFromUrl(wcps_endpoint + "/collections/" + collectionID);
			} catch (JSONException e) {
				log.error("An error occured: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			} catch (IOException e) {
				log.error("An error occured: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			}
			
			String temporalAxis = null;
			for (String tempAxis1 : jsonresp.getJSONObject("cube:dimensions").keySet()) {
				String tempAxis1UpperCase = tempAxis1.toUpperCase();
				if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
					temporalAxis = tempAxis1;
				}
			}
			
			if (dimension.contains("spectral") || dimension.contains("bands")) {
				stretchBuilder.append("min(" + payLoad + ")");
				stretchString = stretchBuilder.toString();
			}
			else if (dimension.equals("t") || dimension.equals("temporal") || dimension.contentEquals(temporalAxis)) {
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
					log.debug("Aggregate Axis " + aggregates.get(a).getAxis());
					log.debug("Aggregate Operator " + aggregates.get(a).getOperator());
					log.debug("Reduce Node " + reduceNodeKey);
					if (aggregates.get(a).getAxis().equals(tempAxis+"_"+collectionID+reduceNodeKey)) {
						stretchBuilder.append(createTempAggWCPSString(filters, aggregates, reduceNodeKey, collectionVar, collectionID, aggregates.get(a), tempAxis));
						log.debug(tempAxis);
						log.debug(payLoad);
						log.debug(reduceNodeKey);
						String replaceDate = Pattern.compile(tempAxis+"\\(.*?\\)").matcher(payLoad).replaceAll(tempAxis+"\\(\\$pm\\"+ reduceNodeKey + ")");
						//String replaceDate = wcpsPayLoad.toString().replaceAll(tempAxis+"\\(.*?\\)", tempAxis+"\\(\\$pm\\)");
						StringBuilder wcpsAggBuilderMod = new StringBuilder("");
						wcpsAggBuilderMod.append(replaceDate);
						stretchBuilder.append(wcpsAggBuilderMod);
						stretchString=stretchBuilder.toString();
					}
				}
			}
			return stretchString;
		}
		
		private String createTempAggWCPSString(Vector<Filter> filters, Vector<Aggregate> aggregates, String reduceNodeKey, String collectionVar, String collectionID, Aggregate tempAggregate, String tempAxis) {
			String operator = tempAggregate.getOperator();
			Filter tempFilter = null;
			for (Filter filter : filters) {
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
				for (Filter filter : filters) {
					System.err.println(filter.getAxis());
				}
				// TODO this error needs to be communicated to end user
				// meaning no appropriate filter found for running the condense operator in
				// temporal axis.
				return "";
			}
		}

		private String createTrigWCPSString(String trigNodeKey, String payLoad, JSONObject reduceProcesses, String name) {
			String stretchString = null;
				StringBuilder stretchBuilder = new StringBuilder("");
				stretchBuilder.append(name + "(" + payLoad + ")");
				stretchString = stretchBuilder.toString();
			
			return stretchString;
		}
		
		private String createMeanTempAggWCPSString(Vector<Filter> filters, Vector<Aggregate> aggregates, String reduceNodeKey, String collectionVar, String collectionID, Aggregate tempAggregate, String payLoad, String tempAxis) {
			Filter tempFilter = null;
			for (Filter filter : filters) {
				log.debug("Filter Axis is : ");
				log.debug(filter.getAxis());
				log.debug("Collection ID is : ");
				log.debug(collectionID);
				String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
				if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
					tempFilter = filter;
					log.debug("TempHigh"+tempFilter.getUpperBound());
					log.debug("Temporal Axis is : ");
					log.debug(tempFilter.getAxis());
				}
			}
			log.debug("Filters are : ");
			log.debug(filters);
			log.debug("Temporal filter is : ");
			log.debug(tempFilter);
			if (tempFilter != null) {
				StringBuilder stringBuilder = new StringBuilder("(condense + ");
				stringBuilder.append("over $pm" + reduceNodeKey + " t (imageCrsDomain(");
				stringBuilder.append(createFilteredCollectionString(collectionVar, collectionID, tempFilter) + ",");
				stringBuilder.append(tempAxis + ")) using ");
				String meanDateRange1 = Pattern.compile(tempAxis+"\\(.*?\\)").matcher(payLoad).replaceAll(tempAxis+"\\(\\$pm\\" + reduceNodeKey + ")");
				stringBuilder.append(meanDateRange1 + ")/( condense + over $pmm" + reduceNodeKey + " t (imageCrsDomain(" + payLoad + ",");
				stringBuilder.append(tempAxis + ")) using 1)");			
				//this.filters.remove(tempFilter);
				//this.filters.add(new Filter(axis, "$pm"));
				return stringBuilder.toString();
			} else {
				for (Filter filter : filters) {
					System.err.println(filter.getAxis());
				}
				// TODO this error needs to be communicated to end user
				// meaning no appropriate filter found for running the condense operator in temporal axis.
				return "";
			}
		}
		
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
//				DateFormat toDateNewFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//				Date toDateNew;			
//				try {
//					toDateNew = toDateNewFormat.parse(toDate);
//					toDateNew.setTime(toDateNew.getTime() - 1);
//					toDate = toDateNewFormat.format(toDateNew);
//					log.debug("To Date"+toDate);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
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
		
		private String createLinearScaleRangeWCPSString(String linearScaleNodeKey, String payLoad, JSONObject process) {
			JSONObject scaleArgumets = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments");
			double inputMin = 0;
			double inputMax = 0;
			double outputMin = 0;
			double outputMax = 1;
			inputMin = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("inputMin");
			inputMax = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("inputMax");

			for (String outputMinMax : scaleArgumets.keySet()) {	
				if (outputMinMax.contentEquals("outputMin")) {
					outputMin = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("outputMin");	        
				}
				else if (outputMinMax.contentEquals("outputMax")) {
					outputMax = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("outputMax");
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
		
		private String createBandSubsetString(String collectionName, String bandName, String subsetString) {
			StringBuilder stringBuilder = new StringBuilder(collectionName);
			stringBuilder.append(subsetString);
			stringBuilder.append(".");
			stringBuilder.append(bandName);		
			return stringBuilder.toString();
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
		
		// Always add the new parameter name added in 'arguments' field of any callback process under reduce process if recently defined by openEO API for reduce process
		private JSONArray getReducerFromNodes(String currentNode, JSONObject reduceProcesses) {
			JSONObject nextNodeName = new JSONObject();
			JSONArray fromNodes = new JSONArray();
			String nextFromNode = null;
			JSONObject reducerProcessArguments =  reduceProcesses.getJSONObject(currentNode).getJSONObject("arguments");
			for (String argumentsKey : reducerProcessArguments.keySet()) {
				if (argumentsKey.contentEquals("data")) {
					if (reducerProcessArguments.get("data") instanceof JSONObject) {
						for (String fromKey : reducerProcessArguments.getJSONObject("data").keySet()) {
							if (fromKey.contentEquals("from_node")) {
								nextFromNode = reducerProcessArguments.getJSONObject("data").getString("from_node");
								fromNodes.put(nextFromNode);
							}
						}
					}
					else if (reducerProcessArguments.get("data") instanceof JSONArray) {
						JSONArray reduceData = reducerProcessArguments.getJSONArray("data");
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
				if (argumentsKey.contentEquals("x")) {
					if (reducerProcessArguments.get("x") instanceof JSONObject) {
						for (String fromKey : reducerProcessArguments.getJSONObject("x").keySet()) {
							if (fromKey.contentEquals("from_node")) {
								nextFromNode = reducerProcessArguments.getJSONObject("x").getString("from_node");
								fromNodes.put(nextFromNode);
							}
						}
					}
					else if (reducerProcessArguments.get("x") instanceof JSONArray) {
						JSONArray reduceData = reducerProcessArguments.getJSONArray("x");
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
				if (argumentsKey.contentEquals("y")) {
					if (reducerProcessArguments.get("y") instanceof JSONObject) {
						for (String fromKey : reducerProcessArguments.getJSONObject("y").keySet()) {
							if (fromKey.contentEquals("from_node")) {
								nextFromNode = reducerProcessArguments.getJSONObject("y").getString("from_node");
								fromNodes.put(nextFromNode);
							}
						}
					}
					else if (reducerProcessArguments.get("y") instanceof JSONArray) {
						JSONArray reduceData = reducerProcessArguments.getJSONArray("y");
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
				if (argumentsKey.contentEquals("value")) {
					if (reducerProcessArguments.get("value") instanceof JSONObject) {
						for (String fromKey : reducerProcessArguments.getJSONObject("value").keySet()) {
							if (fromKey.contentEquals("from_node")) {
								nextFromNode = reducerProcessArguments.getJSONObject("value").getString("from_node");
								fromNodes.put(nextFromNode);
							}
						}
					}
					else if (reducerProcessArguments.get("value") instanceof JSONArray) {
						JSONArray reduceData = reducerProcessArguments.getJSONArray("value");
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
			}
			return fromNodes;
		}
		
		private String readAll(Reader rd) throws IOException {
			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
			return sb.toString();
		}

		private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
			log.debug("Trying to read JSON from the following URL : ");
			log.debug(url);
			InputStream is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				JSONObject json = new JSONObject(jsonText);
				return json;
			} finally {
				is.close();
			}
		}	
}