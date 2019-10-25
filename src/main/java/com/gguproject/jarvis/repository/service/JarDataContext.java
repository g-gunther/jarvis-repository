package com.gguproject.jarvis.repository.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gguproject.jarvis.repository.service.ConfigurationService.PropertyKey;
import com.gguproject.jarvis.repository.service.exception.JarException;

/**
 * Context of data of a jar
 */
public class JarDataContext {
	private static Logger LOGGER = LoggerFactory.getLogger(JarDataContext.class);
	
	private ConfigurationService configurationService = ConfigurationService.get();
	
	/**
	 * Name of the data folders
	 */
	public static final String dataFileName = "data.zip";
	
	/**
	 * Data file
	 */
	private File dataFile;
	
	/**
	 * Parent jar context
	 */
	private JarContext jarContext;
	
	/**
	 * Constructor
	 * @param jarContext jar context
	 * @throws JarException 
	 */
	public JarDataContext(JarContext jarContext) throws JarException {
		this.jarContext = jarContext;
		
		File[] versionDirs = jarContext.getJarRepository().listFiles(f -> f.getName().equals(this.jarContext.getVersion()));
		if(versionDirs.length == 0) {
			LOGGER.debug("No version directory found {} {}", this.jarContext.getName(), this.jarContext.getVersion());
			return;
		}
		
		File[] dataFiles = versionDirs[0].listFiles(f -> f.getName().equals(this.getDataFileName()) && f.isFile());
		if(dataFiles.length == 0) {
			LOGGER.debug("No data files for {}", this.jarContext.getName());
		} else {
			this.dataFile = dataFiles[0];
		}
	}

	/**
	 * Indicates if the data directory exists or not
	 * @return true if exists, false else
	 */
	public boolean exists() {
		return this.dataFile != null && this.dataFile.exists();
	}
	
	/**
	 * Get the data file
	 * @throws JarException 
	 */
	public File getDataFile() throws JarException {
		if(!this.exists()) {
			LOGGER.info("The data file does not exists: {} {}", this.jarContext.getName(), this.jarContext.getVersion());
			return null;
		}
		
		return this.dataFile;
	}
	
	/**
	 * Upload a data zip file 
	 * @param version
	 * @param stream
	 * @throws JarException
	 */
	public void upload(InputStream stream) throws JarException {
		ReadableByteChannel readableByteChannel = Channels.newChannel(stream);
		
		String uploadFilePath = new StringBuilder(this.configurationService.getProperty(PropertyKey.repository))
				.append(this.jarContext.getName())
				.append(File.separator)
				.append(this.jarContext.getVersion())
				.append(File.separator)
				.append(this.getDataFileName())
				.toString();
		
		LOGGER.debug("Upload data file in directory: {}", uploadFilePath);
		
		File targetFile = new File(uploadFilePath);
		try {
			targetFile.getParentFile().mkdirs();
			targetFile.createNewFile();
		} catch (IOException e) {
			throw new JarException("Not able to create new file", e);
		}
		
		try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)){
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			throw new JarException("Not able to upload file", e);
		}
	}
	
	public String getDataFileName() {
		return String.format("data-%s-%s.zip", this.jarContext.getName(), this.jarContext.getVersion());
	}
}
