package org.openeo.wcps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openeo.spring.dao.JobDAO;
import org.openeo.spring.model.Job;
import org.openeo.spring.model.Job.JobStates;
import org.springframework.beans.factory.annotation.Autowired;

public class JobResultDeletion implements Runnable {
	
	Logger log = LogManager.getLogger();
	
	JobDAO jobDAO;
	
	@Autowired
	public void setDao(JobDAO injectedDAO) {
		jobDAO = injectedDAO;
	}

	@Override
	public void run() {
		log.debug("Start thread to call the method deleteFiles()");
		
		try {
			this.checkResultsFilesJobToDelete();
		} catch (Exception e) {
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}		
	};
	
	private void checkResultsFilesJobToDelete() {
		try {
			File f = new File(ConvenienceHelper.readProperties("temp-dir"));			
			File[] listFiles = f.listFiles();
			Job job = null;
			
			for (int i=0; i<listFiles.length; i++) {
				File currentFile = listFiles[i];
				if(currentFile.isFile() && !Files.isSymbolicLink(currentFile.toPath())) {
					if(differenceTimeMinutes(currentFile.lastModified()) > Integer.parseInt(ConvenienceHelper.readProperties("temp-file-expiry"))) {
						currentFile.delete();
						String jobId = currentFile.getName().substring(0, currentFile.getName().indexOf("."));
						log.debug("The current file " + currentFile.getName() + " is expired.\nIt will be deleted and the its job's status will be set to SUBMITTED.");
						job = jobDAO.findOne(jobId);
						if (job == null) {
							log.debug("A job with the specified identifier is not available.");
						} else {
							job.setStatus(JobStates.CANCELED);
							job.setUpdated(OffsetDateTime.now());
							jobDAO.update(job);
						}
					}
				}
			}
		} catch (IOException ioe) {
			log.error("An IO error occured");
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : ioe.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}
		
	private long differenceTimeMinutes(long lastModified) {
		long timestamp = System.currentTimeMillis();
		long difftime = (timestamp - lastModified)/60000;
//		log.debug("DIFF NOW - TIMEFILE " + timestamp + " - " + lastModified +
//				" = " + difftime);
		return difftime;
	}
	
	/**
	//Recursive method to delete all outdated files of al the users
	private void checkFilesToDelete(File currentFolder) {
		File f = new File(currentFolder.getPath());
		
		File[] listFiles = f.listFiles();
//		log.debug("LIST FILES " + String.join(", ", f.list()));
		
		if(listFiles.length > 0) {
			for(int i=0; i<listFiles.length; i++) {
				File currentFile = listFiles[i];
				if(currentFile.isDirectory()) {
					System.out.println(currentFile + " is a directory");
					checkFilesToDelete(currentFile);
				} else {					
					if (differenceTimeMinutes(currentFile.lastModified()) > Integer.parseInt(ConvenienceHelper.readProperties("temp-file-expiry"))) {
						if(!currentFile.isDirectory()) {
							log.debug(currentFile + " is a file out of date");
							currentFile.delete();
						}
					}  else {
						log.debug(currentFile + "is OK");
					}
				}
			}	
		}	
	}**/
}
