package com.team.atapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the tbl_user_info database table.
 * 
 */
@Entity
@Table(name="tbl_user_info")
@NamedQuery(name="TblUserInfo.findAll", query="SELECT t FROM TblUserInfo t")
public class TblUserInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	private String contactnumber;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createddt;

	private String emailId;

	private String loginotp;

	@Column(name="otp_status")
	private String otpStatus;

	private String password;

	@Column(name="referral_code")
	private String referralCode;

	private String status;

	private String uname;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updateddt;

	private String usertype;

	//bi-directional many-to-one association to TblUserCarInfo
	@OneToMany(mappedBy="tblUserInfo")
	private List<TblUserCarInfo> tblUserCarInfos;

	//bi-directional many-to-one association to Role
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="role")
	private Role roleBean;

	public TblUserInfo() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContactnumber() {
		return this.contactnumber;
	}

	public void setContactnumber(String contactnumber) {
		this.contactnumber = contactnumber;
	}

	public Date getCreateddt() {
		return this.createddt;
	}

	public void setCreateddt(Date createddt) {
		this.createddt = createddt;
	}

	public String getEmailId() {
		return this.emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getLoginotp() {
		return this.loginotp;
	}

	public void setLoginotp(String loginotp) {
		this.loginotp = loginotp;
	}

	public String getOtpStatus() {
		return this.otpStatus;
	}

	public void setOtpStatus(String otpStatus) {
		this.otpStatus = otpStatus;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getReferralCode() {
		return this.referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUname() {
		return this.uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public Date getUpdateddt() {
		return this.updateddt;
	}

	public void setUpdateddt(Date updateddt) {
		this.updateddt = updateddt;
	}

	public String getUsertype() {
		return this.usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public List<TblUserCarInfo> getTblUserCarInfos() {
		return this.tblUserCarInfos;
	}

	public void setTblUserCarInfos(List<TblUserCarInfo> tblUserCarInfos) {
		this.tblUserCarInfos = tblUserCarInfos;
	}

	public TblUserCarInfo addTblUserCarInfo(TblUserCarInfo tblUserCarInfo) {
		getTblUserCarInfos().add(tblUserCarInfo);
		tblUserCarInfo.setTblUserInfo(this);

		return tblUserCarInfo;
	}

	public TblUserCarInfo removeTblUserCarInfo(TblUserCarInfo tblUserCarInfo) {
		getTblUserCarInfos().remove(tblUserCarInfo);
		tblUserCarInfo.setTblUserInfo(null);

		return tblUserCarInfo;
	}

	public Role getRoleBean() {
		return this.roleBean;
	}

	public void setRoleBean(Role roleBean) {
		this.roleBean = roleBean;
	}

}