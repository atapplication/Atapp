package com.team.atapp.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the tbl_user_car_info database table.
 * 
 */
@Entity
@Table(name="tbl_user_car_info")
@NamedQuery(name="TblUserCarInfo.findAll", query="SELECT t FROM TblUserCarInfo t")
public class TblUserCarInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	@Column(name="car_manufacture")
	private String carManufacture;

	@Column(name="car_name")
	private String carName;

	@Column(name="car_type")
	private String carType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_dt")
	private Date createdDt;

	@Column(name="reg_no")
	private String regNo;
	
	@Column(name="issue")
	private String issue;
	
	@Column(name="comment")
	private String comment;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="booked_at")
	private Date bookedAt;
	
	public Date getBookedAt() {
		return bookedAt;
	}

	public void setBookedAt(Date bookedAt) {
		this.bookedAt = bookedAt;
	}

	public String getBookedServiceAmount() {
		return bookedServiceAmount;
	}

	public void setBookedServiceAmount(String bookedServiceAmount) {
		this.bookedServiceAmount = bookedServiceAmount;
	}

	@Column(name="booked_service_amount")
	private String bookedServiceAmount;
	
	
	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name="service_type")
	private String serviceType;

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@Column(name="service_status")
	private String serviceStatus;
	
	@Column(name="code")
	private String code;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="slot")
	private Date slot;

	

	public Date getSlot() {
		return slot;
	}

	public void setSlot(Date slot) {
		this.slot = slot;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_dt")
	private Date updatedDt;

	//bi-directional many-to-one association to TblUserInfo
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="userId")
	private TblUserInfo tblUserInfo;

	//bi-directional many-to-many association to TblServiceProvider
	@ManyToMany
	@JoinTable(
		name="tbl_user_car_service_provider"
		, joinColumns={
			@JoinColumn(name="user_car")
			}
		, inverseJoinColumns={
			@JoinColumn(name="service_provider")
			}
		)
	private List<TblServiceProvider> tblServiceProviders;

	public TblUserCarInfo() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCarManufacture() {
		return this.carManufacture;
	}

	public void setCarManufacture(String carManufacture) {
		this.carManufacture = carManufacture;
	}

	public String getCarName() {
		return this.carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getCarType() {
		return this.carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public Date getCreatedDt() {
		return this.createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRegNo() {
		return this.regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	

	public String getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public Date getUpdatedDt() {
		return this.updatedDt;
	}

	public void setUpdatedDt(Date updatedDt) {
		this.updatedDt = updatedDt;
	}

	public TblUserInfo getTblUserInfo() {
		return this.tblUserInfo;
	}

	public void setTblUserInfo(TblUserInfo tblUserInfo) {
		this.tblUserInfo = tblUserInfo;
	}

	public List<TblServiceProvider> getTblServiceProviders() {
		return this.tblServiceProviders;
	}

	public void setTblServiceProviders(List<TblServiceProvider> tblServiceProviders) {
		this.tblServiceProviders = tblServiceProviders;
	}

}