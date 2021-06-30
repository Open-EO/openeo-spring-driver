package org.openeo.spring.components;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openeo.spring.api.ResultApiController;
import org.openeo.spring.dao.BatchJobResultDAO;
import org.openeo.spring.dao.JobDAO;
import org.openeo.spring.model.Asset;
import org.openeo.spring.model.BatchJobResult;
import org.openeo.spring.model.EngineTypes;
import org.openeo.spring.model.Error;
import org.openeo.spring.model.Job;
import org.openeo.spring.model.JobStates;
import org.openeo.wcps.ConvenienceHelper;
import org.openeo.wcps.HyperCubeFactory;
import org.openeo.wcps.UDFFactory;
import org.openeo.wcps.WCPSQueryFactory;
import org.openeo.wcps.events.JobEvent;
import org.openeo.wcps.events.JobEventListener;
import org.openeo.wcps.events.UDFEvent;
import org.openeo.wcps.events.UDFEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JobScheduler implements JobEventListener, UDFEventListener {

	Logger log = LogManager.getLogger(JobScheduler.class);

	JobDAO jobDAO;

	BatchJobResultDAO resultDAO;

	@Autowired
	public void setDao(JobDAO injectedJObDAO, BatchJobResultDAO injectResultDao) {
		jobDAO = injectedJObDAO;
		resultDAO = injectResultDao;
	}
	
	@Autowired
	private CollectionMap collectionMap;

	@Value("${org.openeo.wcps.endpoint}")
	private String wcpsEndpoint;

	@Value("${org.openeo.endpoint}")
	private String openEOEndpoint;
	
	@Value("${org.openeo.public.endpoint}")
	private String openEOPublicEndpoint;

	@Value("${org.openeo.odc.endpoint}")
	private String odcEndpoint;

	@Value("${org.openeo.tmp.dir}")
	private String tmpDir;
	
	@Value("${org.openeo.udf.dir}")
	private String tmpDirUDF;
	
	@Value("${org.openeo.udf.importscript}")
	private String udfimport;

	@Value("{org.openeo.udf.python.endpoint}")
	private String pythonEndpoint;

	@Value("{org.openeo.udf.candela.endpoint}")
	private String candelaEndpoint;

	@Value("{org.openeo.udf.r.endpoint}")
	private String REndpoint;

	private JSONObject processGraphJSON = new JSONObject();
	private JSONObject processGraphAfterUDF = null;

	public JobScheduler() {
		log.debug("Job Scheduler has been initialized successfully!");
	}

	public EngineTypes getProcessingEngine(Job job) {
		return job.getEngine();	
	}
	
	@Override
	public void jobQueued(JobEvent jobEvent) {
		Job job = null;
		try {
			job = jobDAO.findOne(jobEvent.getJobId());
			if (job == null) {
				log.error("A job with the specified identifier is not available.");
			}
			log.debug("The following job was retrieved: \n" + job.toString());

			processGraphJSON = (JSONObject) job.getProcess().getProcessGraph();
			JSONArray nodesSortedArray = getProcessesNodesSequence();
			
			JSONArray processesSequence = new JSONArray();

			for (int i = 0; i < nodesSortedArray.length(); i++) {
				processesSequence
						.put(processGraphJSON.getJSONObject(nodesSortedArray.getString(i)).getString("process_id"));
			}

			if (processesSequence.toString().contains("run_udf")) {
				log.info("Found process_graph containing udf");
				String udfNodeKey = getUDFNode();
				int udfNodeIndex = 0;

				for (int j = 0; j < nodesSortedArray.length(); j++) {
					if (nodesSortedArray.getString(j).equals(udfNodeKey)) {
						udfNodeIndex = j;
					}
				}

				JSONObject udfNode = processGraphJSON.getJSONObject(nodesSortedArray.getString(udfNodeIndex));

				String runtime = udfNode.getJSONObject("arguments").getString("runtime");
				String version = "";
				try {
					version = udfNode.getJSONObject("arguments").getString("version");
				} catch (JSONException e) {
					log.warn(
							"no version for udf specified. Will fall back on default version of specified environment.");
				}
				String udfCode = udfNode.getJSONObject("arguments").getString("udf");
				log.debug("runtime: " + runtime);
				log.debug("udf: " + udfCode);

				String udfCubeCoverageID = "udfCube_"; // Get ID from Rasdaman where UDF generated Cube is stored
				JSONObject loadUDFCube = new JSONObject();
				JSONObject loadUDFCubearguments = new JSONObject();

				loadUDFCubearguments.put("id", udfCubeCoverageID + job.getId().toString().replace('-', '_'));
				udfCubeCoverageID = loadUDFCubearguments.getString("id");
				loadUDFCubearguments.put("spatial_extent", JSONObject.NULL);
				loadUDFCubearguments.put("temporal_extent", JSONObject.NULL);

				loadUDFCube.put("process_id", "load_collection");
				loadUDFCube.put("arguments", loadUDFCubearguments);

				processGraphAfterUDF = new JSONObject();

				processGraphAfterUDF.put(udfNodeKey, loadUDFCube);

				for (int k = udfNodeIndex + 1; k < nodesSortedArray.length(); k++) {
					processGraphAfterUDF.put(nodesSortedArray.getString(k),
							processGraphJSON.getJSONObject(nodesSortedArray.getString(k)));
				}

				JSONObject udfDescriptor = null;

				try {
					// Get code block for UDF
					InputStream codeStream = null;
					if (udfCode.startsWith("http")) {
						String relativeFileLocation = udfCode.substring(udfCode.indexOf("files") + 6);
						String fileToDownloadPath = tmpDir + relativeFileLocation;
						log.debug("Grepping code from udf object from uploaded file resource: " + fileToDownloadPath);
						codeStream = new FileInputStream(fileToDownloadPath);
					} else {
						log.debug("Grepping code from udf object as byte array.");
						codeStream = new ByteArrayInputStream(udfCode.getBytes(StandardCharsets.UTF_8));
					}
					// Get data block for UDF
					WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON, openEOEndpoint, wcpsEndpoint, collectionMap);
					wcpsFactory.setOutputFormat("gml");

					URL url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages"
							+ "&QUERY=" + URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					// Compile code and data into one common JSON object.
					UDFFactory udfFactory = new UDFFactory(runtime, codeStream, conn.getInputStream());
					udfDescriptor = udfFactory.getUdfDescriptor();
					log.trace("UDF JSON: " + udfDescriptor.toString(2));
				} catch (IOException e) {
					log.error("An error occured when streaming in input to UDF: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
					job.setStatus(JobStates.ERROR);
					jobDAO.update(job);
				}

				String service_url = null;
				// Select correct parameters for execution environment of UDF
				if (runtime.toLowerCase().equals("python") && version.toLowerCase().equals("openeo")) {
					runtime = "python";
					service_url = pythonEndpoint;
					log.debug("service URL for UDF processing: " + pythonEndpoint);
				} else if (runtime.toLowerCase().equals("python") && version.toLowerCase().equals("candela")) {
					runtime = "python";
					service_url = candelaEndpoint;
					log.debug("service URL for UDF processing: " + candelaEndpoint);
				} else if (runtime.toLowerCase().equals("r")) {
					runtime = "r";
					//TODO remove hardcoded link
					service_url = "http://10.8.246.140:5555";
					log.debug("service URL for UDF processing: " + REndpoint);
				} else {
					log.error("The requested runtime is not available!");
				}

				// Find correct udf engine endpoint based on selected execution environment
				URL udfServiceEndpoint = null;
				try {
					udfServiceEndpoint = new URL(service_url + "/udf");
				} catch (MalformedURLException e) {
					log.error("An error occured when generating udf service endpoint url: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
					job.setStatus(JobStates.ERROR);
					jobDAO.update(job);
				}
				log.info(udfServiceEndpoint);
				// open http connection to udf execution endpoint
				HttpURLConnection con = null;
				try {
					log.info("Sending UDF to UDF endpoint.");
					con = (HttpURLConnection) udfServiceEndpoint.openConnection();
					con.setRequestMethod("POST");
					con.setRequestProperty("Content-Type", "application/json; utf-8");
					con.setRequestProperty("Accept", "application/json");
					con.setDoOutput(true);
				} catch (IOException e) {
					log.error("An error occured when connecting to udf service endpoint: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
					job.setStatus(JobStates.ERROR);
					jobDAO.update(job);
				}

				String inputHyperCubeDebugPath = tmpDirUDF + "udf_result/input_" + job.getId() + ".json";
				saveHyperCubeToDisk(udfDescriptor, inputHyperCubeDebugPath);

				// stream UDF in form of json hypercube object to udf endpoint via http post
				// method
				try (OutputStream postUDFStream = con.getOutputStream()) {
					byte[] udfBlob = udfDescriptor.toString().getBytes(StandardCharsets.UTF_8);
					// log.debug(new String(udfBlob, StandardCharsets. UTF_8));
					postUDFStream.write(udfBlob, 0, udfBlob.length);
					postUDFStream.close();
					log.info("Posting UDF to UDF Service endpoint.");
				} catch (IOException e) {
					log.error("\"An error occured when posting to udf service endpoint: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
					job.setStatus(JobStates.ERROR);
					jobDAO.update(job);
				}

				// get result from udf endpoint in form of a json hypercube document.
				try (BufferedReader udfResultReader = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String responseLine = null;
					while ((responseLine = udfResultReader.readLine()) != null) {
						response.append(responseLine.trim());
					}
					udfResultReader.close();
					log.info("Received result from UDF endpoint.");
					JSONObject udfResponse = new JSONObject(response.toString());
					JSONArray hyperCubes = udfResponse.getJSONArray("hypercubes");

					JSONObject firstHyperCube = hyperCubes.getJSONObject(0);

					String outputHyperCubeDebugPath = tmpDirUDF + "udf_result/output_" + job.getId() + ".json";
					saveHyperCubeToDisk(firstHyperCube, outputHyperCubeDebugPath);
					// convert hypercube json object into netcdf file and save to tempory disk
					String netCDFPath = tmpDirUDF + "udf_result/" + job.getId() + ".nc";
					new HyperCubeFactory().writeHyperCubeToNetCDFBandAsVariable(firstHyperCube,
							udfResponse.getString("proj"), netCDFPath);
					JSONArray dimensionsArray = firstHyperCube.getJSONArray("dimensions");
					Iterator iterator = dimensionsArray.iterator();
					Boolean containsMultiBands = false;

					// Re-import result from UDF in rasdaman using wcst_import tool
					try {
						ProcessBuilder importProcessBuilder = new ProcessBuilder();
						importProcessBuilder.command("bash", "-c", udfimport + " " + netCDFPath);
						log.debug(netCDFPath);
						Process importProcess = importProcessBuilder.start();
						StringBuilder importProcessLogger = new StringBuilder();

						BufferedReader importProcessLogReader = new BufferedReader(
								new InputStreamReader(importProcess.getInputStream()));

						String outLine;
						while ((outLine = importProcessLogReader.readLine()) != null) {
							importProcessLogger.append(outLine + "\n");
						}

						BufferedReader importProcessLogErrorReader = new BufferedReader(
								new InputStreamReader(importProcess.getErrorStream()));

						String line;
						while ((line = importProcessLogErrorReader.readLine()) != null) {
							importProcessLogger.append(line + "\n");
						}

						int exitValue = importProcess.waitFor();
						if (exitValue == 0) {
							log.info("Import to rasdaman succeeded!");
							log.debug(importProcessLogger.toString());
						} else {
							log.error("Import to rasdaman failed!");
							log.error(importProcessLogger.toString());
						}
						importProcessLogReader.close();
						importProcessLogErrorReader.close();
					} catch (IOException e) {
						log.error("\"An io error occured when launching import to cube: " + e.getMessage());
						StringBuilder builder = new StringBuilder();
						for (StackTraceElement element : e.getStackTrace()) {
							builder.append(element.toString() + "\n");
						}
						log.error(builder.toString());
						job.setStatus(JobStates.ERROR);
						jobDAO.update(job);
					} catch (InterruptedException e) {
						log.error("\"An error occured when launching import to cube: " + e.getMessage());
						StringBuilder builder = new StringBuilder();
						for (StackTraceElement element : e.getStackTrace()) {
							builder.append(element.toString() + "\n");
						}
						log.error(builder.toString());
						job.setStatus(JobStates.ERROR);
						jobDAO.update(job);
					}
					// continue processing of process_graph after the UDF
					log.debug(processGraphAfterUDF);
					WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphAfterUDF, openEOEndpoint,
							wcpsEndpoint, collectionMap);
					URL urlUDF = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages"
							+ "&QUERY=" + URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
					executeWCPS(urlUDF, job, wcpsFactory);
					deleteUDFCube(job.getId());

				} catch (UnsupportedEncodingException e) {
					log.error("An error occured when encoding response of udf service endpoint " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
					job.setStatus(JobStates.ERROR);
					jobDAO.update(job);
				} catch (IOException e) {
					log.error("An error occured during execution of UDF: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
					logErrorStream(con.getErrorStream());
					job.setStatus(JobStates.ERROR);
					jobDAO.update(job);
				}
			}

			else if(job.getEngine()==EngineTypes.WCPS){
				WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON, openEOEndpoint, wcpsEndpoint, collectionMap);
				URL url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages"
						+ "&QUERY=" + URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
				executeWCPS(url, job, wcpsFactory);
			}
			else if(job.getEngine()==EngineTypes.ODC_DASK) {
				executeODC(job);
			}
		} catch (MalformedURLException e) {
			log.error("An error occured when running job with udf: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			job.setStatus(JobStates.ERROR);
			jobDAO.update(job);
		} catch (UnsupportedEncodingException e) {
			log.error("An error occured when running job with udf: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			job.setStatus(JobStates.ERROR);
			jobDAO.update(job);
		} catch (IOException e) {
			log.error("An error occured when running job with udf: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			job.setStatus(JobStates.ERROR);
			jobDAO.update(job);
		}
	}

	@Override
	public void jobExecuted(JobEvent jobEvent) {
		// TODO check if this is still needed. Currently this event chain is unused...
	}
	
	private void deleteUDFCube(UUID jobId) {
		Job job = jobDAO.findOne(jobId);
		if(job != null && job.getStatus() == JobStates.FINISHED) {
			try {
				URL url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages"
						+ "&COVERAGEID=udfCube_" + job.getId());
				URLConnection con =  url.openConnection();
				con.getContent();
				File f = new File(tmpDirUDF + "udf_result");			
				File[] listFiles = f.listFiles();
				
				for (int i=0; i<listFiles.length; i++) {
					File currentFile = listFiles[i];
					if(currentFile.isFile() && !Files.isSymbolicLink(currentFile.toPath())) {
						if(currentFile.getName().contains(job.getId().toString())) {
							log.debug("The following file will be deleted: " + currentFile.getName());
							currentFile.delete();
						}
					}
				}
			} catch (MalformedURLException e) {
				log.error("An error occured when defining URL to delete UDF data cube from WCS endpoint: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			log.error("UDF could not be deleted since job was not finished correctly.");
		}
	}

	private void executeWCPS(URL url, Job job, WCPSQueryFactory wcpsQuery) {
		
		BatchJobResult batchJobResult = resultDAO.findOne(job.getId());
		
		//Skip computing if result is already available.
		if(batchJobResult != null) {
			return;
		}else {
			batchJobResult = new BatchJobResult();
		}

		job.setUpdated(OffsetDateTime.now());
		job.setStatus(JobStates.RUNNING);
		jobDAO.update(job);

		JSONObject linkProcessGraph = new JSONObject();
		linkProcessGraph.put("job_id", job.getId());
		linkProcessGraph.put("updated", job.getUpdated());
		String jobResultPath = tmpDir + job.getId() + "/";
		File jobResultDirectory = new File(jobResultPath);
		if(!jobResultDirectory.exists()) {
			jobResultDirectory.mkdir();
		}
		String dataFileName = "data." + wcpsQuery.getOutputFormat();
		log.debug("The output file will be saved here: \n" + (tmpDir + dataFileName).toString());

		try (BufferedInputStream in = new BufferedInputStream(url.openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(jobResultPath + dataFileName)) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
			log.debug("File saved correctly");
			in.close();
			fileOutputStream.close();
		} catch (IOException e) {
			log.error("An error occured when downloading the file of the current job: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			//TODO add external error stream
			//logErrorStream(e);
			log.error(builder.toString());
		}
		
		batchJobResult.setId(job.getId());
		batchJobResult.bbox(null);
		batchJobResult.setStacVersion("1.0.0");
		batchJobResult.setGeometry(null);
		LinkedHashMap<String, Asset> assetMap = new LinkedHashMap<String, Asset>();

		// Data Asset
		Asset dataAsset = new Asset();
		dataAsset.setHref(openEOPublicEndpoint + "/download/" + job.getId() + "/" + dataFileName);
		String dataMimeType = "";
		try {
			dataMimeType = ConvenienceHelper.getMimeTypeFromRasName(wcpsQuery.getOutputFormat());
		} catch (JSONException e) {
			log.error("An error occured when getting mime type in json: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (IOException e) {
			log.error("An error occured when reading output formats file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		log.debug("Mime type is: " + dataMimeType);
		dataAsset.setType(dataMimeType);
		dataAsset.setTitle(dataFileName);
		List<String> dataAssetRoles = new ArrayList<String>();
		dataAssetRoles.add("data");
		dataAsset.setRoles(dataAssetRoles);
		assetMap.put(dataFileName, dataAsset);

		// Process Asset
		Asset processAsset = new Asset();
		String processFileName = "process.json";
		String processFilePath = tmpDir + job.getId() + "/" + processFileName;
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File(processFilePath), job.getProcess());
		} catch (JsonGenerationException e) {
			log.error("An error occured when generating json of process: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (JsonMappingException e) {
			log.error("An error occured when mapping process.class to json: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (IOException e) {
			log.error("An error occured when writing process to file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		processAsset.setHref(openEOPublicEndpoint + "/download/" + job.getId() + "/" + processFileName);
		String processMimeType = "application/json";
		log.debug("Mime type is: " + processMimeType);
		processAsset.setType(processMimeType);
		processAsset.setTitle(processFileName);
		List<String> processAssetRoles = new ArrayList<String>();
		processAssetRoles.add("process");
		processAsset.setRoles(processAssetRoles);
		assetMap.put(processFileName, processAsset);

		batchJobResult.setAssets(assetMap);
		log.debug(batchJobResult.toString());
		resultDAO.save(batchJobResult);
		log.debug("Result Stored in DB");

		job.setStatus(JobStates.FINISHED);
		job.setUpdated(OffsetDateTime.now());
		job.setProgress(new BigDecimal(100));
		jobDAO.update(job);
		log.debug("The following job was set to status finished: \n" + job.toString());
	}
	private void executeODC(Job job) {
		
		BatchJobResult batchJobResult = resultDAO.findOne(job.getId());
		
		//Skip computing if result is already available.
		if(batchJobResult != null) {
			return;
		}else {
			batchJobResult = new BatchJobResult();
		}

			job.setUpdated(OffsetDateTime.now());
			job.setStatus(JobStates.RUNNING);
			jobDAO.update(job);
			JSONObject processGraphJSON = (JSONObject) job.getProcess().getProcessGraph();

			JSONObject process = new JSONObject();
			process.put("id", "ODC-graph");
			process.put("process_graph", processGraphJSON);
			
			URL url;
			HttpURLConnection conn = null;
			try {
				url = new URL(odcEndpoint);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json; utf-8");
				conn.setDoOutput(true);
				log.debug("+++ Successfully connected to ODC!");
			} catch (Exception e) {
				addStackTraceAndErrorToLog(e);
				Error error = new Error();
				error.setCode("500");
				error.setMessage("Not possible to establish connection with ODC Endpoint!");
				log.error(error.getMessage());
				job.setStatus(JobStates.ERROR);
				jobDAO.update(job);
				return;
			}
			
			String jobResultPath = tmpDir + job.getId() + "/";
			File jobResultDirectory = new File(jobResultPath);
			if(!jobResultDirectory.exists()) {
				jobResultDirectory.mkdir();
			}

			String dataFormat = getSaveNodeFormat();
			String dataFileName = "result";
			if(dataFormat.equalsIgnoreCase("netcdf")) {
				dataFileName = "result.nc";
				}
			else if(dataFormat.equalsIgnoreCase("gtiff") || dataFormat.equalsIgnoreCase("geotiff")) {
				dataFileName = "result.tif";
				}
			else if(dataFormat.equalsIgnoreCase("png")){
				dataFileName = "result.png";
				}
			else if(dataFormat.equalsIgnoreCase("json")){
				dataFileName = "result.json";
				}
			log.debug("The output file will be saved here: \n" + (jobResultPath + dataFileName).toString());
			
			String dataMimeType = "application/octet-stream";
			try {
				try (OutputStream os = conn.getOutputStream()) {
					byte[] requestBody = process.toString().getBytes("utf-8");
					os.write(requestBody, 0, requestBody.length);
				}
				InputStream is = conn.getInputStream();
				dataMimeType = conn.getContentType();
				byte[] response = IOUtils.toByteArray(is);
				FileOutputStream fileOutputStream = new FileOutputStream(jobResultPath + dataFileName);
				fileOutputStream.write(response, 0, response.length); 
				log.debug("File saved correctly");
				fileOutputStream.close();
			} catch (IOException e) {
				log.error("An error occured when downloading the file of the current job: " + e.getMessage());
				job.setStatus(JobStates.ERROR);
				jobDAO.update(job);
				return;
			}
					
			batchJobResult.setId(job.getId());
			batchJobResult.bbox(null);
			batchJobResult.setStacVersion("1.0.0");
			batchJobResult.setGeometry(null);
			LinkedHashMap<String, Asset> assetMap = new LinkedHashMap<String, Asset>();

			// Data Asset
			Asset dataAsset = new Asset();
			dataAsset.setHref(openEOPublicEndpoint + "/download/" + job.getId() + "/" + dataFileName);
			log.debug("Mime type is: " + dataMimeType);
			dataAsset.setType(dataMimeType);
			dataAsset.setTitle(dataFileName);
			List<String> dataAssetRoles = new ArrayList<String>();
			dataAssetRoles.add("data");
			dataAsset.setRoles(dataAssetRoles);
			assetMap.put(dataFileName, dataAsset);

			// Process Asset
			Asset processAsset = new Asset();
			String processFileName = "process.json";
			String processFilePath = tmpDir + job.getId() + "/" + processFileName;
			ObjectMapper mapper = new ObjectMapper();
			try {
				mapper.writeValue(new File(processFilePath), job.getProcess());
			} catch (JsonGenerationException e) {
				log.error("An error occured when generating json of process: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			} catch (JsonMappingException e) {
				log.error("An error occured when mapping process.class to json: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			} catch (IOException e) {
				log.error("An error occured when writing process to file: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
			}
			processAsset.setHref(openEOPublicEndpoint + "/download/" + job.getId() + "/" + processFileName);
			String processMimeType = "application/json";
			log.debug("Mime type is: " + processMimeType);
			processAsset.setType(processMimeType);
			processAsset.setTitle(processFileName);
			List<String> processAssetRoles = new ArrayList<String>();
			processAssetRoles.add("process");
			processAsset.setRoles(processAssetRoles);
			assetMap.put(processFileName, processAsset);

			batchJobResult.setAssets(assetMap);
			log.debug(batchJobResult.toString());
			resultDAO.save(batchJobResult);
			log.debug("Result Stored in DB");

			job.setStatus(JobStates.FINISHED);
			job.setUpdated(OffsetDateTime.now());
			job.setProgress(new BigDecimal(100));
			jobDAO.update(job);
			log.debug("The following job was set to status finished: \n" + job.toString());

	}

	public JSONArray getProcessesNodesSequence() {
		JSONArray nodesArray = new JSONArray();
		JSONArray nodesSortedArray = new JSONArray();

		String saveNode = getSaveNode();
		JSONArray saveNodeAsArray = new JSONArray();
		saveNodeAsArray.put(saveNode);
		nodesArray.put(saveNodeAsArray);

		for (int n = 0; n < nodesArray.length(); n++) {
			for (int a = 0; a < nodesArray.getJSONArray(n).length(); a++) {
				JSONArray fromNodeOfProcess = getFromNodeOfCurrentKey(nodesArray.getJSONArray(n).getString(a));
				if (fromNodeOfProcess.length() > 0) {
					nodesArray.put(fromNodeOfProcess);
				} else if (fromNodeOfProcess.length() == 0) {
					nodesSortedArray.put(nodesArray.getJSONArray(n).getString(a));
				}
			}
		}

		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}

		nodesArray.remove(nodesArray.length() - 1);
		for (int i = nodesArray.length() - 1; i > 0; i--) {
			if (nodesArray.getJSONArray(i).length() > 0) {
				for (int a = 0; a < nodesArray.getJSONArray(i).length(); a++) {
					nodesSortedArray.put(nodesArray.getJSONArray(i).getString(a));
				}
			}
		}

		nodesSortedArray.put(saveNode);
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}

		return nodesSortedArray;
	}

	private String getUDFNode() {
		for (String processNodeKey : processGraphJSON.keySet()) {
			JSONObject processNode = processGraphJSON.getJSONObject(processNodeKey);
			String processID = processNode.getString("process_id");
			if (processID.equals("run_udf")) {
				log.debug("UDF Process Node key found is: " + processNodeKey);
				return processNodeKey;
			}
		}
		return null;
	}
	
	public List<JSONObject> getProcessNode(String process_id, JSONObject inputProcessGraphJSON) {
		ArrayList<JSONObject> nodesList = new ArrayList<JSONObject>();
		for (String processNodeKey : inputProcessGraphJSON.keySet()) {
			JSONObject processNode = inputProcessGraphJSON.getJSONObject(processNodeKey);
			String processID = processNode.getString("process_id");
			if (processID.equals(process_id)) {
				log.debug(process_id + " Process Node key found is: " + processNodeKey);
				nodesList.add(processNode);
			}
		}
		return nodesList;
	}

	private String getSaveNode() {
		for (String processNodeKey : processGraphJSON.keySet()) {
			JSONObject processNode = processGraphJSON.getJSONObject(processNodeKey);
			String processID = null;
			try {
				 processID = processNode.getString("process_id");
			}catch(JSONException e) {
				log.error("process_id not found!");
			}
			if (processID.equals("save_result")) {
				log.debug("Save Process Node key found is: " + processNodeKey);
				return processNodeKey;
			}
		}
		return null;
	}
	
	private String getSaveNodeFormat() {
		for (String processNodeKey : processGraphJSON.keySet()) {
			JSONObject processNode = processGraphJSON.getJSONObject(processNodeKey);
			String processID = null;
			try {
				 processID = processNode.getString("process_id");
			}catch(JSONException e) {
				log.error("process_id not found!");
			}
			if (processID.equals("save_result")) {
				String outputFormat = processNode.getJSONObject("arguments").getString("format");
				return outputFormat;
			}
		}
		return null;
	}

	private JSONArray getFromNodeOfCurrentKey(String currentNode) {
		JSONObject nextNodeName = new JSONObject();
		JSONArray fromNodes = new JSONArray();
		String nextFromNode = null;
		JSONObject currentNodeProcessArguments = processGraphJSON.getJSONObject(currentNode).getJSONObject("arguments");
		for (String argumentsKey : currentNodeProcessArguments.keySet()) {
			if (argumentsKey.contentEquals("data")) {
				if (currentNodeProcessArguments.get("data") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("data").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				} else if (currentNodeProcessArguments.get("data") instanceof JSONArray) {
					JSONArray reduceData = currentNodeProcessArguments.getJSONArray("data");
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
			} else if (argumentsKey.contentEquals("band1")) {
				if (currentNodeProcessArguments.get("band1") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("band1").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("band1").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);
			} else if (argumentsKey.contentEquals("band2")) {
				if (currentNodeProcessArguments.get("band2") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("band2").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("band2").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);
			}
		}
		return fromNodes;
	}

	@Override
	public void udfExecuted(UDFEvent jobEvent) {
		Job job = null;
		try {
			job = jobDAO.findOne(jobEvent.getJobId());
			if (job == null) {
				log.error("A job with the specified identifier is not available.");
			}
			log.debug("The following job was retrieved: \n" + job.toString());
			// TODO receive resulting json object from UDF container
			// TODO import resulting UDF object into rasdaman

			WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphAfterUDF, openEOEndpoint, wcpsEndpoint, collectionMap);
			URL urlUDF = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages"
					+ "&QUERY=" + URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
			executeWCPS(urlUDF, job, wcpsFactory);
		} catch (MalformedURLException e) {
			log.error("An error occured: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (UnsupportedEncodingException e) {
			log.error("An error occured: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}

	private void saveHyperCubeToDisk(JSONObject hyperCubeJSON, String filePath) {
		log.debug("Saving hypercube to disk: " + filePath);
		byte[] firstHyperCubeBlob = hyperCubeJSON.toString(2).getBytes(StandardCharsets.UTF_8);
		File firstHyperCubeFile = new File(filePath);
		FileOutputStream firstHyperCubeStream;
		// store json hypercube as file on disk
		try {
			firstHyperCubeStream = new FileOutputStream(firstHyperCubeFile);
			firstHyperCubeStream.write(firstHyperCubeBlob, 0, firstHyperCubeBlob.length);
			firstHyperCubeStream.flush();
			firstHyperCubeStream.close();
			// TODO fire udf finished event here!
		} catch (FileNotFoundException e1) {
			log.error("File not found: " + e1.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e1.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (IOException e) {
			log.error("An error occured during writing of file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}

	private void logErrorStream(InputStream errorStream) {
		try {
			log.debug("Error stream content below this line:");
			BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, "utf-8"));
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			br.close();
			JSONObject responseJSON = new JSONObject(response.toString());
			log.debug(responseJSON.toString(4));
		} catch (UnsupportedEncodingException e) {
			log.error("An error occured when encoding response of udf service endpoint " + e.getMessage());
			StringBuilder builderNested = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builderNested.append(element.toString() + "\n");
			}
			log.error(builderNested.toString());
		} catch (IOException e) {
			log.error("An error occured when receiving error stream from udf service endpoint " + e.getMessage());
			StringBuilder builderNested = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builderNested.append(element.toString() + "\n");
			}
			log.error(builderNested.toString());
		}
	}
	private void addStackTraceAndErrorToLog(Exception e) {
		log.error(e.getMessage());
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			builder.append(element.toString() + "\n");
		}
		log.error(builder.toString());
	}
}
