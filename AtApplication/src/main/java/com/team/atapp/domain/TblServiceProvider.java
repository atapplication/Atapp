package com.team.atapp.domain;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


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

	@Column(name="3D")
	private String threeD;

	private String address;

	@Column(name="balancing_alignment_service")
	private String balancingAlignmentService;

	@Column(name="close_time")
	private String closeTime;

	private String company;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_at")
	private Date createdAt;

	@Column(name="display_name")
	private String displayName;

	

	@Column(name="email_id")
	private String emailId;

	@Column(name="gst_num")
	private String gstNum;

	@Lob
	private Blob image;

	private String latitude;

	private String longitude;

	private String manual;

	@Column(name="open_status")
	private String openStatus;

	@Column(name="open_time")
	private String openTime;

	private String password;

	@Column(name="person_incharge")
	private String personIncharge;

	@Column(name="phone_number")
	private String phoneNumber;

	private String ratings;

	@Column(name="registration_num")
	private String registrationNum;

	private String slots;



	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="temp_freeze_end")
	private Date tempFreezeEnd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="temp_freeze_start")
	private Date tempFreezeStart;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_at")
	private Date updatedAt;

	private String website;

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

	

	public String getThreeD() {
		return threeD;
	}

	public void setThreeD(String threeD) {
		this.threeD = threeD;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBalancingAlignmentService() {
		return this.balancingAlignmentService;
	}

	public void setBalancingAlignmentService(String balancingAlignmentService) {
		this.balancingAlignmentService = balancingAlignmentService;
	}

	public String getCloseTime() {
		return this.closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
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


	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getManual() {
		return this.manual;
	}

	public void setManual(String manual) {
		this.manual = manual;
	}

	public String getOpenStatus() {
		return this.openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}

	public String getOpenTime() {
		return this.openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
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

	

	public Date getTempFreezeEnd() {
		return this.tempFreezeEnd;
	}

	public void setTempFreezeEnd(Date tempFreezeEnd) {
		this.tempFreezeEnd = tempFreezeEnd;
	}

	public Date getTempFreezeStart() {
		return this.tempFreezeStart;
	}

	public void setTempFreezeStart(Date tempFreezeStart) {
		this.tempFreezeStart = tempFreezeStart;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public List<TblUserCarInfo> getTblUserCarInfos() {
		return this.tblUserCarInfos;
	}

	public void setTblUserCarInfos(List<TblUserCarInfo> tblUserCarInfos) {
		this.tblUserCarInfos = tblUserCarInfos;
	}

}