package com.appsdeveloperblog.photoapp.api.users.data;

import org.springframework.data.repository.CrudRepository;


public interface UsersRepository extends CrudRepository<UserEntity, Long> {
	
	
	// naming conversion for method name : 
	// anything related to operation delete - methodname must start with delete...
	// anything related to operation update - methodname must start with update...
	// anything related to operation select - methodname must start with findby and field name
	public UserEntity findByEmail(String email);
	
	public UserEntity findByUserId(String userId);

}
