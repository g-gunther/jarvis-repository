package com.gguproject.jarvis.repository.service;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Name;

import com.gguproject.jarvis.repository.service.ConfigurationService.PropertyKey;

import net.posick.mDNS.MulticastDNSService;
import net.posick.mDNS.ServiceInstance;
import net.posick.mDNS.ServiceName;

public class DnsRegistryService {
	private static Logger LOGGER = LoggerFactory.getLogger(DnsRegistryService.class);
	
	private static final DnsRegistryService instance = new DnsRegistryService();
	
	public static DnsRegistryService get() {
		return instance;
	}
	
	private ConfigurationService configurationService = ConfigurationService.get();
	
	/*
	 * No idea what is it for but it works...
	 */
	private static final int priority = 10;
	private static final int weight = 10;
	private static final String ucnPTRName = "com.gguproject.jarvis:repository_v0.0.1._http";
	private static final String domain = "local.";
	private static final String hostname = "jarvis";
	
	
	private DnsRegistryService() {
	}
	
	public void register() throws IOException {
        MulticastDNSService service = new MulticastDNSService();
        
        Name host = new Name(hostname + "." + (domain.endsWith(".") ? domain : domain + "."));
        ServiceName serviceName = new ServiceName(hostname + "." + ucnPTRName + "." + (domain.endsWith(".") ? domain : domain + "."));
        
        ServiceInstance serviceInstance = new ServiceInstance(
        		serviceName, 
        		priority, 
        		weight, 
        		configurationService.getPropertyAsInteger(PropertyKey.serverPort), 
        		host, 
        		new InetAddress[] {InetAddress.getLocalHost()},
        		// no idea what those properties are for...
        		"textvers=1", 
        		"rn=urn:smpte:udn:local:id=1234567890ABCDEF", 
        		"proto=mdcp", 
        		"path=/Device"
    		);
        
        ServiceInstance registeredService = service.register(serviceInstance);
        if (registeredService != null) {
        	LOGGER.info("Services successfully registered: {}", registeredService);
        } else {
        	LOGGER.error("Services registration for UCN in domain {} failed", domain);
        }
	}
}
