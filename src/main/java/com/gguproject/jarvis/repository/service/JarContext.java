package com.gguproject.jarvis.repository.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gguproject.jarvis.repository.service.ConfigurationService.PropertyKey;
import com.gguproject.jarvis.repository.service.exception.JarException;

public class JarContext {
	private static Logger LOGGER = LoggerFactory.getLogger(JarContext.class);
	
	private ConfigurationService configurationService = ConfigurationService.get();
	
	private String name;
	
	private String version;
	
	private File jarFile;
	
	private File repositoryDirectory;
	
	public static JarContext getLatestVersion(String name) throws JarException {
		JarContext context = new JarContext(name);
		context.version = context.findLatestVersion();
		context.jarFile = context.findVersionJarFile(context.version);
		
		return context;
	}
	
	public static JarContext getVersion(String name, String version) throws JarException {
		JarContext context = new JarContext(name);
		context.version = version;
		context.jarFile = context.findVersionJarFile(context.version);
		
		return context;
	}
	
	private JarContext(String name) throws JarException {
		this.name = name;
		this.repositoryDirectory = new File(new StringBuilder(this.configurationService.getProperty(PropertyKey.repository)).append(this.name).toString());

		// create it if necessary
		this.repositoryDirectory.mkdir();
	}
	
	/**
	 * Find the lastest jar version
	 * @throws JarException 
	 */
	private String findLatestVersion() throws JarException {
		LOGGER.debug("Find latest repository file for: {}", this.name);
		
		// get the list of folders -> list of available versions
		List<File> versions = Arrays.asList(this.repositoryDirectory.listFiles((f) -> f.isDirectory()));
		
		if(versions.isEmpty()) {
			throw new JarException(String.format("Can't find any versions in the repository: %s", this.name));
		}
		
		// sort them to have the most recent one first
		versions.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
		
		LOGGER.debug("Found latest version: {}", versions.get(0));
		
		return versions.get(0).getName();
	}
	/**
	 * 
	 * @param version
	 * @return
	 * @throws JarException
	 */
	private File findVersionJarFile(String version) throws JarException {
		File[] versionDirs = this.repositoryDirectory.listFiles(f -> f.getName().equals(version));
		
		if(versionDirs.length == 0) {
			LOGGER.debug("No version directory found {} {}", this.getName(), this.getVersion());
			return null;
		}
		
		String versionnedFileName = String.format("%s-%s.jar", this.name, version);
		
		List<File> jarFiles = Arrays.asList(versionDirs[0].listFiles(f -> f.isFile() && f.getName().equals(versionnedFileName)));
		
		if(jarFiles.isEmpty()) {
			LOGGER.debug("No jar files found {} {}", this.getName(), this.getVersion());
			return null;
		} else if (jarFiles.size() > 1) {
			throw new JarException(String.format("Found more than 1 file: %s", versionnedFileName));
		}
		
		return jarFiles.get(0);
	}
	
	/**
	 * Upload a jar file 
	 * @param version
	 * @param stream
	 * @throws JarException
	 */
	public void upload(InputStream stream) throws JarException {
		ReadableByteChannel readableByteChannel = Channels.newChannel(stream);
		
		String uploadFilePath = new StringBuilder(this.configurationService.getProperty(PropertyKey.repository))
			.append(this.name)
			.append(File.separator)
			.append(this.version)
			.append(File.separator)
			.append(this.getJarName())
			.toString();
		
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
	
	/**
	 *  Return the data context of this jar
	 * @return Data context
	 * @throws com.gguproject.jarvis.repository.service.exception.JarException 
	 */
	public JarDataContext getDataContext() throws JarException {
		return new JarDataContext(this);
	}
	
	public File getJarFile() {
		if(this.jarFile != null && this.jarFile.exists()) {
			return this.jarFile;
		}
		return null;
	}
	
	public File getJarRepository() {
		return this.repositoryDirectory;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public String getJarName() {
		return String.format("%s-%s.jar", this.name, this.version);
	}
}
