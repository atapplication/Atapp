package com.team.atapp.dto;

import java.io.Serializable;

public class StatusDto implements Serializable{

	private static final long serialVersionUID = 1L;
	private String statusCode;
	private String statusDesc;
	private String code;
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
}
