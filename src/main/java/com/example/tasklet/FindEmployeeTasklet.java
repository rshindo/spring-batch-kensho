package com.example.tasklet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.dto.Employee;

public class FindEmployeeTasklet implements Tasklet {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(FindEmployeeTasklet.class);

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		Long jobExecutionId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();
		
		log.info("JOB_EXCUTION_ID : " + jobExecutionId.toString()); 
		
//		jdbcTemplate.query("SELECT * FROM employees", 
//				(ResultSet rs, int row) -> 
//					new Employee(row, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)))
//					.stream()
//					.forEach(emp -> log.info("!! find employee ! :" + emp.toString()));
		
		jdbcTemplate.query("SELECT * FROM employees", new BeanPropertyRowMapper<Employee>(Employee.class))
					.stream()
					.forEach(emp -> log.info("!! find employee ! :" + emp.toString()));
		
		
		return RepeatStatus.FINISHED;
	}

}
