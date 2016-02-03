package com.example.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.dto.Employee;
import com.example.processor.EmployeeItemProcessor;
import com.example.reader.EmployeeItemReader;
import com.example.tasklet.FindEmployeeTasklet;

@Configuration
@EnableBatchProcessing
public class BatchConfigulation {
	
	/*
	 * ================================================
	 *                   Step1
	 * ================================================
	 */
	
	@Bean(name="employeeItemReader")
	@StepScope
	public FlatFileItemReader<Employee> reader(@Value("#{jobParameters['fileName']}") final String fileName) {
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
		reader.setEncoding("Shift-JIS");
		reader.setResource(new FileSystemResource(fileName));
		reader.setLineMapper(new DefaultLineMapper<Employee>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] {"departmentCode", "departmentName",
						"ptrId", "name", "email"});
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
				setTargetType(Employee.class);
			}});
		}});
		reader.setLinesToSkip(1);
		return reader;
	}
	
	@Bean(name="employeeItemReaderSub")
	@StepScope
	public EmployeeItemReader readerSub (@Value("#{jobParameters['fileName']}") final String fileName) {
		EmployeeItemReader reader = new EmployeeItemReader();
		reader.setSkipLines(1);
		reader.setFileName(fileName);
		return reader;
	}
	
	@Bean(name="employeeItemProcessor")
	public ItemProcessor<Employee, Employee> processor() {
		return new EmployeeItemProcessor(); 
	}
	
	@Bean(name="employeeItemWriter")
	public ItemWriter<Employee> writer(DataSource dataSource) {
		JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
		writer.setDataSource(dataSource);
		writer.setSql("INSERT INTO employees (department_code, department_name, ptr_id, name, email) "
				+ "VALUES (:departmentCode, :departmentName, :ptrId, :name, :email)");
		return writer;
	}
	
	@Bean(name="step1")
	public Step step1(StepBuilderFactory stepBuilderFactory, 
			@Qualifier("employeeItemReaderSub") ItemReader<Employee> reader,
			@Qualifier("employeeItemWriter") ItemWriter<Employee> writer, 
			@Qualifier("employeeItemProcessor") ItemProcessor<Employee, Employee> processor) {
		return stepBuilderFactory.get("step1")
				.<Employee, Employee> chunk(1)
				.reader(reader)
				.writer(writer)
				.processor(processor)
				.build();
	}
	

	/*
	 * ================================================
	 *                   Step2
	 * ================================================
	 */
	
	@Bean(name="tasklet1")
	public Tasklet tasklet1() {
		return new FindEmployeeTasklet();
	}
	
	@Bean(name="step2")
	public Step step2(StepBuilderFactory stepBuilderFactory,
			@Qualifier("tasklet1") Tasklet tasklet) {
		return stepBuilderFactory.get("step2")
				.tasklet(tasklet)
				.build();
	}
	
	
	/*
	 * ================================================
	 *               Job Definition
	 * ================================================
	 */
	
	@Bean
	public Job importUserJob(JobBuilderFactory jobs, @Qualifier("step1") Step s1, @Qualifier("step2") Step s2) {
		return jobs.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.flow(s1)
				.next(s2)
				.end()
				.build();
	}

}
