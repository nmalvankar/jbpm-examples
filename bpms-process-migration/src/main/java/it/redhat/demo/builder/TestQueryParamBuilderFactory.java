package it.redhat.demo.builder;

import java.util.Map;

import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;

public class TestQueryParamBuilderFactory implements QueryParamBuilderFactory {
	 
    @Override
    public boolean accept(String identifier) {
        if ("test".equalsIgnoreCase(identifier)) {
            return true;
        }
        return false;
    }
 
    @Override
    public QueryParamBuilder newInstance(Map<String, Object> parameters) {
        return new TestQueryParamBuilder(parameters);
    }
 
}

