package com.team.atapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


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

	public String getValidCount() {
		return validCount;
	}

	public void setValidCount(String validCount) {
		this.validCount = validCount;
	}

	private String active;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_dt")
	private Date createdDt;

	@Column(name="reg_no")
	private String regNo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_dt")
	private Date updatedDt;

	private String validity;
	
	@Column(name="valid_count")
	private String validCount;

	//bi-directional many-to-one association to TblBookedCarInfo
	@OneToMany(mappedBy="tblUserCarInfo")
	private List<TblBookedCarInfo> tblBookedCarInfos;

	//bi-directional many-to-one association to TblCarModel
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="model_Id")
	private TblCarModel tblCarModel;

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

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Date getCreatedDt() {
		return this.createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	public String getRegNo() {
		return this.regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public Date getUpdatedDt() {
		return this.updatedDt;
	}

	public void setUpdatedDt(Date updatedDt) {
		this.updatedDt = updatedDt;
	}

	public String getValidity() {
		return this.validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public List<TblBookedCarInfo> getTblBookedCarInfos() {
		return this.tblBookedCarInfos;
	}

	public void setTblBookedCarInfos(List<TblBookedCarInfo> tblBookedCarInfos) {
		this.tblBookedCarInfos = tblBookedCarInfos;
	}

	public TblBookedCarInfo addTblBookedCarInfo(TblBookedCarInfo tblBookedCarInfo) {
		getTblBookedCarInfos().add(tblBookedCarInfo);
		tblBookedCarInfo.setTblUserCarInfo(this);

		return tblBookedCarInfo;
	}

	public TblBookedCarInfo removeTblBookedCarInfo(TblBookedCarInfo tblBookedCarInfo) {
		getTblBookedCarInfos().remove(tblBookedCarInfo);
		tblBookedCarInfo.setTblUserCarInfo(null);

		return tblBookedCarInfo;
	}

	public TblCarModel getTblCarModel() {
		return this.tblCarModel;
	}

	public void setTblCarModel(TblCarModel tblCarModel) {
		this.tblCarModel = tblCarModel;
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