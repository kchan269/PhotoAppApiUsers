package com.appsdeveloperblog.photoapp.api.users.shared;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		switch (response.status()) {
		case 404: {

			String reason = String.format("Method %S", methodKey);
			reason = String.format("Error due to %S", response.reason());
			return new ResponseStatusException(HttpStatus.valueOf(response.status()), reason);

		}

		default:
			return new Exception(response.reason());

		}

	}

}
