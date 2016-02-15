package com.example.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
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
import com.orangesignal.csv.CsvTokenException;
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
		config.setVariableColumns(false);
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		log.info("[EmployeeItemReader] JOB_EXECUTION_ID : " + stepExecution.getJobExecutionId());
	}

	@Override
	public Employee read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		Employee entity = null;
		entity = readLine();
		return entity;

	}

	public void setFileName(String path) {
		this.fileName = path;
	}

	public void setSkipLines(int skipLines) {
		config.setSkipLines(skipLines);
	}

	/**
	 * 一行を一行読み込み、POJOにマッピングして取得します.<br>
	 * パースに失敗した場合は次の行を読み込んでPOJOを戻し、EOFだった場合はnullを戻します.
	 *
	 * @return　Employeeインスタンス
	 * @throws IOException ファイル読み込みに異常が発生した場合
	 */
	private Employee readLine() throws IOException {

		Employee value = null;
		boolean error = false;
		try {
			++lineCount;
			value = entityReader.read();
			if(value != null ) value.setLineNumber(lineCount);

			// リスタート位置までストリームを進める
			if(lineCount <= restartIndex) {
				value = readLine();
			}
		} catch (CsvColumnException e) {
			log.warn("!! failed to parse a line " + lineCount);
			error = true;
		} catch(CsvTokenException e) {
			log.warn("!! failed to parse a line " + lineCount + " : number of columns doesn't match.");
			error = true;
		}

		//パースに失敗した場合は次の行を読み込む
		if(error) {
			value = readLine();
		}

		return value;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {

		try {
			if(entityReader == null) {
				InputStreamReader in = new InputStreamReader(new FileInputStream(this.fileName), "MS932");
				CsvReader csvReader = new CsvReader(in, config);
				entityReader = CsvEntityReader.newInstance(csvReader, Employee.class);
			}
		} catch(FileNotFoundException | UnsupportedEncodingException e) {
			throw new ItemStreamException("cannot open file", e);
		}
		this.restartIndex = executionContext.getInt("partnerCsvLine", 0);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt("partnerCsvLine", lineCount);

	}

	@Override
	public void close() throws ItemStreamException {
		try {
			if(entityReader != null) {
				entityReader.close();
				entityReader = null;
			}
		} catch (IOException e) {
			throw new ItemStreamException("cannot close file", e);
		}
	}



}
