package com.team.atapp.exception;

import org.springframework.http.HttpStatus;

/**
 * 
 * @author Shankara
 *
 */
public class AtAppException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HttpStatus httpStatus;
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public AtAppException(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	public AtAppException(String errorMessage) {
		super(errorMessage);
	}
	
	public AtAppException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}
	
	public AtAppException(Throwable throwable) {
		super(throwable);
	}
	
	public AtAppException(String errorMessage, HttpStatus httpStatus, Throwable throwable) {
		super(errorMessage, throwable);
		this.httpStatus = httpStatus;
	}
	
	public AtAppException(String errorMessage, HttpStatus httpStatus) {
		super(errorMessage);
		this.httpStatus = httpStatus;
	}
}
