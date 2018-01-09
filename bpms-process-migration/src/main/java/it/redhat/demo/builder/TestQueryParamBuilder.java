package it.redhat.demo.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.jbpm.services.api.query.QueryParamBuilder;

public class TestQueryParamBuilder implements QueryParamBuilder<ColumnFilter> {
	 
    private Map<String, Object> parameters;
    
    private boolean built = false;
    public TestQueryParamBuilder(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
     
    @Override
    public ColumnFilter build() {
        // return null if it was already invoked
        if (built) {
            return null;
        }
        
        Subject mySubject = null;
		try {
			mySubject = (Subject)PolicyContext.getContext("javax.security.auth.Subject.container");
			
		} catch (PolicyContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("Name: " + mySubject.getPrincipals());
                 
        String columnName = "oeid";
        
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add("kie-server");
        allowedValues.add("rest-all");
         
        ColumnFilter filter = FilterFactory.in(columnName, allowedValues);
        filter.setColumnId(columnName);
        
        built = true;
        return filter;
    }
 
}
