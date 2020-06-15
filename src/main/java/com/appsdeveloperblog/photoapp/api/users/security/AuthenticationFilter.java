package com.appsdeveloperblog.photoapp.api.users.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.LoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	
	private UsersService usersService;
	
	// we will use environment to access property fole so that we can read the secret token from our property file
	// this secret token when we are envrypting JWT token.
	private Environment env;
	
	public AuthenticationFilter(UsersService usersService, Environment env, AuthenticationManager authenticationManager) {
		this.usersService = usersService;
		this.env = env;
		super.setAuthenticationManager(authenticationManager);
	}
	
	//attemptauthentication needs to know where and how to find the user details and to help bring framework find
	// user details so it can perform the authentication.  We need a couple of methods.
	// 1.  we need to implement configure(authenticationMangerbuilder) .. in WebSecurity class
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		 
		 try {
			
			// objectMapper will map request object which read email address and password to LoginRequestModel 
			 LoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
			 
			 // pass user log in request to usernamepasswordauth token 
			 // that object will be passed on to authenticate method which is called on the 
			 // authentication manger object and 
			 // getauthenticationManger will return that authentication manger object 
			 // getAuthenicationManger comes from spring security
			 return getAuthenticationManager().authenticate( 
					 new UsernamePasswordAuthenticationToken(creds.getEmail(),
							                                 creds.getPassword(),
							                                 new ArrayList<>())
					 );
			 
		 }
		 catch (IOException e){
			 throw new RuntimeException(e);
			 
		 }
		 
		 
		 
	}

	// Once Spring framework is done performing user authentication. 
	// It will call this successfulauthentication method itself
	// We are not calling this method ourselves
	// however we need to provide implementation of this method
	// and the job of this method will be to take user details and then 
	// use those user details like user name or user id to generate JWT token and then add  
	// JWT token to  HTTP response header and return it back with  http response. The client 
	// application will then be able to read this JWT token from http response header and use this
	// JWT token in the subsequent request to our application as authorization header
	protected void successfulAuthentication(HttpServletRequest req, 
            HttpServletResponse res,
            FilterChain chain,
            Authentication auth) throws IOException, ServletException {
		
		String userName = ((User) auth.getPrincipal()).getUsername();
		UserDto userDto = usersService.getUserDetailsByEmail(userName);
		
		System.out.println("expriy time=" + env.getProperty("token.expiration_time"));
		System.out.println("secret  =" + env.getProperty("token.secret"));
		String token = Jwts.builder()
				       .setSubject(userDto.getUserId())
				       .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time"))))
				       .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
				       .compact();
		
		res.addHeader("token", token);
		res.addHeader("userId",  userDto.getUserId());

}
	 
}
