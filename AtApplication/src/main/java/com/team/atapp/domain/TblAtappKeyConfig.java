package com.team.atapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the tbl_atapp_key_config database table.
 * 
 */
@Entity
@Table(name="tbl_atapp_key_config")
@NamedQuery(name="TblAtappKeyConfig.findAll", query="SELECT t FROM TblAtappKeyConfig t")
public class TblAtappKeyConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	private String atapp_key_name;

	private String atapp_key_value;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createddt;

	private String is_Enabled;

	private String status;

	public TblAtappKeyConfig() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAtapp_key_name() {
		return this.atapp_key_name;
	}

	public void setAtapp_key_name(String atapp_key_name) {
		this.atapp_key_name = atapp_key_name;
	}

	public String getAtapp_key_value() {
		return this.atapp_key_value;
	}

	public void setAtapp_key_value(String atapp_key_value) {
		this.atapp_key_value = atapp_key_value;
	}

	public Date getCreateddt() {
		return this.createddt;
	}

	public void setCreateddt(Date createddt) {
		this.createddt = createddt;
	}

	public String getIs_Enabled() {
		return this.is_Enabled;
	}

	public void setIs_Enabled(String is_Enabled) {
		this.is_Enabled = is_Enabled;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}