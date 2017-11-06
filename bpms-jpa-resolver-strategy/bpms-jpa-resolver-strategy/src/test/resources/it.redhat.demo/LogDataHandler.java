package it.redhat.demo.handlers;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

public class LogDataHandler extends AbstractLogOrThrowWorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 10000; i++) {
			System.out.println("Work item handler working");
		}
		
		/*
		boolean validCustomer = true;
		
		if(validCustomer) {
			throw new RuntimeException();
		}
		*/
		
		manager.completeWorkItem(workItem.getId(), null);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}

}
