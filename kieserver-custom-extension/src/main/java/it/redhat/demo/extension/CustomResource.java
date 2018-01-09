package it.redhat.demo.extension;

import static org.kie.server.remote.rest.common.util.RestUtils.createResponse;
import static org.kie.server.remote.rest.common.util.RestUtils.getContentType;
import static org.kie.server.remote.rest.common.util.RestUtils.getVariant;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.ProcessService;
import org.kie.api.KieServices;
import org.kie.api.command.KieCommands;
import org.kie.server.api.KieServerConstants;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieServerConfig;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.jbpm.jpa.PersistenceUnitInfoImpl;
import org.kie.server.services.jbpm.jpa.PersistenceUnitInfoLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("server/custom-extension")
public class CustomResource {
 
    private static final Logger logger = LoggerFactory.getLogger(CustomResource.class);
     
    private KieCommands commandsFactory = KieServices.Factory.get().getCommands();
    
    private String persistenceUnitName = "org.jbpm.domain";
    
    private static final String PERSISTENCE_XML_LOCATION = "/jpa/META-INF/persistence.xml";

    private ProcessService processService;
    private KieServerRegistry registry;
 
    public CustomResource() {
 
    }
 
    public CustomResource(ProcessService processService, KieServerRegistry registry) {
        this.processService = processService;
        this.registry = registry;
    }
    
    
    @GET
    @Path("/custom-endpoint")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response echoProcess(@Context HttpHeaders headers) {
 
        Variant v = getVariant(headers);
        String contentType = getContentType(headers);
        
        MarshallingFormat format = MarshallingFormat.fromType(contentType);
        if (format == null) {
            format = MarshallingFormat.valueOf(contentType);
        }
        
        KieServerConfig config = registry.getConfig();
        
/*      EntityManagerFactory emf = build(getPersistenceProperties(config));
        EntityManagerFactoryManager.get().addEntityManagerFactory(persistenceUnitName, emf);
*/        
        String response = null;
        
        try {
        	response = "Response from extension";
        	 return createResponse(response, v, Response.Status.OK);
        } catch (Exception e) {
        	response = "Execution failed with error : " + e.getMessage();
        	logger.error("Unexpected error during processing", e.getMessage(), e);
        	return createResponse(response, v, Response.Status.INTERNAL_SERVER_ERROR);
        }
 
    }
    
    protected Map<String, String> getPersistenceProperties(KieServerConfig config) {
        Map<String, String> persistenceProperties = new HashMap<String, String>();

        persistenceProperties.put("hibernate.dialect", config.getConfigItemValue(KieServerConstants.CFG_PERSISTANCE_DIALECT, "org.hibernate.dialect.H2Dialect"));
        persistenceProperties.put("hibernate.default_schema", config.getConfigItemValue(KieServerConstants.CFG_PERSISTANCE_DEFAULT_SCHEMA));
        persistenceProperties.put("hibernate.transaction.jta.platform", config.getConfigItemValue(KieServerConstants.CFG_PERSISTANCE_TM, "org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform"));
        persistenceProperties.put("javax.persistence.jtaDataSource", config.getConfigItemValue(KieServerConstants.CFG_PERSISTANCE_DS, "java:jboss/datasources/ExampleDS"));

        return persistenceProperties;
    }
    
    protected EntityManagerFactory build(Map<String, String> properties) {
        try {
            InitialContext ctx = new InitialContext();
            InputStream inputStream = PersistenceUnitInfoLoader.class.getResourceAsStream(PERSISTENCE_XML_LOCATION);
            PersistenceUnitInfo info = PersistenceUnitInfoLoader.load(inputStream, ctx, this.getClass().getClassLoader());
            // prepare persistence unit root location
            URL root = PersistenceUnitInfoLoader.class.getResource(PERSISTENCE_XML_LOCATION);
            String jarLocation = root.toExternalForm().split("!")[0].replace(PERSISTENCE_XML_LOCATION, "");
            try {
                ((PersistenceUnitInfoImpl) info).setPersistenceUnitRootUrl(new URL(jarLocation));
            } catch (Exception e) {
                // in case setting URL to jar file location only fails, fallback to complete URL
                ((PersistenceUnitInfoImpl) info).setPersistenceUnitRootUrl(root);
            }
            // Need to explicitly set jtaDataSource here, its value is fetched in Hibernate logger before configuration
            ((PersistenceUnitInfoImpl) info).setJtaDataSource(properties.get("javax.persistence.jtaDataSource"));
            List<PersistenceProvider> persistenceProviders = PersistenceProviderResolverHolder.getPersistenceProviderResolver().getPersistenceProviders();
            PersistenceProvider selectedProvider = null;
            if (persistenceProviders != null) {
                for (PersistenceProvider provider : persistenceProviders) {
                    if (provider.getClass().getName().equals(info.getPersistenceProviderClassName())) {
                        selectedProvider = provider;
                        break;
                    }
                }
            }

            return selectedProvider.createContainerEntityManagerFactory(info, properties);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create EntityManagerFactory due to " + e.getMessage(), e);
        }
    }
}

