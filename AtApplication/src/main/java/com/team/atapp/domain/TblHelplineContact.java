package com.team.atapp.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tbl_helpline_contact database table.
 * 
 */
@Entity
@Table(name="tbl_helpline_contact")
@NamedQuery(name="TblHelplineContact.findAll", query="SELECT t FROM TblHelplineContact t")
public class TblHelplineContact implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	@Column(name="contact_no")
	private String contactNo;

	private String type;

	public TblHelplineContact() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContactNo() {
		return this.contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}