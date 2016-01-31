package com.example.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.example.dto.Employee;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.io.CsvEntityReader;

public class EmployeeItemReader implements ItemReader<Employee> {
	
	private String fileName;
	
	private CsvConfig config;
	
	private InputStreamReader in;
	
	CsvEntityReader<Employee> entityReader;
	
	private int lineCount = 0;
	
	public EmployeeItemReader() {
		init();
	}
	
	private void init() {
		config = new CsvConfig();
		config.setIgnoreEmptyLines(true);
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(entityReader != null) entityReader.close();
	}

	@Override
	public Employee read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		Employee entity = null;
		
		try {
			
			if(in == null) {
				in = new InputStreamReader(new FileInputStream(this.fileName), "MS932");
				CsvReader csvReader = new CsvReader(in, config);
				entityReader = CsvEntityReader.newInstance(csvReader, Employee.class);
			}
			
			entity = readLine();
			
		} catch(UnsupportedEncodingException e) {
			throw new UnexpectedInputException("encoding not supported.", e);
		} catch(FileNotFoundException e) {
			throw new UnexpectedInputException("file not found", e);
		} catch(IOException e) {
			throw new NonTransientResourceException("Some problem has occured in file I/O.", e);
		}
		
		return entity;
	}

	public void setFileName(String path) {
		this.fileName = path;
	}
	
	public void setSkipLines(int skipLines) {
		config.setSkipLines(skipLines);
	}
	
	private Employee readLine() throws ParseException, NonTransientResourceException, IOException {
		Employee value = entityReader.read();
		if(value != null ) value.setLineNumber(++lineCount);
		return value;
	}

	

}
