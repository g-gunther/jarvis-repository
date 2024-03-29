package com.gguproject.jarvis.repository;

import java.io.IOException;
import java.net.InetAddress;

import net.posick.mDNS.Lookup;
import net.posick.mDNS.ServiceInstance;

public class TestLookupService {
/*
 * Services Successfully Registered: 
	Service ("TestHost._mdc._tcp.local." can be reached at "TestHost.local." [TestHost.local./192.168.1.74] on port 8080
	TXT: textvers="1", rn="urn:smpte:udn:local:id=1234567890ABCDEF", proto="mdcp", path="/Device",                                )
 */
	
	public static void main(String...args) throws IOException {
		Lookup lookup = new Lookup("jarvis.com.gguproject.jarvis:repository_v0.0.1._http.local.");
		ServiceInstance[] services = lookup.lookupServices();
		System.out.println(services.length);
		for (ServiceInstance service : services)
		{
			System.out.println(service.getPort());
		    for(InetAddress addr : service.getAddresses()) {
		    	System.out.println(addr.getHostAddress());
		    }
		}
	}
}
