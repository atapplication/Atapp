package com.team.atapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the tbl_booked_car_info database table.
 * 
 */
@Entity
@Table(name="tbl_booked_car_info")
@NamedQuery(name="TblBookedCarInfo.findAll", query="SELECT t FROM TblBookedCarInfo t")
public class TblBookedCarInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="booked_at")
	private Date bookedAt;

	@Column(name="booked_service_amount")
	private String bookedServiceAmount;

	private String code;

	private String comment;

	private String issue;

	@Column(name="service_status")
	private String serviceStatus;

	@Column(name="service_type")
	private String serviceType;

	@Temporal(TemporalType.TIMESTAMP)
	private Date slot;

	private String spId;

	//bi-directional many-to-one association to TblUserCarInfo
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="car_id")
	private TblUserCarInfo tblUserCarInfo;

	public TblBookedCarInfo() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getBookedAt() {
		return this.bookedAt;
	}

	public void setBookedAt(Date bookedAt) {
		this.bookedAt = bookedAt;
	}

	public String getBookedServiceAmount() {
		return this.bookedServiceAmount;
	}

	public void setBookedServiceAmount(String bookedServiceAmount) {
		this.bookedServiceAmount = bookedServiceAmount;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getIssue() {
		return this.issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getServiceStatus() {
		return this.serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public String getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Date getSlot() {
		return this.slot;
	}

	public void setSlot(Date slot) {
		this.slot = slot;
	}

	public String getSpId() {
		return this.spId;
	}

	public void setSpId(String spId) {
		this.spId = spId;
	}

	public TblUserCarInfo getTblUserCarInfo() {
		return this.tblUserCarInfo;
	}

	public void setTblUserCarInfo(TblUserCarInfo tblUserCarInfo) {
		this.tblUserCarInfo = tblUserCarInfo;
	}

}