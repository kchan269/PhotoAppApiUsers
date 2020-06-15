package com.appsdeveloperblog.photoapp.api.users.ui.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateUserRequestModel {
	
	
	@NotNull(message="first Name is required")
	@Size(min=1, max=30, message="first name must be at least one char and can't exceed 30 chars")
	private String firstName;
	
	@NotNull(message="last Name is required")
	@Size(min=1, max=30, message="last name must be at least one char and can't exceed 30 chars")
	private String lastName;
	
	@Pattern(regexp="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})", message="Password must contain at least 1 upper case, 1 special char, and greater 6 char")
	private String password;
	
	@Email(message="invalid email format")
	private String email;
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
	

}
