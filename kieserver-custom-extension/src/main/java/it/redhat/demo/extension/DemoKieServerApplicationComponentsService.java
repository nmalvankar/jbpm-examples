package it.redhat.demo.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jbpm.services.api.ProcessService;
import org.kie.server.services.api.KieServerApplicationComponentsService;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.api.SupportedTransports;

public class DemoKieServerApplicationComponentsService implements KieServerApplicationComponentsService {
	
	public static final String OWNER_EXTENSION="jBPM";

	public Collection<Object> getAppComponents(String extension, SupportedTransports type, Object... services) {
		// skip calls from other than owning extension
        if ( !OWNER_EXTENSION.equals(extension) ) {
            return Collections.emptyList();
        }
         
        ProcessService processService = null;
        KieServerRegistry context = null;
        
        for( Object object : services ) { 
            if( ProcessService.class.isAssignableFrom(object.getClass()) ) { 
            	processService = (ProcessService) object;
                continue;
            } else if( KieServerRegistry.class.isAssignableFrom(object.getClass()) ) {
                context = (KieServerRegistry) object;
                continue;
            }
        }
         
        List<Object> components = new ArrayList<Object>(1);
        if( SupportedTransports.REST.equals(type) ) {
            components.add(new CustomResource(processService, context));
        }
         
        return components;
	}

}
