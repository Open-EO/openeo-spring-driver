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

public class WCPSApplyFunc {
	
	Logger log = LogManager.getLogger();
	private JSONObject processGraph;
	private String openEOEndpoint;
	private String wcpsEndpoint;

	// Create WCPS query for whole Apply Process Callback Functions
	String createApplyWCPSString(String wcpsEndpoint, String openEOEndpoint, JSONObject openEOGraph,
			String applyNodeKey, String payLoad, String filterString, String collectionVar, String collectionID) {
		String applyBuilderExtend = null;
		this.processGraph = openEOGraph;
		this.openEOEndpoint = openEOEndpoint;
		this.wcpsEndpoint = wcpsEndpoint;
		JSONObject applyProcesses = processGraph.getJSONObject(applyNodeKey).getJSONObject("arguments")
				.getJSONObject("process").getJSONObject("process_graph");
		JSONObject applyPayLoads = new JSONObject();
		JSONArray applyNodesArray = new JSONArray();
		String endApplyNode = null;
		JSONArray endApplyNodeAsArray = new JSONArray();

		for (String applyProcessKey : applyProcesses.keySet()) {
			JSONObject applyProcess = applyProcesses.getJSONObject(applyProcessKey);
			for (String applierField : applyProcess.keySet()) {
				if (applierField.equals("result")) {
					Boolean resultFlag = applyProcess.getBoolean("result");
					if (resultFlag) {
						endApplyNode = applyProcessKey;
						endApplyNodeAsArray.put(endApplyNode);
						log.debug("End Apply Process is : "
								+ applyProcesses.getJSONObject(endApplyNode).getString("process_id"));
					}
				}
			}
		}

		JSONArray applyNodesSortedArray = new JSONArray();
		applyNodesArray.put(endApplyNodeAsArray);
		for (int n = 0; n < applyNodesArray.length(); n++) {
			for (int a = 0; a < applyNodesArray.getJSONArray(n).length(); a++) {
				JSONArray fromNodeOfApplyProcesses = getApplyFromNodes(applyNodesArray.getJSONArray(n).getString(a),
						applyProcesses);
				if (fromNodeOfApplyProcesses.length() > 0) {
					applyNodesArray.put(fromNodeOfApplyProcesses);
				} else if (fromNodeOfApplyProcesses.length() == 0) {
					applyNodesSortedArray.put(applyNodesArray.getJSONArray(n).getString(a));
				}
			}
		}

		for (int i = 0; i < applyNodesSortedArray.length(); i++) {
			for (int j = i + 1; j < applyNodesSortedArray.length(); j++) {
				if (applyNodesSortedArray.get(i).equals(applyNodesSortedArray.get(j))) {
					applyNodesSortedArray.remove(j);
				}
			}
		}

		applyNodesArray.remove(applyNodesArray.length() - 1);
		for (int i = applyNodesArray.length() - 1; i > 0; i--) {
			if (applyNodesArray.getJSONArray(i).length() > 0) {
				for (int a = 0; a < applyNodesArray.getJSONArray(i).length(); a++) {
					applyNodesSortedArray.put(applyNodesArray.getJSONArray(i).getString(a));
				}
			}
		}

		applyNodesSortedArray.put(endApplyNode);
		for (int i = 0; i < applyNodesSortedArray.length(); i++) {
			for (int j = i + 1; j < applyNodesSortedArray.length(); j++) {
				if (applyNodesSortedArray.get(i).equals(applyNodesSortedArray.get(j))) {
					applyNodesSortedArray.remove(j);
				}
			}
		}

		JSONArray applyProcessesSequence = new JSONArray();
		for (int i = 0; i < applyNodesSortedArray.length(); i++) {
			applyProcessesSequence
					.put(applyProcesses.getJSONObject(applyNodesSortedArray.getString(i)).getString("process_id"));
		}

		log.debug("Apply's Nodes Sequence is : ");
		log.debug(applyNodesSortedArray);
		log.debug("Apply's Processes Sequence is : ");
		log.debug(applyProcessesSequence);

		for (int r = 0; r < applyNodesSortedArray.length(); r++) {
			String nodeKey = applyNodesSortedArray.getString(r);
			String name = applyProcesses.getJSONObject(nodeKey).getString("process_id");

			if (name.contains("linear_scale_range")) {
				String x = null;
				JSONObject linearScaleRangeArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : linearScaleRangeArguments.keySet()) {
					if ((argType.equals("x")) && linearScaleRangeArguments.get(argType) instanceof JSONObject) {
						for (String fromType : linearScaleRangeArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_parameter") && linearScaleRangeArguments.getJSONObject(argType)
									.getString("from_parameter").equals("x")) {
								x = payLoad;
							} else if (fromType.equals("from_node")) {
								String dataNode = linearScaleRangeArguments.getJSONObject(argType)
										.getString("from_node");
								String linearScaleRangePayLoad = applyPayLoads.getString(dataNode);
								x = linearScaleRangePayLoad;
							}
						}
					} else if (argType.equals("x") && linearScaleRangeArguments.get(argType) instanceof Double) {
						x = String.valueOf(linearScaleRangeArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createLinearScaleRangeWCPSString(nodeKey, x, applyProcesses);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Linear Scale Range Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("absolute")) {
				String x = null;
				JSONObject absArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");

				if (absArguments.get("x") instanceof JSONObject) {
					for (String fromType : absArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& absArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							x = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNode = absArguments.getJSONObject("x").getString("from_node");
							String absPayLoad = applyPayLoads.getString(dataNode);
							x = absPayLoad;
						}
					}
				} else if (absArguments.get("x") instanceof Double) {
					x = String.valueOf(absArguments.getDouble("x"));
				}

				applyBuilderExtend = createAbsWCPSString(x);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Absolute Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("not")) {
				String x = null;
				JSONObject notArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");

				if (notArguments.get("x") instanceof JSONObject) {
					for (String fromType : notArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& notArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							x = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNode = notArguments.getJSONObject("x").getString("from_node");
							String notPayLoad = applyPayLoads.getString(dataNode);
							x = notPayLoad;
						}
					}
				} else if (notArguments.get("x") instanceof Boolean) {
					x = String.valueOf(notArguments.getBoolean("x"));
				}

				applyBuilderExtend = createNotWCPSString(x);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("NOT Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("log")) {
				String x = null;
				JSONObject logArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : logArguments.keySet()) {
					if ((argType.equals("x")) && logArguments.get(argType) instanceof JSONObject) {
						for (String fromType : logArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_parameter")
									&& logArguments.getJSONObject(argType).getString("from_parameter").equals("x")) {
								x = payLoad;
							} else if (fromType.equals("from_node")) {
								String dataNode = logArguments.getJSONObject(argType).getString("from_node");
								String logPayLoad = applyPayLoads.getString(dataNode);
								x = logPayLoad;
							}
						}
					} else if (argType.equals("x") && logArguments.get(argType) instanceof Double) {
						x = String.valueOf(logArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createLogWCPSString(x);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Log Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("ln")) {
				String x = null;
				JSONObject logNArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : logNArguments.keySet()) {
					if ((argType.equals("x")) && logNArguments.get(argType) instanceof JSONObject) {
						for (String fromType : logNArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_parameter")
									&& logNArguments.getJSONObject(argType).getString("from_parameter").equals("x")) {
								x = payLoad;
							} else if (fromType.equals("from_node")) {
								String dataNode = logNArguments.getJSONObject(argType).getString("from_node");
								String logNPayLoad = applyPayLoads.getString(dataNode);
								x = logNPayLoad;
							}
						}
					} else if (argType.equals("x") && logNArguments.get(argType) instanceof Double) {
						x = String.valueOf(logNArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createLogNWCPSString(x);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Natural Log Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("sqrt")) {
				String x = null;
				JSONObject sqrtArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : sqrtArguments.keySet()) {
					if ((argType.equals("x")) && sqrtArguments.get(argType) instanceof JSONObject) {
						for (String fromType : sqrtArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_parameter")
									&& sqrtArguments.getJSONObject(argType).getString("from_parameter").equals("x")) {
								x = payLoad;
							} else if (fromType.equals("from_node")) {
								String dataNode = sqrtArguments.getJSONObject(argType).getString("from_node");
								String sqrtPayLoad = applyPayLoads.getString(dataNode);
								x = sqrtPayLoad;
							}
						}
					} else if (argType.equals("x") && sqrtArguments.get(argType) instanceof Double) {
						x = String.valueOf(sqrtArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createSqrtWCPSString(x);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Square Root Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("power")) {
				String base = null;
				JSONObject powArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : powArguments.keySet()) {
					if ((argType.equals("base")) && powArguments.get(argType) instanceof JSONObject) {
						for (String fromType : powArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_parameter")
									&& powArguments.getJSONObject(argType).getString("from_parameter").equals("x")) {
								base = payLoad;
							} else if (fromType.equals("from_node")) {
								String dataNode = powArguments.getJSONObject(argType).getString("from_node");
								String powPayLoad = applyPayLoads.getString(dataNode);
								base = powPayLoad;
							}
						}
					} else if (argType.equals("x") && powArguments.get(argType) instanceof Double) {
						base = String.valueOf(powArguments.getDouble("base"));
					}
				}
				applyBuilderExtend = createPowWCPSString(nodeKey, base, applyProcesses);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Power Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("exp")) {
				String p = null;
				JSONObject expArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : expArguments.keySet()) {
					if ((argType.equals("p")) && expArguments.get(argType) instanceof JSONObject) {
						for (String fromType : expArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_parameter")
									&& expArguments.getJSONObject(argType).getString("from_parameter").equals("x")) {
								p = payLoad;
							} else if (fromType.equals("from_node")) {
								String dataNode = expArguments.getJSONObject(argType).getString("from_node");
								String expPayLoad = applyPayLoads.getString(dataNode);
								p = expPayLoad;
							}
						}
					} else if (argType.equals("x") && expArguments.get(argType) instanceof Double) {
						p = String.valueOf(expArguments.getDouble("p"));
					}
				}
				applyBuilderExtend = createExpWCPSString(p);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Exponential Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("pi")) {
				applyBuilderExtend = createPiWCPSString();
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Pi Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("e")) {
				applyBuilderExtend = createEulerNumWCPSString();
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Euler's Constant Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("sin") || name.equals("cos") || name.equals("tan") || name.equals("sinh")
					|| name.equals("cosh") || name.equals("tanh") || name.equals("arcsin") || name.equals("arccos")
					|| name.equals("arctan")) {
				String x = null;
				JSONObject trigArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : trigArguments.keySet()) {
					if ((argType.equals("x")) && trigArguments.get(argType) instanceof JSONObject) {
						for (String fromType : trigArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_parameter")
									&& trigArguments.getJSONObject(argType).getString("from_parameter").equals("x")) {
								x = payLoad;
							} else if (fromType.equals("from_node")) {
								String dataNode = trigArguments.getJSONObject(argType).getString("from_node");
								String trigPayLoad = applyPayLoads.getString(dataNode);
								x = trigPayLoad;
							}
						}
					} else if (argType.equals("x") && trigArguments.get(argType) instanceof Double) {
						x = String.valueOf(trigArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createTrigWCPSString(nodeKey, x, applyProcesses, name);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Trigonometric Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("if")) {
				StringBuilder wcpsIFpayLoad = new StringBuilder("");
				String acceptPayLoad = null;
				String rejectPayLoad = null;
				double accept = 0;
				double reject = 0;
				JSONObject ifArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");

				if (ifArguments.get("value") instanceof JSONObject) {
					for (String fromType : ifArguments.getJSONObject("value").keySet()) {
						if (fromType.equals("from_node")) {
							String dataNode = ifArguments.getJSONObject("value").getString("from_node");
							payLoad = applyPayLoads.getString(dataNode);
							log.debug("IF Process : ");
							if (ifArguments.get("accept") instanceof JSONObject) {
								String acceptDataNode = ifArguments.getJSONObject("accept").getString("from_node");
								acceptPayLoad = applyPayLoads.getString(acceptDataNode);
								log.debug("Accept Payload : " + acceptPayLoad);
							} else {
								accept = ifArguments.getDouble("accept");
								log.debug("Accept Payload : " + accept);
							}
							if (ifArguments.get("reject") instanceof JSONObject) {
								String rejectDataNode = ifArguments.getJSONObject("reject").getString("from_node");
								rejectPayLoad = applyPayLoads.getString(rejectDataNode);
								log.debug("Reject Payload : " + rejectPayLoad);
							} else {
								reject = ifArguments.getDouble("reject");
								log.debug("Reject Payload : " + reject);
							}
						}
					}
				}

				if (ifArguments.get("accept") instanceof JSONObject) {
					if (ifArguments.get("reject") instanceof JSONObject) {
						wcpsIFpayLoad.append("(" + payLoad + "*" + acceptPayLoad + "+" + "(not "
								+ payLoad.replaceAll("pm", "pm" + applyNodeKey + nodeKey)
										.replaceAll("merge", "merge" + applyNodeKey + nodeKey)
										.replaceAll("\\$T", "\\$T" + applyNodeKey + nodeKey)
										.replaceAll("\\$Y", "\\$Y" + applyNodeKey + nodeKey)
										.replaceAll("\\$X", "\\$X" + applyNodeKey + nodeKey)
										.replaceAll("\\$N", "\\$N" + applyNodeKey + nodeKey).replaceAll("\\$E",
												"\\$E" + applyNodeKey + nodeKey)
								+ ")*" + rejectPayLoad + ")");
					} else {
						wcpsIFpayLoad.append("(" + payLoad + "*" + acceptPayLoad + "+" + "(not "
								+ payLoad.replaceAll("pm", "pm" + applyNodeKey + nodeKey)
										.replaceAll("\\$T", "\\$T" + applyNodeKey + nodeKey)
										.replaceAll("\\$Y", "\\$Y" + applyNodeKey + nodeKey)
										.replaceAll("\\$X", "\\$X" + applyNodeKey + nodeKey)
										.replaceAll("\\$N", "\\$N" + applyNodeKey + nodeKey)
										.replaceAll("\\$E", "\\$E" + applyNodeKey + nodeKey)
								+ ")*" + reject + ")");
					}
				} else {
					if (ifArguments.get("reject") instanceof JSONObject) {
						wcpsIFpayLoad.append("(" + payLoad + "*" + accept + "+" + "(not "
								+ payLoad.replaceAll("pm", "pm" + applyNodeKey + nodeKey)
										.replaceAll("\\$T", "\\$T" + applyNodeKey + nodeKey)
										.replaceAll("\\$Y", "\\$Y" + applyNodeKey + nodeKey)
										.replaceAll("\\$X", "\\$X" + applyNodeKey + nodeKey)
										.replaceAll("\\$N", "\\$N" + applyNodeKey + nodeKey).replaceAll("\\$E",
												"\\$E" + applyNodeKey + nodeKey)
								+ ")*" + rejectPayLoad + ")");
					} else {
						wcpsIFpayLoad.append("(" + payLoad + "*" + accept + "+" + "(not "
								+ payLoad.replaceAll("pm", "pm" + applyNodeKey + nodeKey)
										.replaceAll("\\$T", "\\$T" + applyNodeKey + nodeKey)
										.replaceAll("\\$Y", "\\$Y" + applyNodeKey + nodeKey)
										.replaceAll("\\$X", "\\$X" + applyNodeKey + nodeKey)
										.replaceAll("\\$N", "\\$N" + applyNodeKey + nodeKey)
										.replaceAll("\\$E", "\\$E" + applyNodeKey + nodeKey)
								+ ")*" + reject + ")");
					}
				}

				applyBuilderExtend = wcpsIFpayLoad.toString();
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Process Stored for Node " + nodeKey + " : " + applyPayLoads.get(nodeKey));
				log.debug("IF Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("gte")) {
				String x = null;
				String y = null;
				JSONObject gteArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (gteArguments.get("x") instanceof JSONObject) {
					for (String fromType : gteArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& gteArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							x = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeX = gteArguments.getJSONObject("x").getString("from_node");
							String gtePayLoadX = applyPayLoads.getString(dataNodeX);
							x = gtePayLoadX;
						}
					}
				} else {
					x = String.valueOf(gteArguments.getDouble("x"));
				}
				if (gteArguments.get("y") instanceof JSONObject) {
					for (String fromType : gteArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& gteArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							y = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeY = gteArguments.getJSONObject("y").getString("from_node");
							String gtePayLoadY = applyPayLoads.getString(dataNodeY);
							y = gtePayLoadY;
						}
					}
				} else {
					y = String.valueOf(gteArguments.getDouble("y"));
				}

				applyBuilderExtend = createGreatThanEqWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Greater Than Equal Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("gt")) {
				String x = null;
				String y = null;
				JSONObject gtArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");

				if (gtArguments.get("x") instanceof JSONObject) {
					for (String fromType : gtArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& gtArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							x = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeX = gtArguments.getJSONObject("x").getString("from_node");
							String gtPayLoadX = applyPayLoads.getString(dataNodeX);
							x = gtPayLoadX;
						}
					}
				} else {
					x = String.valueOf(gtArguments.getDouble("x"));
				}
				if (gtArguments.get("y") instanceof JSONObject) {
					for (String fromType : gtArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& gtArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							y = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeY = gtArguments.getJSONObject("y").getString("from_node");
							String gtPayLoadY = applyPayLoads.getString(dataNodeY);
							y = gtPayLoadY;
						}
					}
				} else {
					y = String.valueOf(gtArguments.getDouble("y"));
				}

