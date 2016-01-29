package com.example.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.dto.Employee;

@Configuration
@EnableBatchProcessing
public class BatchConfigulation {
	
	
	@Bean
	@Value("#{jobParameters['fileName']}")
	public ItemReader<Employee> reader(final String fileName) {
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
		return null;
	}

}
