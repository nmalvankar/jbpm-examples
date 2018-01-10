package it.redhat.demo.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.commands.LogCleanupCommand;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.audit.commands.DeleteBAMTaskSummariesCommand;
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

public class WotCleanupCommand implements Command, Reoccurring{
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public Date getScheduleTime() {
		// TODO Auto-generated method stub
		return null;
	}

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
		
		
		//Get Entity manager factory
		EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate(emfName);
		
		//Get runtimedataservice instance
		RuntimeDataService runtimeDataService = new RuntimeDataServiceImpl();
		((RuntimeDataServiceImpl) runtimeDataService).setCommandService(new TransactionalCommandService(emf));
		
		//Get taskService instance
		InternalTaskService taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator() 
	            .entityManagerFactory(emf) 
	            .listener(new JPATaskLifeCycleEventListener(true)) 
	            .listener(new BAMTaskEventListener(true)) 
	            .getTaskService();
		
		//Get list of completed tasks
		List<TaskSummary> completedTasks = taskService.execute(new GetCompletedTasksCommand());
		
		List<TaskSummary> validTasks = new ArrayList<TaskSummary>();
		
		//Identify tasks which shouldn't be deleted
		for(TaskSummary taskSummary : completedTasks) {
			System.out.println("Task id: " + taskSummary.getId() + "& Task Status:" + taskSummary.getStatus());
			ProcessInstanceDesc processInstanceDesc = runtimeDataService.getProcessInstanceById(taskSummary.getProcessInstanceId());
			Date taskCreatedOn = formatToUse.parse(taskSummary.getCreatedOn().toString()); 
			if(processInstanceDesc.getState() == ProcessInstance.STATE_ACTIVE || !taskCreatedOn.before(olderThan==null?null:formatToUse.parse(olderThan)))
			//if(processInstanceDesc.getState() == ProcessInstance.STATE_ACTIVE)
				validTasks.add(taskSummary);
		}
		
		//Remove tasks with active process instance or >= olderThan date
		completedTasks.removeAll(validTasks);
				
		//Archive & remove the tasks
		if(!completedTasks.isEmpty()) {
			taskService.archiveTasks(completedTasks);
			taskService.removeTasks(completedTasks);
		}
				
				
		for(TaskSummary summary : completedTasks) {
			//Deleting data from TaskVariableImpl & BAMTaskSummaries
			
			//Delete TaskVariableImpl
			taskService.execute(new DeleteTaskVariableImplCommand(summary.getId()));
			
			//Delete BAMTaskSummary
			taskService.execute(new DeleteBAMTaskSummariesCommand(summary.getId()));
		}
		
		//Finally call LogCleanup
		CommandContext commandContext = new CommandContext();
		commandContext.setData("SingleRun", "true");
		commandContext.setData("OlderThan", olderThan);
		LogCleanupCommand cleanupCommand = new LogCleanupCommand();
		ExecutionResults result = cleanupCommand.execute(commandContext);
		
		return result;
	}

}
