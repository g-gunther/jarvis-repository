package com.gguproject.jarvis.repository;

import com.gguproject.jarvis.repository.monitor.DnsRegistryMonitor;
import com.gguproject.jarvis.repository.rest.JarDataRestRessource;
import com.gguproject.jarvis.repository.rest.JarRestRessource;
import com.gguproject.jarvis.repository.rest.JarVersionRestRessource;
import com.gguproject.jarvis.repository.service.ConfigurationService;
import com.gguproject.jarvis.repository.service.ConfigurationService.PropertyKey;
import com.gguproject.jarvis.repository.service.DnsRegistryService;

import net.codestory.http.WebServer;

public class App {
	
	public static void main(final String... args) throws Exception {
		DnsRegistryService.get().register();
		
		new DnsRegistryMonitor().start();
		
		ConfigurationService configurationService = ConfigurationService.get();
		new WebServer().configure(routes -> routes
				.filter(LogRequestFilter.class)
			    .get("/", "Jarvis Repository")
			    .add(JarRestRessource.class)
			    .add(JarVersionRestRessource.class)
			    .add(JarDataRestRessource.class)
			).start(configurationService.getPropertyAsInteger(PropertyKey.serverPort));
	}
}
