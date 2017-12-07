package it.redhat.demo.handlers;

import java.util.Collection;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public class CustomHandler extends AbstractLogOrThrowWorkItemHandler {

	private KieSession ksession;

	public CustomHandler(KieSession ksession) {
		this.ksession = ksession;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 10000; i++) {
			System.out.println("Work item handler working");
		}

		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) this.ksession
				.getProcessInstance(workItem.getProcessInstanceId());
		
		Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
		
		for(NodeInstance nodeInstance : nodeInstances) {
			if ("ManagerTask".equalsIgnoreCase(nodeInstance.getNodeName()))
				System.out.println("Node Instance ID of previous node: " + nodeInstance.getId());
		}

		manager.completeWorkItem(workItem.getId(), null);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub

	}

}
