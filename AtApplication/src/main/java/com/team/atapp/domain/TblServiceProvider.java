package com.team.atapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the tbl_service_provider database table.
 * 
 */
@Entity
@Table(name="tbl_service_provider")
@NamedQuery(name="TblServiceProvider.findAll", query="SELECT t FROM TblServiceProvider t")
public class TblServiceProvider implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	private String address;

	private String company;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_at")
	private Date createdAt;

	@Column(name="display_name")
	private String displayName;
	
	@Column(name="website")
	private String website;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="effective_at")
	private Date effectiveAt;

	@Column(name="email_id")
	private String emailId;

	@Column(name="gst_num")
	private String gstNum;

	@Lob
	private byte[] image;

	private String latitude;

	private String loginId;

	private String longitude;

	@Column(name="wheel_alignment_service")
	private String wheelAlignmentService;
	
	@Column(name="wheel_balancing_service")
	private String wheelBalancingService;
	
	public String getWheelAlignmentService() {
		return wheelAlignmentService;
	}

	public void setWheelAlignmentService(String wheelAlignmentService) {
		this.wheelAlignmentService = wheelAlignmentService;
	}

	public String getWheelBalancingService() {
		return wheelBalancingService;
	}

	public void setWheelBalancingService(String wheelBalancingService) {
		this.wheelBalancingService = wheelBalancingService;
	}

	@Column(name="open_status")
	private String openStatus;

	private String password;
		

	@Column(name="person_incharge")
	private String personIncharge;

	@Column(name="phone_number")
	private String phoneNumber;

	private String ratings;

	@Column(name="registration_num")
	private String registrationNum;

	
	private String slots;

	@Column(name="sp_status")
	private String spStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_at")
	private Date updatedAt;

	//bi-directional many-to-many association to TblUserCarInfo
	@ManyToMany(mappedBy="tblServiceProviders")
	private List<TblUserCarInfo> tblUserCarInfos;

	public TblServiceProvider() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Date getEffectiveAt() {
		return this.effectiveAt;
	}

	public void setEffectiveAt(Date effectiveAt) {
		this.effectiveAt = effectiveAt;
	}

	public String getEmailId() {
		return this.emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getGstNum() {
		return this.gstNum;
	}

	public void setGstNum(String gstNum) {
		this.gstNum = gstNum;
	}

	public byte[] getImage() {
		return this.image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLoginId() {
		return this.loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getOpenStatus() {
		return this.openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPersonIncharge() {
		return this.personIncharge;
	}

	public void setPersonIncharge(String personIncharge) {
		this.personIncharge = personIncharge;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRatings() {
		return this.ratings;
	}

	public void setRatings(String ratings) {
		this.ratings = ratings;
	}

	public String getRegistrationNum() {
		return this.registrationNum;
	}

	public void setRegistrationNum(String registrationNum) {
		this.registrationNum = registrationNum;
	}

	

	public String getSlots() {
		return this.slots;
	}

	public void setSlots(String slots) {
		this.slots = slots;
	}

	public String getSpStatus() {
		return this.spStatus;
	}

	public void setSpStatus(String spStatus) {
		this.spStatus = spStatus;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<TblUserCarInfo> getTblUserCarInfos() {
		return this.tblUserCarInfos;
	}

	public void setTblUserCarInfos(List<TblUserCarInfo> tblUserCarInfos) {
		this.tblUserCarInfos = tblUserCarInfos;
	}

}