				applyBuilderExtend = createGreatThanWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Greater Than Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("lte")) {
				String x = null;
				String y = null;

				JSONObject lteArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (lteArguments.get("x") instanceof JSONObject) {
					for (String fromType : lteArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& lteArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							x = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeX = lteArguments.getJSONObject("x").getString("from_node");
							String ltePayLoadX = applyPayLoads.getString(dataNodeX);
							x = ltePayLoadX;
						}
					}
				} else {
					x = String.valueOf(lteArguments.getDouble("x"));
				}
				if (lteArguments.get("y") instanceof JSONObject) {
					for (String fromType : lteArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& lteArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							y = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeY = lteArguments.getJSONObject("y").getString("from_node");
							String ltePayLoadY = applyPayLoads.getString(dataNodeY);
							y = ltePayLoadY;
						}
					}
				} else {
					y = String.valueOf(lteArguments.getDouble("y"));
				}

				applyBuilderExtend = createLessThanEqWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Less Than Equal Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}

			if (name.equals("lt")) {
				String x = null;
				String y = null;
				JSONObject ltArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (ltArguments.get("x") instanceof JSONObject) {
					for (String fromType : ltArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& ltArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							x = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeX = ltArguments.getJSONObject("x").getString("from_node");
							String ltPayLoadX = applyPayLoads.getString(dataNodeX);
							x = ltPayLoadX;
						}
					}
				} else {
					x = String.valueOf(ltArguments.getDouble("x"));
				}
				if (ltArguments.get("y") instanceof JSONObject) {
					for (String fromType : ltArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& ltArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							y = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeY = ltArguments.getJSONObject("y").getString("from_node");
							String ltPayLoadY = applyPayLoads.getString(dataNodeY);
							y = ltPayLoadY;
						}
					}
				} else {
					y = String.valueOf(ltArguments.getInt("y"));
				}

				applyBuilderExtend = createLessThanWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Less Than Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("neq")) {
				String x = null;
				String y = null;

				JSONObject neqArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (neqArguments.get("x") instanceof JSONObject) {
					for (String fromType : neqArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& neqArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							x = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeX = neqArguments.getJSONObject("x").getString("from_node");
							String neqPayLoadX = applyPayLoads.getString(dataNodeX);
							x = neqPayLoadX;
						}
					}
				} else {
					x = String.valueOf(neqArguments.getDouble("x"));
				}
				if (neqArguments.get("y") instanceof JSONObject) {
					for (String fromType : neqArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& neqArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							y = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeY = neqArguments.getJSONObject("y").getString("from_node");
							String neqPayLoadY = applyPayLoads.getString(dataNodeY);
							y = neqPayLoadY;
						}
					}
				} else {
					y = String.valueOf(neqArguments.getDouble("y"));
				}

				applyBuilderExtend = createNotEqWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Not Equal Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("eq")) {
				String x = null;
				String y = null;
				JSONObject eqArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");

				if (eqArguments.get("x") instanceof JSONObject) {
					for (String fromType : eqArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& eqArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							x = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeX = eqArguments.getJSONObject("x").getString("from_node");
							String eqPayLoadX = applyPayLoads.getString(dataNodeX);
							x = eqPayLoadX;
						}
					}
				} else {
					x = String.valueOf(eqArguments.getDouble("x"));
				}
				if (eqArguments.get("y") instanceof JSONObject) {
					for (String fromType : eqArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& eqArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							y = payLoad;
						} else if (fromType.equals("from_node")) {
							String dataNodeY = eqArguments.getJSONObject("y").getString("from_node");
							String eqPayLoadY = applyPayLoads.getString(dataNodeY);
							y = eqPayLoadY;
						}
					}
				} else {
					y = String.valueOf(eqArguments.getDouble("y"));
				}

				applyBuilderExtend = createEqWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Equal Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("array_element")) {
				JSONObject arrayData = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				int arrayIndex = arrayData.getInt("index");
				if (arrayData.get("data") instanceof JSONObject) {
					for (String fromType : arrayData.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& arrayData.getJSONObject("data").getString("from_parameter").equals("x")) {
							// if (dataNode.equals(loadCollNode)) {
							applyBuilderExtend = createBandWCPSString(collectionID, arrayIndex, applyNodeKey,
									filterString, collectionVar);
							// }
						} else if (fromType.equals("from_node")) {
							applyBuilderExtend = createBandWCPSString(collectionID, arrayIndex, applyNodeKey,
									filterString, collectionVar);
						}
					}
				} else {
					applyBuilderExtend = arrayData.getJSONArray("data").getString(arrayIndex);
				}

				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Array Element Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
