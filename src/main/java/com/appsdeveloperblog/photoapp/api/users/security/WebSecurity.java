package com.appsdeveloperblog.photoapp.api.users.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	private Environment env;
	private UsersService usersService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public WebSecurity(Environment env, UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.env = env;
		this.usersService = usersService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		http.headers().frameOptions().disable();
	//	http.authorizeRequests().antMatchers("/users/**").permitAll();
		http.authorizeRequests().antMatchers("/users/**").hasIpAddress(env.getProperty("gateway.ID"))
		// in additional to the above rules, also include .addFilter()
		.and()
		//register authenticationfilter
		.addFilter(getAuthenticationFilter());
		
	}
	
	// register  authenticationFilter 
	private AuthenticationFilter getAuthenticationFilter() throws Exception
	{
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService, env, authenticationManager());
		// set authenication manager by calling authenticationManger method built by spring security
		// if we did not create constructor in authenticationFilter,  we need to setAuthenticationManager.
	//	authenticationFilter.setAuthenticationManager(authenticationManager());
		
		authenticationFilter.setFilterProcessesUrl(env.getProperty("loginUrlPath"));
		
		return authenticationFilter;
		
		 
	}

	

	// this configure method is to tell spring framework which service is going to used to load user details
	// and which passwordEncoder is going to be used
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
	}
}
