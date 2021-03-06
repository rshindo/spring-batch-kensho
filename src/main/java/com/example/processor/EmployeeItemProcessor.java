package com.example.processor;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.dto.Employee;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee> {

	private final Logger log = LoggerFactory.getLogger(EmployeeItemProcessor.class);

	@Autowired
	private Validator validator;

	@Override
	public Employee process(Employee item) throws Exception {

		log.info(item.toString());

		Set<ConstraintViolation<Employee>> result = validator.validate(item);

		if(result.size() == 0) {
			return item;
		}

		for (ConstraintViolation<Employee> constraintViolation : result) {
			String message = constraintViolation.getMessage();
			log.info(message);
		}


		return null;
	}

}
