package com.gguproject.jarvis.repository.monitor;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gguproject.jarvis.repository.service.DnsRegistryService;

import net.posick.mDNS.Lookup;
import net.posick.mDNS.ServiceInstance;

public class DnsRegistryMonitor extends Thread {
	private static Logger LOGGER = LoggerFactory.getLogger(DnsRegistryMonitor.class);
	
	public void run() {
		LOGGER.info("Start dns registry monitor");
		
		while(true) {
			
			try {
				if(this.check()) {
					LOGGER.debug("DNS service is registered");
				} else {
					LOGGER.debug("DNS service is not registered anymore - register it again");
					DnsRegistryService.get().register();
				}
			} catch (IOException e) {
				LOGGER.error("Error while checking if the service is registered", e);
			}
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				LOGGER.error("Error while sleeping", e);
			}
		}
	}
	
	/**
	 * Check if the service is running
	 * @return
	 * @throws IOException
	 */
	private boolean check() throws IOException {
		try(Lookup lookup = new Lookup("jarvis.com.gguproject.jarvis:repository_v0.0.1._http.local.")){
			ServiceInstance[] services = lookup.lookupServices();
			for (ServiceInstance service : services) {
				if(service.getAddresses().length > 0) {
					return true;
				}
			}
		}
		return false;
	}
}
