package com.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

public class Step2ExecutionListener extends StepExecutionListenerSupport {
	
	private final Logger log = LoggerFactory.getLogger(Step2ExecutionListener.class);
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("!! Step2 started !!");
	}
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		BatchStatus stepExecutionStatus = stepExecution.getStatus();
		if(stepExecutionStatus == BatchStatus.COMPLETED
				|| stepExecutionStatus == BatchStatus.ABANDONED) {
			log.info("!! Step2 completed !!");
		} else {
			log.info("!! Step2 failed !!");
		}
		return stepExecution.getExitStatus();
	}

}
