package com.gguproject.jarvis.repository;

import java.net.InetAddress;

import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import net.posick.mDNS.Lookup;
import net.posick.mDNS.MulticastDNSService;
import net.posick.mDNS.ServiceInstance;
import net.posick.mDNS.ServiceName;

public class TestRegisterService {

	public static void main(String...args) {
		int priority = 10;
        int weight = 10;
        int port = 8080;
//        String ucn = "urn:smpte:ucn:org.smpte.st2071:device_v1.0";
        String ucnPTRName = "com.gguproject.jarvis:repository_v0.0.1._http";
        //        String ucnPTRName = "_mdc._tcp";
        String domain = "local.";
        String hostname = "jarvis";
        
        try
        {
            MulticastDNSService service = new MulticastDNSService();
            
            Name fqn = new Name(hostname + "." + (domain.endsWith(".") ? domain : domain + "."));
            ServiceName srvName = new ServiceName(hostname + "." + ucnPTRName + "." + (domain.endsWith(".") ? domain : domain + "."));
            
            ServiceInstance serviceInstance = new ServiceInstance(
            		srvName, 
            		priority, 
            		weight, 
            		port, 
            		fqn, 
            		new InetAddress[] {InetAddress.getByName("192.168.1.74")}, 
            		"textvers=1", 
            		"rn=urn:smpte:udn:local:id=1234567890ABCDEF", 
            		"proto=mdcp", 
            		"path=/Device"
        		);
            ServiceInstance registeredService = service.register(serviceInstance);
            if (registeredService != null)
            {
                System.out.println("Services Successfully Registered: \n\t" + registeredService);
            } else
            {
                boolean hostnameResolves = false;
                
                Lookup lookup = new Lookup(fqn);
                Record[] rrs = lookup.lookupRecords();
                if ((rrs != null) && (rrs.length > 0))
                {
                    outer:
                        for (Record rr : rrs)
                        {
                            switch (rr.getType())
                            {
                                case Type.A:
                                case Type.A6:
                                case Type.AAAA:
                                case Type.DNAME:
                                case Type.CNAME:
                                case Type.PTR:
                                    if (rr.getName().equals(fqn))
                                    {
                                        hostnameResolves = true;
                                        break outer;
                                    }
                                    break;
                            }
                        }
                }
                
                System.err.println("Services Registration for UCN in domain \"" + domain + "\" Failed!");
                if (hostnameResolves)
                {
                    System.err.println("Hostname \"" + fqn + "\" can be resolved.");
                } else
                {
                    System.err.println("Hostname \"" + fqn + "\" cannot be resolved!");
                }
            }
            
            while (true)
            {
                Thread.sleep(10);
                if (System.in.read() == 'q')
                {
                    break;
                }
            }
            
            if (service.unregister(registeredService))
            {
                System.out.println("Services Successfully Unregistered: \n\t" + registeredService);
            } else
            {
                System.err.println("Services was not Unregistered: \n\t" + registeredService);
            }
            
            service.close();
            System.exit(0);
        } catch (Exception e)
        {
            System.err.println("Error Registering Capability for Discovery - " + e.getMessage());
            e.printStackTrace(System.err);
        }
	}
}
