package com.example.config;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;

import com.example.dto.Employee;

@Configuration
@EnableBatchProcessing
public class BatchConfigulation {
	
	
	@Bean
	@JobScope
	public ItemReader<Employee> reader(@Value("#{jobParameters['fileName']}") final String fileName) {
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
		reader.setResource(new FileSystemResource(fileName));
		reader.setLineMapper(new DefaultLineMapper<Employee>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] {"departmentCode", "departmentName",
						"prtId", "name", "email"});
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
				setTargetType(Employee.class);
			}});
		}});
		return reader;
	}
	
	@Bean
	public ItemProcessor<Employee, Employee> processor() {
		return (Employee e1) -> e1; 
	}
	
	@Bean
	public ItemWriter<Employee> writer() {
		return (List<? extends Employee> list) -> list.stream().forEach(System.out::println);
	}
	
	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Employee> reader,
			ItemWriter<Employee> writer, ItemProcessor<Employee, Employee> processor) {
		return stepBuilderFactory.get("step1")
				.<Employee, Employee> chunk(10)
				.reader(reader)
				.writer(writer)
				.processor(processor)
				.build();
	}
	
	@Bean
	public Job importUserJob(JobBuilderFactory jobs, Step s1) {
		return jobs.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.flow(s1)
				.end()
				.build();
	}

}
