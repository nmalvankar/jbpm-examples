package it.redhat.demo.mappers;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceWithCustomVarsQueryMapper;

public class CustomMapper2 extends UserTaskInstanceWithCustomVarsQueryMapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5725977269012565932L;
	
	public CustomMapper2() {
		super(getVariableMapping());
	}
	
	protected static Map<String, String> getVariableMapping() {
		Map<String, String> variableMap = new HashMap<String, String>();
		
		variableMap.put("worklistCount", "integer");
		
		return variableMap;
		
	}
	
	@Override
	public String getName() {
		return "WorklistCountMapper";
	}

}
