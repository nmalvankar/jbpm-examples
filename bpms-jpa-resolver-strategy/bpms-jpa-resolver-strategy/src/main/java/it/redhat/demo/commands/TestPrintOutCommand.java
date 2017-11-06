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

    public ExecutionResults execute(CommandContext ctx) {
        logger.info("Command executed on executor with data {} at {}", ctx.getData(), new Date());
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }

	@Override
	public Date getScheduleTime() {
		long currentTime = System.currentTimeMillis();
		return new Date(currentTime + 1000);
	}
}
