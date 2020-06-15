package com.appsdeveloperblog.photoapp.api.users.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.appsdeveloperblog.photoapp.api.users.ui.model.AlbumResponseModel;

import feign.FeignException;
import feign.hystrix.FallbackFactory;

//@FeignClient(name="albums-ws", fallback=AlbumsFallback.class)   <-- before we use fallbackFactory
@FeignClient(name="albums-ws", fallbackFactory=AlbumsFallbackFactory.class) 
public interface AlbumsServiceClient {
	
	@GetMapping(value="/users/{id}/albums")
	public List<AlbumResponseModel> getAlbums(@PathVariable String id);
	

}

/* the following class is for fallback 
@Component
class AlbumsFallback implements AlbumsServiceClient {
	
	public List<AlbumResponseModel> getAlbums(@PathVariable String id) {
		System.out.println("fall back method run");
		return new ArrayList<>();
	}
}
*/

@Component
class AlbumsFallbackFactory implements FallbackFactory<AlbumsServiceClient> {

	// handle error message, still able to return back a list Albums response 
	// to be able to return AlbumsServiceClient,  we need to create new class that implements this interface 
	@Override
	public AlbumsServiceClient create(Throwable cause) {
		// TODO Auto-generated method stub
		return new AlbumServiceClientFallback(cause);
	}
	
	
class AlbumServiceClientFallback implements AlbumsServiceClient {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	private Throwable cause;
	
	
	public AlbumServiceClientFallback(Throwable cause) {
		this.cause = cause;
		
	}
	public List<AlbumResponseModel> getAlbums(@PathVariable String id) {
		System.out.println("fall back method run");
		
		if (cause instanceof FeignException && ((FeignException) cause).status() == 404 ) {
			logger.error(" 404 error took place when getAlbums was called with userId" + id +
					"Error message : " + 
					cause.getLocalizedMessage());
		}
		else {
			logger.error("Other error took place" + cause.getLocalizedMessage());
		}
		return new ArrayList<>();
	}
}


 
}

 