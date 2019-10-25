package com.gguproject.jarvis.repository.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gguproject.jarvis.repository.rest.dto.JarDto;
import com.gguproject.jarvis.repository.service.ConfigurationService;
import com.gguproject.jarvis.repository.service.ConfigurationService.PropertyKey;
import com.gguproject.jarvis.repository.service.JarContext;
import com.gguproject.jarvis.repository.service.exception.JarException;

import net.codestory.http.Request;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Post;
import net.codestory.http.annotations.Prefix;
import net.codestory.http.constants.Headers;
import net.codestory.http.errors.HttpException;
import net.codestory.http.payload.Payload;

@Prefix("/jar/:jarName/version")
public class JarVersionRestRessource {
	private static Logger LOGGER = LoggerFactory.getLogger(JarVersionRestRessource.class);
	
	private ConfigurationService configurationService = ConfigurationService.get();
	
	/**
	 * List all versions of the jar
	 * @param jarName Jar name
	 * @return
	 */
	@Get("/list")
	public List<String> listAllVersions(String jarName) {
		LOGGER.debug("jar version list: {}", jarName);
		
		List<String> versions = new ArrayList<>();
		File jarRepositoryDirectory = new File(String.format("%s/%s", this.configurationService.getProperty(PropertyKey.repository), jarName));
		
		Arrays.asList(jarRepositoryDirectory.listFiles(f -> f.isDirectory())).forEach(versionFile -> {
			versions.add(versionFile.getName());
		});
		
		return versions;
	}
	
	@Get("/latest")
	public Payload getLastVersion(String jarName) {
		try {
			JarContext jarContext = JarContext.getLatestVersion(jarName);
			return new Payload(new JarDto(jarContext.getName(), jarContext.getVersion(), jarContext.getJarName()));
		} catch (JarException e) {
			LOGGER.error("Not able to download file: {}", e);
			return Payload.notFound();
		}
	}
	
	/**
	 * Download the latest jar version
	 * @param jarName Jar name
	 * @return
	 */
	@Get("/latest/download")
	public Payload download(String jarName) {
		try {
			JarContext jarContext = JarContext.getLatestVersion(jarName);
			
			if(jarContext.getJarFile() == null) {
				return Payload.notFound();
			}
			
			return new Payload("application/octet-stream", new FileInputStream(jarContext.getJarFile()))
					.withHeader(Headers.CONTENT_DISPOSITION, "attachment; filename="+ jarContext.getJarName());
		} catch (FileNotFoundException | JarException e) {
			LOGGER.error("Not able to download file: {}", e);
			return Payload.notFound();
		}
	}
	
	/**
	 * Download a given jar version
	 * @param jarName Jar name
	 * @param version version
	 * @return
	 */
	@Get("/:version/download")
	public Payload downloadVersion(String jarName, String version) {
		try {
			JarContext jarContext = JarContext.getVersion(jarName, version);
			
			if(jarContext.getJarFile() == null) {
				return Payload.notFound();
			}
			
			return new Payload("application/octet-stream", new FileInputStream(jarContext.getJarFile()))
					.withHeader(Headers.CONTENT_DISPOSITION, "attachment; filename="+ jarContext.getJarName());
		} catch (FileNotFoundException | JarException e) {
			LOGGER.error("Not able to download file: {}", e);
			return Payload.notFound();
		}
	}
	
	/**
	 * Upload a jar file for a given jar & version
	 * @param jarName Jar file name
	 * @param version jar version
	 * @param stream Body stream 
	 * @param request Incoming request
	 * @return
	 */
	@Post("/:version")
	public Payload post(String jarName, String version, InputStream stream, Request request) {
		String fileName = request.header("Content-Disposition").replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");

		// check that the given name & version match the file name
		String pathJarName = String.format("%s-%s.jar", jarName, version);
		
		if(!pathJarName.equals(fileName)) {
			LOGGER.error("File name: {} does not match path param: {} {}", fileName, jarName, version);
			return Payload.badRequest();
		}

		try {
			JarContext.getVersion(jarName, version).upload(stream);
			return Payload.ok();
		} catch (JarException e) {
			LOGGER.error("Not able to upload file: {} {}", jarName, version, e);
			throw new HttpException(500);
		}
	}
}
