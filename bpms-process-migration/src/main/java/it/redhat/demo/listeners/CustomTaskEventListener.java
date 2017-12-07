package it.redhat.demo.listeners;

import org.jbpm.runtime.manager.impl.mapper.EnvironmentAwareProcessInstanceContext;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskEvent;

public class CustomTaskEventListener extends DefaultTaskEventListener {

	private RuntimeManager runtimeManager = null;

	public CustomTaskEventListener(RuntimeManager runtimeManager) {
		this.runtimeManager = runtimeManager;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void beforeTaskClaimedEvent(TaskEvent event) {

		RuntimeEngine engine = runtimeManager.getRuntimeEngine(EnvironmentAwareProcessInstanceContext.get(event.getTask().getTaskData().getProcessInstanceId()));
		KieSession ksession = engine.getKieSession();

		System.out.println("Kie Session is : " + ksession.getId());

		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
				.getProcessInstance(event.getTask().getTaskData().getProcessInstanceId());

		System.out.println("Process Instance ID: " + processInstance.getId());

		System.out.println("Before task claim event for task id: " + event.getTask().getId());
		
	}

}
