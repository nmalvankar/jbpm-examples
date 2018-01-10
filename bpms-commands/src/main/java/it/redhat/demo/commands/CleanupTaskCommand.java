package it.redhat.demo.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.commands.GetCompletedTasksCommand;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.Reoccurring;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.InternalTaskService;

public class CleanupTaskCommand implements Command, Reoccurring {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public Date getScheduleTime() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private InternalTaskService taskService;
	
	public ExecutionResults execute(CommandContext ctx) throws Exception {
		// TODO Auto-generated method stub
		SimpleDateFormat formatToUse = DATE_FORMAT;
		String dataFormat = (String) ctx.getData("DateFormat");
		if (dataFormat != null) {
			formatToUse = new SimpleDateFormat(dataFormat);
		}
		
		// collect parameters
		String olderThan = (String)ctx.getData("OlderThan");
		String olderThanPeriod = (String)ctx.getData("OlderThanPeriod");
		
		if (olderThanPeriod != null) {
			long olderThanDuration = DateTimeUtils.parseDateAsDuration(olderThanPeriod);
			Date olderThanDate = new Date(System.currentTimeMillis() - olderThanDuration);
			
			olderThan = formatToUse.format(olderThanDate);
		}
		
		String emfName = (String)ctx.getData("EmfName");
		if (emfName == null) {
			emfName = "org.jbpm.domain"; 
		}
		
		EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(emfName);
		
		
		this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator() 
	            .entityManagerFactory(emf) 
	            .listener(new JPATaskLifeCycleEventListener(true)) 
	            .listener(new BAMTaskEventListener(true)) 
	            .getTaskService(); 
		
		RuntimeDataService runtimeDataService = new RuntimeDataServiceImpl();
		((RuntimeDataServiceImpl) runtimeDataService).setCommandService(new TransactionalCommandService(emf));
		
		/*RuntimeDataService runtimeDataService = new RuntimeDataServiceImpl();
		((RuntimeDataServiceImpl) runtimeDataService).setCommandService(new TransactionalCommandService(emf));
		
		List<Integer> states = new ArrayList<Integer>();
		states.add(new Integer(2));
		
		Collection<ProcessInstanceDesc> proInstanceDescs = runtimeDataService.getProcessInstances(states, null, new QueryContext());
		
		for(ProcessInstanceDesc processInstanceDesc : proInstanceDescs) 
			System.out.println("Process Instance ID: " + processInstanceDesc.getId() + "Process Inst name: " + processInstanceDesc.getProcessId());
		
		*/
		
		//Get list of completed tasks
		List<TaskSummary> completedTasks = taskService.execute(new GetCompletedTasksCommand());
		
		List<TaskSummary> deleteTasks = new ArrayList<TaskSummary>();
		
		for(TaskSummary taskSummary : completedTasks) {
			System.out.println("Task id: " + taskSummary.getId() + "& Task Status:" + taskSummary.getStatus());
			ProcessInstanceDesc processInstanceDesc = runtimeDataService.getProcessInstanceById(taskSummary.getProcessInstanceId());
			if(processInstanceDesc.getState() == ProcessInstance.STATE_ACTIVE || !taskSummary.getCreatedOn().before(olderThan==null?null:formatToUse.parse(olderThan)))
				deleteTasks.add(taskSummary);
		}
		
		//Remove tasks with active process instance
		completedTasks.removeAll(deleteTasks);
		
		//Archive & remove the tasks
		if(!completedTasks.isEmpty()) {
			taskService.archiveTasks(completedTasks);
			taskService.removeTasks(completedTasks);
		}
		
		//Finally Call the LogCleanup Command
        
        return null;
	}

	public InternalTaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(InternalTaskService taskService) {
		this.taskService = taskService;
	}

}
