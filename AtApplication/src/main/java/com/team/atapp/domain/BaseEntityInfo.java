package com.team.atapp.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;

public class BaseEntityInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_TS")
	private Timestamp createdTs;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Column(name = "UPDATED_TS")
	private Timestamp updatedTs;
	
	@Column(name = "STATUS")
	private String status;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Timestamp createdTs) {
		this.createdTs = createdTs;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedTs() {
		return updatedTs;
	}

	public void setUpdatedTs(Timestamp updatedTs) {
		this.updatedTs = updatedTs;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
