package com.team.atapp.domain;

import java.io.Serializable;
import java.sql.Blob;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the tbl_car_model database table.
 * 
 */
@Entity
@Table(name="tbl_car_model")
@NamedQuery(name="TblCarModel.findAll", query="SELECT t FROM TblCarModel t")
public class TblCarModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	@Column(name="car_model")
	private String carModel;

	@Column(name="car_type")
	private String carType;

	@Lob
	private Blob img;

	//bi-directional many-to-one association to TblCarManufacture
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="car_manufacture_id")
	private TblCarManufacture tblCarManufacture;

	//bi-directional many-to-one association to TblUserCarInfo
	@OneToMany(mappedBy="tblCarModel")
	private List<TblUserCarInfo> tblUserCarInfos;

	public TblCarModel() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCarModel() {
		return this.carModel;
	}

	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	public String getCarType() {
		return this.carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	

	public Blob getImg() {
		return img;
	}

	public void setImg(Blob img) {
		this.img = img;
	}

	public TblCarManufacture getTblCarManufacture() {
		return this.tblCarManufacture;
	}

	public void setTblCarManufacture(TblCarManufacture tblCarManufacture) {
		this.tblCarManufacture = tblCarManufacture;
	}

	public List<TblUserCarInfo> getTblUserCarInfos() {
		return this.tblUserCarInfos;
	}

	public void setTblUserCarInfos(List<TblUserCarInfo> tblUserCarInfos) {
		this.tblUserCarInfos = tblUserCarInfos;
	}

	public TblUserCarInfo addTblUserCarInfo(TblUserCarInfo tblUserCarInfo) {
		getTblUserCarInfos().add(tblUserCarInfo);
		tblUserCarInfo.setTblCarModel(this);

		return tblUserCarInfo;
	}

	public TblUserCarInfo removeTblUserCarInfo(TblUserCarInfo tblUserCarInfo) {
		getTblUserCarInfos().remove(tblUserCarInfo);
		tblUserCarInfo.setTblCarModel(null);

		return tblUserCarInfo;
	}

}