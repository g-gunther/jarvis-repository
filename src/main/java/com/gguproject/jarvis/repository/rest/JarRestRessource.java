package com.gguproject.jarvis.repository.rest;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gguproject.jarvis.repository.service.ConfigurationService;
import com.gguproject.jarvis.repository.service.ConfigurationService.PropertyKey;

import net.codestory.http.annotations.Get;
import net.codestory.http.annotations.Prefix;

@Prefix("/jar")
public class JarRestRessource {
	private ConfigurationService configurationService = ConfigurationService.get();
	
	@Get("/list")
	public Map<String, String> listAllJars() {
		Map<String, String> jars = new HashMap<>();
		File repositoryDirectory = new File(this.configurationService.getProperty(PropertyKey.repository));
		
		Arrays.asList(repositoryDirectory.listFiles(f -> f.isDirectory())).forEach(jarFile -> {
			List<File> versions = Arrays.asList(jarFile.listFiles((f) -> f.isDirectory()));
			if(versions.isEmpty()) {
				jars.put(jarFile.getName(), "");
			} else {
				versions.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
				jars.put(jarFile.getName(), versions.get(0).getName());
			}
		});
		
		return jars;
	}
}
