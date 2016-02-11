package com.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

public class Step1ExecutionListener extends StepExecutionListenerSupport {
	
	private final Logger log = LoggerFactory.getLogger(Step1ExecutionListener.class);
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("!! Step1 started !!");
	}
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		BatchStatus stepExecutionStatus = stepExecution.getStatus();
		if(stepExecutionStatus == BatchStatus.COMPLETED
				|| stepExecutionStatus == BatchStatus.ABANDONED) {
			log.info("!! Step1 completed !!");
		} else {
			log.info("!! Step1 failed !!");
		}
		return stepExecution.getExitStatus();
	}

}
