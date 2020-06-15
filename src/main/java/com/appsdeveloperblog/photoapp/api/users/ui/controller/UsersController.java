package com.appsdeveloperblog.photoapp.api.users.ui.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.appsdeveloperblog.photoapp.api.users.ui.model.ExistingUserReponseModel;
import com.appsdeveloperblog.photoapp.api.users.ui.model.UserResponseModel;

@RestController
@RequestMapping("/users")
public class UsersController {
	
	@Autowired
	private Environment env;
	
	private UsersService usersService;
	
	
	public UsersController() {
		
	}
	
	@Autowired
	public UsersController(UsersService usersService) {
		this.usersService = usersService;
		
	}
	
	@GetMapping("status/check")
	public String status() {
		return "working port = " + env.getProperty("local.server.port") + ", with secret token =" 
	           + env.getProperty("token.secret") + "****";
	}

	
	
	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}, 
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<UserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userRequest) {
		
	 
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT );
		UserDto userDto = modelMapper.map(userRequest, UserDto.class);
		UserDto responseUserDto = usersService.createUser(userDto);
		
		UserResponseModel userResponseModel = modelMapper.map(responseUserDto, UserResponseModel.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(userResponseModel);
	}
	
	@GetMapping(value="/{userId}",  
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ExistingUserReponseModel> getUser(@PathVariable("userId") String userId) {
		
		
		UserDto responseUserDto = usersService.getUserDetailsByUserId(userId);
		
		
		if (responseUserDto != null) {
			ModelMapper modelMapper = new ModelMapper();
			ExistingUserReponseModel userResponseModel = modelMapper.map(responseUserDto, ExistingUserReponseModel.class);
			
			System.out.println("UserDto=" + responseUserDto);
			System.out.println("userResponseModel=" + userResponseModel);
			return ResponseEntity.status(HttpStatus.FOUND).body(userResponseModel);
		}
		else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		
		
	}
}