//				if (name.equals("count")) {
//					String x = null;
//					JSONObject countArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
//					if (countArguments.get("data") instanceof JSONObject) {
//						for (String fromType : countArguments.getJSONObject("data").keySet()) {
//							if (fromType.equals("from_parameter") && countArguments.getJSONObject("data").getString("from_parameter").equals("x")) {
//								x = payLoad;
//							}
//							else if (fromType.equals("from_node")) {
//								String dataNode = countArguments.getJSONObject("data").getString("from_node");
//								String countPayLoad = applyPayLoads.getString(dataNode);
//								x = countPayLoad;
//							}
//						}
//					}
//					else {
//						x = String.valueOf(countArguments.getJSONArray("data"));
//					}
//					applyBuilderExtend = createCountWCPSString(x);
//					applyPayLoads.put(nodeKey, applyBuilderExtend);
//					log.debug("Count Process PayLoad is : ");
//					log.debug(applyPayLoads.get(nodeKey));
//				}
			if (name.equals("and")) {
				JSONObject andArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				JSONArray andArrayreturn = new JSONArray();

				if (andArguments.get("x") instanceof JSONObject) {
					for (String fromType : andArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& andArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							andArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = andArguments.getJSONObject("x").getString("from_node");
							String andPayLoad = applyPayLoads.getString(dataNode);
							andArrayreturn.put(andPayLoad);
						}
					}
				} else if (andArguments.get("x") instanceof Boolean) {
					andArrayreturn.put(andArguments.getBoolean("x"));
				}

				if (andArguments.get("y") instanceof JSONObject) {
					for (String fromType : andArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& andArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							andArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = andArguments.getJSONObject("y").getString("from_node");
							String andPayLoad = applyPayLoads.getString(dataNode).replaceAll("\\$pm", "\\$pm" + nodeKey)
									.replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey)
									.replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey)
									.replaceAll("\\$E", "\\$E" + nodeKey);
							andArrayreturn.put(andPayLoad);
						}
					}
				} else if (andArguments.get("y") instanceof Boolean) {
					andArrayreturn.put(andArguments.getBoolean("y"));
				}

				applyBuilderExtend = createANDWCPSString(andArrayreturn);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("AND Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("or")) {
				JSONObject orArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				JSONArray orArrayreturn = new JSONArray();

				if (orArguments.get("x") instanceof JSONObject) {
					for (String fromType : orArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& orArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							orArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = orArguments.getJSONObject("x").getString("from_node");
							String orPayLoad = applyPayLoads.getString(dataNode);
							orArrayreturn.put(orPayLoad);
						}
					}
				} else if (orArguments.get("x") instanceof Boolean) {
					orArrayreturn.put(orArguments.getBoolean("x"));
				}

				if (orArguments.get("y") instanceof JSONObject) {
					for (String fromType : orArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& orArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							orArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = orArguments.getJSONObject("y").getString("from_node");
							String orPayLoad = applyPayLoads.getString(dataNode);
							orPayLoad = orPayLoad.replaceAll("\\$pm", "\\$pm" + nodeKey)
									.replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey)
									.replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey)
									.replaceAll("\\$E", "\\$E" + nodeKey);
							orArrayreturn.put(orPayLoad);
						}
					}
				} else if (orArguments.get("y") instanceof Boolean) {
					orArrayreturn.put(orArguments.getBoolean("y"));
				}

				applyBuilderExtend = createORWCPSString(orArrayreturn);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("OR Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("xor")) {
				JSONObject xorArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				JSONArray xorArrayreturn = new JSONArray();

				if (xorArguments.get("x") instanceof JSONObject) {
					for (String fromType : xorArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& xorArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							xorArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = xorArguments.getJSONObject("x").getString("from_node");
							String xorPayLoad = applyPayLoads.getString(dataNode);
							xorArrayreturn.put(xorPayLoad);
						}
					}
				} else if (xorArguments.get("x") instanceof Boolean) {
					xorArrayreturn.put(xorArguments.getBoolean("x"));
				}

				if (xorArguments.get("y") instanceof JSONObject) {
					for (String fromType : xorArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& xorArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							xorArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = xorArguments.getJSONObject("y").getString("from_node");
							String xorPayLoad = applyPayLoads.getString(dataNode).replaceAll("\\$pm", "\\$pm" + nodeKey)
									.replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey)
									.replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey)
									.replaceAll("\\$E", "\\$E" + nodeKey);
							xorArrayreturn.put(xorPayLoad);
						}
					}
				} else if (xorArguments.get("y") instanceof Boolean) {
					xorArrayreturn.put(xorArguments.getBoolean("y"));
				}

				applyBuilderExtend = createXORWCPSString(xorArrayreturn);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("XOR Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("multiply")) {
				JSONObject productArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				JSONArray productArrayreturn = new JSONArray();

				if (productArguments.get("x") instanceof JSONObject) {
					for (String fromType : productArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& productArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							productArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = productArguments.getJSONObject("x").getString("from_node");
							String productPayLoad = applyPayLoads.getString(dataNode)
									.replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey)
									.replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey)
									.replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
							productArrayreturn.put(productPayLoad);
						}
					}
				} else {
					productArrayreturn.put(productArguments.get("x"));
				}

				if (productArguments.get("y") instanceof JSONObject) {
					for (String fromType : productArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& productArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							productArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = productArguments.getJSONObject("y").getString("from_node");
							String productPayLoad = applyPayLoads.getString(dataNode);
							productArrayreturn.put(productPayLoad);
						}
					}
				} else {
					productArrayreturn.put(productArguments.get("y"));
				}

				applyBuilderExtend = createProductWCPSString(productArrayreturn);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Product Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("add")) {
				JSONObject sumArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				JSONArray sumArrayreturn = new JSONArray();

				if (sumArguments.get("x") instanceof JSONObject) {
					for (String fromType : sumArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& sumArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							sumArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = sumArguments.getJSONObject("x").getString("from_node");
							String sumPayLoad = applyPayLoads.getString(dataNode);
							sumArrayreturn.put(sumPayLoad);
						}
					}
				} else {
					sumArrayreturn.put(sumArguments.get("x"));
				}

				if (sumArguments.get("y") instanceof JSONObject) {
					for (String fromType : sumArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& sumArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							sumArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = sumArguments.getJSONObject("y").getString("from_node");
							String sumPayLoad = applyPayLoads.getString(dataNode).replaceAll("\\$pm", "\\$pm" + nodeKey)
									.replaceAll("\\$T", "\\$T" + nodeKey).replaceAll("\\$Y", "\\$Y" + nodeKey)
									.replaceAll("\\$X", "\\$X" + nodeKey).replaceAll("\\$N", "\\$N" + nodeKey)
									.replaceAll("\\$E", "\\$E" + nodeKey);
							sumArrayreturn.put(sumPayLoad);
						}
					}
				} else {
					sumArrayreturn.put(sumArguments.get("y"));
				}

				applyBuilderExtend = createSumWCPSString(sumArrayreturn);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Sum Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("subtract")) {
				JSONObject subtractArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				JSONArray subtractArrayreturn = new JSONArray();

				if (subtractArguments.get("x") instanceof JSONObject) {
					for (String fromType : subtractArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& subtractArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							subtractArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = subtractArguments.getJSONObject("x").getString("from_node");
							String subtractPayLoad = applyPayLoads.getString(dataNode)
									.replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey)
									.replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey)
									.replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
							subtractArrayreturn.put(subtractPayLoad);
						}
					}
				} else {
					subtractArrayreturn.put(subtractArguments.get("x"));
				}

				if (subtractArguments.get("y") instanceof JSONObject) {
					for (String fromType : subtractArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& subtractArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							subtractArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = subtractArguments.getJSONObject("y").getString("from_node");
							String subtractPayLoad = applyPayLoads.getString(dataNode);
							subtractArrayreturn.put(subtractPayLoad);
						}
					}
				} else {
					subtractArrayreturn.put(subtractArguments.get("y"));
				}

				applyBuilderExtend = createSubtractWCPSString(subtractArrayreturn);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Subtract Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("divide")) {
				JSONObject divideArguments = applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				JSONArray divideArrayreturn = new JSONArray();

				if (divideArguments.get("x") instanceof JSONObject) {
					for (String fromType : divideArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_parameter")
								&& divideArguments.getJSONObject("x").getString("from_parameter").equals("x")) {
							divideArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = divideArguments.getJSONObject("x").getString("from_node");
							String dividePayLoad = applyPayLoads.getString(dataNode);
							divideArrayreturn.put(dividePayLoad);
						}
					}
				} else {
					divideArrayreturn.put(divideArguments.get("x"));
				}

				if (divideArguments.get("y") instanceof JSONObject) {
					for (String fromType : divideArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_parameter")
								&& divideArguments.getJSONObject("y").getString("from_parameter").equals("x")) {
							divideArrayreturn.put(payLoad);
						} else if (fromType.equals("from_node")) {
							String dataNode = divideArguments.getJSONObject("y").getString("from_node");
							String dividePayLoad = applyPayLoads.getString(dataNode)
									.replaceAll("\\$pm", "\\$pm" + nodeKey).replaceAll("\\$T", "\\$T" + nodeKey)
									.replaceAll("\\$Y", "\\$Y" + nodeKey).replaceAll("\\$X", "\\$X" + nodeKey)
									.replaceAll("\\$N", "\\$N" + nodeKey).replaceAll("\\$E", "\\$E" + nodeKey);
							divideArrayreturn.put(dividePayLoad);
						}
					}
				} else {
					divideArrayreturn.put(divideArguments.get("y"));
				}

				applyBuilderExtend = createDivideWCPSString(divideArrayreturn);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Divide Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
		}
		return applyBuilderExtend;
	}

	private String createEqWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(x + " = " + y + ")");
		return stretchBuilder.toString();
	}

	private String createNotEqWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(x + " != " + y + ")");
		return stretchBuilder.toString();
	}

	private String createLessThanWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(x + " < " + y + ")");
		return stretchBuilder.toString();
	}

	private String createLessThanEqWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(x + " <= " + y + ")");
		return stretchBuilder.toString();
	}

	private String createGreatThanWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(x + " > " + y + ")");
		return stretchBuilder.toString();
	}

	private String createGreatThanEqWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(x + " >= " + y + ")");
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

	private String createBandWCPSString(String collectionID, int arrayIndex, String reduceNodeKey, String filterString,
			String collectionVar) {
		StringBuilder stretchBuilder = new StringBuilder("");
		String fromNodeOfReduce = processGraph.getJSONObject(reduceNodeKey).getJSONObject("arguments")
				.getJSONObject("data").getString("from_node");
		fromNodeOfReduce = getFilterCollectionNode(fromNodeOfReduce);
		JSONObject fromProcess = processGraph.getJSONObject(fromNodeOfReduce);

		if (fromProcess.getString("process_id").equals("load_collection")) {
			String bandfromIndex = fromProcess.getJSONObject("arguments").getJSONArray("bands").getString(arrayIndex);
			String bandName = null;

			try {
				URL url = new URL(wcpsEndpoint + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID="
						+ collectionID);

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
					if (current.getPrefix().equals("gml")) {
						gmlNS = current;
					}
				}

				Boolean bandsMeta = false;
				Element metadataElement = null;
				try {
					metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS)
							.getChild("Extension", gmlNS).getChild("covMetadata", gmlNS);
				} catch (Exception e) {
				}
				List<Element> bandsList = null;
				try {
					bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
					bandsMeta = true;
				} catch (Exception e) {
				}
				if (bandsMeta) {
					try {
						for (int c = 0; c < bandsList.size(); c++) {
							String bandCommonName = null;
							Element band = bandsList.get(c);
							try {
								bandCommonName = band.getChildText("common_name");
								if (bandCommonName.equals(bandfromIndex)) {
									bandName = band.getChildText("name");
									break;
								} else {
									bandName = bandfromIndex;
								}
							} catch (Exception e) {
								bandName = bandfromIndex;
							}
						}
					} catch (Exception e) {
					}
				}
			} catch (MalformedURLException e) {
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
		StringBuilder stretchBuilder = new StringBuilder("(" + productArrayreturn.get(0));
		for (int f = 1; f < productArrayreturn.length(); f++) {
			stretchBuilder.append(" * " + productArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createSumWCPSString(JSONArray sumArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("(" + sumArrayreturn.get(0));
		for (int f = 1; f < sumArrayreturn.length(); f++) {
			stretchBuilder.append(" + " + sumArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createSubtractWCPSString(JSONArray subtractArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("(" + subtractArrayreturn.get(0));
		for (int f = 1; f < subtractArrayreturn.length(); f++) {
			stretchBuilder.append(" - " + subtractArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createDivideWCPSString(JSONArray divideArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("(" + divideArrayreturn.get(0));
		for (int f = 1; f < divideArrayreturn.length(); f++) {
			stretchBuilder.append(" / " + divideArrayreturn.get(f));
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

	private String createTrigWCPSString(String trigNodeKey, String payLoad, JSONObject reduceProcesses, String name) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");
		stretchBuilder.append(name + "(" + payLoad + ")");
		stretchString = stretchBuilder.toString();

		return stretchString;
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
			} else if (outputMinMax.contentEquals("outputMax")) {
				outputMax = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("outputMax");
			}
		}

		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(payLoad + ")");
		String stretchString = stretchBuilder.toString();
		StringBuilder stretchBuilderExtend = new StringBuilder("(unsigned char)(");
		stretchBuilderExtend.append("(" + stretchString + " + " + (-inputMin) + ")");
		stretchBuilderExtend.append("*(" + outputMax + "/" + (inputMax - inputMin) + ")");
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
			} else if (argumentsKey.contentEquals("data")) {
				String filterfromNode = loadCollectionNodeKeyArguments.getJSONObject("data").getString("from_node");
				filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
			} else if (argumentsKey.contentEquals("cube1")) {
				String filterfromNode = loadCollectionNodeKeyArguments.getJSONObject("cube1").getString("from_node");
				filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
			} else if (argumentsKey.contentEquals("x")) {
				String filterfromNode = loadCollectionNodeKeyArguments.getJSONObject("x").getString("from_node");
				filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
			} else if (argumentsKey.contentEquals("value")) {
				String filterfromNode = loadCollectionNodeKeyArguments.getJSONObject("value").getString("from_node");
				filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
			}
		}
		return filterCollectionNodeKey;
	}

	// Always add the new parameter name added in 'arguments' field of any callback
	// process under apply process if recently defined by openEO API for apply
	// process
	private JSONArray getApplyFromNodes(String currentNode, JSONObject applyProcesses) {
		JSONObject nextNodeName = new JSONObject();
		JSONArray fromNodes = new JSONArray();
		String nextFromNode = null;
		JSONObject applyProcessArguments = applyProcesses.getJSONObject(currentNode).getJSONObject("arguments");
		for (String argumentsKey : applyProcessArguments.keySet()) {
			if (argumentsKey.contentEquals("data")) {
				if (applyProcessArguments.get("data") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("data").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				} else if (applyProcessArguments.get("data") instanceof JSONArray) {
					JSONArray reduceData = applyProcessArguments.getJSONArray("data");
					for (int a = 0; a < reduceData.length(); a++) {
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
				if (applyProcessArguments.get("x") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("x").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("x").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				} else if (applyProcessArguments.get("x") instanceof JSONArray) {
					JSONArray reduceData = applyProcessArguments.getJSONArray("x");
					for (int a = 0; a < reduceData.length(); a++) {
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
				if (applyProcessArguments.get("y") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("y").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("y").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				} else if (applyProcessArguments.get("y") instanceof JSONArray) {
					JSONArray reduceData = applyProcessArguments.getJSONArray("y");
					for (int a = 0; a < reduceData.length(); a++) {
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
				if (applyProcessArguments.get("value") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("value").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("value").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				} else if (applyProcessArguments.get("value") instanceof JSONArray) {
					JSONArray reduceData = applyProcessArguments.getJSONArray("value");
					for (int a = 0; a < reduceData.length(); a++) {
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
			if (argumentsKey.contentEquals("accept")) {
				if (applyProcessArguments.get("accept") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("accept").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("accept").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);
			}

			if (argumentsKey.contentEquals("reject")) {
				if (applyProcessArguments.get("reject") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("reject").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("reject").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);
			}
		}
		return fromNodes;
	}
}