package it.redhat.demo.mappers;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceWithCustomVarsQueryMapper;

public class CustomMapper extends UserTaskInstanceWithCustomVarsQueryMapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5725977269012565932L;
	
	public CustomMapper() {
		super(getVariableMapping());
	}
	
	protected static Map<String, String> getVariableMapping() {
		Map<String, String> variableMap = new HashMap<String, String>();
		
		variableMap.put("premium", "integer");
		variableMap.put("SUBMITTED_BY", "string");
		variableMap.put("parentProcessInstanceId", "integer");
		variableMap.put("process-instance-name", "string");	
		
		return variableMap;
		
	}
	
	@Override
	public String getName() {
		return "WorklistQueryMapper";
	}

}
