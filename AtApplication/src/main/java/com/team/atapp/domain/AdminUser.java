package com.team.atapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the admin_user database table.
 * 
 */
@Entity
@Table(name="admin_user")
@NamedQuery(name="AdminUser.findAll", query="SELECT a FROM AdminUser a")
public class AdminUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	private String active;

	private String address;

	private String contactnumber;

	private String displayname;

	private String emailaddress;

	private String firstname;

	private String gender;

	private String lastname;

	private String loginId;

	private String password;

	@Temporal(TemporalType.TIMESTAMP)
	private Date pwdChangedDate;

	//bi-directional many-to-one association to Role
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="roleId")
	private Role role;

	public AdminUser() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactnumber() {
		return this.contactnumber;
	}

	public void setContactnumber(String contactnumber) {
		this.contactnumber = contactnumber;
	}

	public String getDisplayname() {
		return this.displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getEmailaddress() {
		return this.emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getLoginId() {
		return this.loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getPwdChangedDate() {
		return this.pwdChangedDate;
	}

	public void setPwdChangedDate(Date pwdChangedDate) {
		this.pwdChangedDate = pwdChangedDate;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}