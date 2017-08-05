package com.team.atapp.dto;

import java.io.Serializable;
import java.util.Date;

public class BaseResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String statusCode;
	private String apiToken;
	private String accessToken;
	private String baseToken;
	private Date accessTokenExpDate;
	private Date baseTokenExpDate;

	
	public Date getAccessTokenExpDate() {
		return accessTokenExpDate;
	}

	public void setAccessTokenExpDate(Date accessTokenExpDate) {
		this.accessTokenExpDate = accessTokenExpDate;
	}

	public Date getBaseTokenExpDate() {
		return baseTokenExpDate;
	}

	public void setBaseTokenExpDate(Date baseTokenExpDate) {
		this.baseTokenExpDate = baseTokenExpDate;
	}

	public void setBaseToken(String baseToken) {
		this.baseToken = baseToken;
	}
	public String getBaseToken() {
		return baseToken;
	}
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}
}
