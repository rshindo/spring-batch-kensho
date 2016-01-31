package com.example.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvEntity;

import lombok.Data;

@Data
@CsvEntity(header=false)
public class Employee {
	
	private int lineNumber;
	
	@CsvColumn(position=0)
	@NotEmpty
	@Size(min=4, max=4)
	private String departmentCode;
	
	@CsvColumn(position=1)
	@Size(max=200)
	private String departmentName;
	
	@CsvColumn(position=2)
	@NotEmpty
	@Size(min=6, max=6)
	private String ptrId;
	
	@CsvColumn(position=3)
	@Pattern(regexp="^.+　.+$")
	private String name;
	
	@CsvColumn(position=4)
	@Email
	private String email;
	
	

}
