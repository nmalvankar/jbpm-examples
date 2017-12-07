package it.redhat.demo.commands;

import java.util.Date;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.Reoccurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPrintOutCommand implements Command, Reoccurring{

	private static final Logger logger = LoggerFactory.getLogger(TestPrintOutCommand.class);

    public ExecutionResults execute(CommandContext ctx) throws Exception {
        logger.info("Command executed on executor with data {} at {}", ctx.getData(), new Date());
        
        boolean condition = true;
        
        try {
        
	        if(condition) {
	        	throw new Error("Deliberate throw");
	        }
        } catch (Throwable e) {
        	logger.error("Throwing Runtime exception.... " + e);
        	e.printStackTrace();
        }
        
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }

	@Override
	public Date getScheduleTime() {
		long currentTime = System.currentTimeMillis();
		Date nextSchedule = new Date(currentTime + 600000);
		logger.info("Next schedule for " + TestPrintOutCommand.class.getCanonicalName() + " : " + nextSchedule);
		
		return nextSchedule;
	}
}
