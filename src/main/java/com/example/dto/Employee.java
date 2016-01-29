package com.example.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Employee {
	
	@NotEmpty
	@Size(min=4, max=4)
	private String departmentCode;
	
	@Size(max=200)
	private String departmentName;
	
	@NotEmpty
	@Size(min=6, max=6)
	private String ptrId;
	
	@Pattern(regexp="^.+Å@.+$")
	private String name;
	
	@Email
	private String email;

}
