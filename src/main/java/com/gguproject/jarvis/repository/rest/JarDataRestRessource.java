package com.gguproject.jarvis.repository.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gguproject.jarvis.repository.service.JarContext;
import com.gguproject.jarvis.repository.service.JarDataContext;
import com.gguproject.jarvis.repository.service.exception.JarException;

import net.codestory.http.Context;
import net.codestory.http.Request;
import net.codestory.http.Response;
import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Post;
import net.codestory.http.annotations.Prefix;
import net.codestory.http.constants.Headers;
import net.codestory.http.errors.HttpException;
import net.codestory.http.payload.Payload;

@Prefix("/jar/:jarName/version")
public class JarDataRestRessource {
	private static Logger LOGGER = LoggerFactory.getLogger(JarDataRestRessource.class);
	
	@Get("/latest/data/download")
	public Payload download(String jarName) {
		try {
			JarDataContext dataContext = JarContext.getLatestVersion(jarName).getDataContext();
			
			if(dataContext.getDataFile() == null) {
				return Payload.notFound();
			}
			
			return new Payload("application/octet-stream", new FileInputStream(dataContext.getDataFile()))
					.withHeader(Headers.CONTENT_DISPOSITION, "attachment; filename="+ dataContext.getDataFileName());
		} catch (JarException | FileNotFoundException e) {
			return Payload.notFound();
		}
	}
	
	@Get("/:version/data/download")
	public Payload download(String jarName, String version) {
		try {
			JarDataContext dataContext = JarContext.getVersion(jarName, version).getDataContext();
			
			if(dataContext.getDataFile() == null) {
				return Payload.notFound();
			}
			
			return new Payload("application/octet-stream", new FileInputStream(dataContext.getDataFile()))
					.withHeader(Headers.CONTENT_DISPOSITION, "attachment; filename="+ dataContext.getDataFileName());
		} catch (JarException | FileNotFoundException e) {
			return Payload.notFound();
		}
	}
	
	@Post("/:version/data")
	public Payload post(String jarName, String version, InputStream stream, Context context, Request request, Response response) {
		String fileName = request.header("Content-Disposition").replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
		try {
			JarDataContext dataContext = JarContext.getVersion(jarName, version).getDataContext();
			
			if(!dataContext.getDataFileName().equals(fileName)) {
				LOGGER.error("File name: {} does not match path param: {}", fileName, dataContext.getDataFileName());
				return Payload.badRequest();
			}
			
			dataContext.upload(stream);
			return Payload.ok();
		} catch (JarException e) {
			LOGGER.error("Not able to upload file: {}", fileName, e);
			throw new HttpException(500);
		}
	}
}
