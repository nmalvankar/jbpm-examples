package it.redhat.demo.commands;

import org.jbpm.services.task.commands.TaskCommand;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

public class DeleteTaskVariableImplCommand extends TaskCommand<Void> {
	
    private static final long serialVersionUID = -7929370526623674312L;

    public DeleteTaskVariableImplCommand() { 
        // default, delete all
    }
    
    public DeleteTaskVariableImplCommand(long taskId) {
        this.taskId = taskId;
    }

    @Override
    public Void execute(Context context) {
        TaskPersistenceContext persistenceContext = ((TaskContext) context).getPersistenceContext();
        if( this.taskId != null ) { 
            persistenceContext.executeUpdateString("delete from TaskVariableImpl b where b.taskId = "+ this.taskId);
        } else { 
            persistenceContext.executeUpdateString("delete from TaskVariableImpl b");
        }
        return null;
    }

}
