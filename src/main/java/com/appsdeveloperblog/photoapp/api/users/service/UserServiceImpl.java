package com.appsdeveloperblog.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.photoapp.api.users.data.AlbumsServiceClient;
import com.appsdeveloperblog.photoapp.api.users.data.UserEntity;
import com.appsdeveloperblog.photoapp.api.users.data.UsersRepository;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.AlbumResponseModel;

import feign.FeignException;

@Service 
public class UserServiceImpl implements UsersService {

	
	private UsersRepository usersRepository;
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//Since we are using Feign client,  we need to comment out restTemplate
	//private RestTemplate restTemplate;
	
	
	private AlbumsServiceClient albumsServiceClient;
	
	private Environment env;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public UserServiceImpl() {
		
	}
	
	@Autowired
	public UserServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder, 
			//RestTemplate restTemplate,
			AlbumsServiceClient albumsServiceClient,
			Environment env) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		//this.restTemplate = restTemplate;
		this.albumsServiceClient = albumsServiceClient;
		this.env= env;
	}
	
	@Override
	public UserDto createUser(UserDto userDetails) {
		 
		userDetails.setUserId(UUID.randomUUID().toString());
		
		ModelMapper modelMapper = new ModelMapper();
		//source object = userDetails(dto)  to destination object userEntity 
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		usersRepository.save(userEntity);
		UserDto responseDto = modelMapper.map(userEntity, UserDto.class);
		
		
		
		
		return  responseDto;
	}

	// when spring framework is trying to authenticate the user it will look for this method and it will 
	// rely on this method to return the user details that much the user name provided during log in
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = usersRepository.findByEmail(username);
		if (userEntity == null) 
			throw new UsernameNotFoundException(username);
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword() , true, true, true, true, new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		 UserEntity userEntity = usersRepository.findByEmail(email);
		 if (userEntity == null) 
				throw new UsernameNotFoundException(email);
		 ModelMapper modelMapper = new ModelMapper();
		 	
		 UserDto userDto = modelMapper.map(userEntity, UserDto.class);
			
		return userDto;
	}
	
	
	@Override
	public UserDto getUserDetailsByUserId(String userId) {
		 UserEntity userEntity = usersRepository.findByUserId(userId);
		 if (userEntity == null) 
				throw new UsernameNotFoundException(userId);
		 ModelMapper modelMapper = new ModelMapper();
		 	
		 UserDto userDto = modelMapper.map(userEntity, UserDto.class);
	     
		 
		 /*
		 String albumUrl = String.format(env.getProperty("albums.url") , userId);
		 System.out.println("almurl = " +  albumUrl);
		 // restTemplate call :  url,  method, http entity (like http header or request body), response type
		 ResponseEntity<List<AlbumResponseModel>> albumnListResponse = restTemplate.exchange(albumUrl, HttpMethod.GET, null, 
				 new ParameterizedTypeReference<List<AlbumResponseModel>>() {});
		 List<AlbumResponseModel> albumsList = albumnListResponse.getBody();
		 */
		 
		 
		List<AlbumResponseModel> albumsList = null;
		/*  try catch block is usual way to handle exception.  Now we are implementing FeignErrorDecoder
		 *  so we comment out try catch block
		try {
			albumsList = albumsServiceClient.getAlbums(userId);
		} catch (FeignException e) {
			// TODO Auto-generated catch block
			logger.error(e.getLocalizedMessage());
		}
		*/
		logger.info("before calling getAlbums");
		albumsList = albumsServiceClient.getAlbums(userId);
		logger.info("afer calling getAlbums");
		userDto.setAlbums(albumsList);
		 
		 
		 
		 return userDto;
	}
	

}
