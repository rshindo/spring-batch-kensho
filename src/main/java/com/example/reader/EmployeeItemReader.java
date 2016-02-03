package com.example.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.example.dto.Employee;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.annotation.CsvColumnException;
import com.orangesignal.csv.io.CsvEntityReader;

public class EmployeeItemReader implements ItemReader<Employee>, ItemStream {
	
	private String fileName;
	
	private CsvConfig config;
	
	private CsvEntityReader<Employee> entityReader;
	
	private static final Logger log = LoggerFactory.getLogger(EmployeeItemReader.class);
	
	private int lineCount = 0;
	
	private int restartIndex = 0;
	
	public EmployeeItemReader() {
		init();
	}
	
	private void init() {
		config = new CsvConfig();
		config.setIgnoreEmptyLines(true);
	}

	@Override
	public Employee read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		Employee entity = null;
		
		try {
			
			if(entityReader == null) {
				InputStreamReader in = new InputStreamReader(new FileInputStream(this.fileName), "MS932");
				CsvReader csvReader = new CsvReader(in, config);
				entityReader = CsvEntityReader.newInstance(csvReader, Employee.class);
			}

			entity = readLine();
		
		} catch(ParseException e) {
			log.info("faild to parse the line. line number:" + lineCount);
		} catch(UnsupportedEncodingException e) {
			throw new UnexpectedInputException("encoding not supported.", e);
		} catch(FileNotFoundException e) {
			throw new UnexpectedInputException("file not found", e);
		} catch(IOException e) {
			throw new NonTransientResourceException("Some problems have occured in file I/O.", e);
		} finally {
			// entityがnullであればEOFなので、確実にリソースの開放を行う.
			if(entity == null && entityReader != null) {
				entityReader.close();
			}
		}
		
		return entity;
	}

	public void setFileName(String path) {
		this.fileName = path;
	}
	
	public void setSkipLines(int skipLines) {
		config.setSkipLines(skipLines);
	}
	
	/**
	 * 一行をPOJOにマッピングして取得します.EOFだった場合はnullを戻します.
	 * @return　Employeeインスタンス
	 * @throws ParseException
	 * @throws NonTransientResourceException
	 * @throws IOException
	 */
	private Employee readLine() throws ParseException, IOException {
		
		Employee value = null;
		try {
			value = entityReader.read();
			if(value != null ) value.setLineNumber(++lineCount);
			
			// リスタート位置までストリームを進める
			if(lineCount <= restartIndex) {
				value = readLine();
			}
		} catch (CsvColumnException e) {
			throw new ParseException("faild to parse the line.", e);
		}
		return value;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		this.restartIndex = executionContext.getInt("partnerCsvLine", 0);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt("partnerCsvLine", lineCount);
		
	}

	@Override
	public void close() throws ItemStreamException {
	}

	

}
