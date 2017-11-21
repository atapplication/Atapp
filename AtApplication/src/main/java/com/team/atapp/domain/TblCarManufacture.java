package com.team.atapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the tbl_car_manufacture database table.
 * 
 */
@Entity
@Table(name="tbl_car_manufacture")
@NamedQuery(name="TblCarManufacture.findAll", query="SELECT t FROM TblCarManufacture t")
public class TblCarManufacture implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	@Column(name="car_manufacture")
	private String carManufacture;

	//bi-directional many-to-one association to TblCarModel
	@OneToMany(mappedBy="tblCarManufacture")
	private List<TblCarModel> tblCarModels;

	public TblCarManufacture() {
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

	public List<TblCarModel> getTblCarModels() {
		return this.tblCarModels;
	}

	public void setTblCarModels(List<TblCarModel> tblCarModels) {
		this.tblCarModels = tblCarModels;
	}

	public TblCarModel addTblCarModel(TblCarModel tblCarModel) {
		getTblCarModels().add(tblCarModel);
		tblCarModel.setTblCarManufacture(this);

		return tblCarModel;
	}

	public TblCarModel removeTblCarModel(TblCarModel tblCarModel) {
		getTblCarModels().remove(tblCarModel);
		tblCarModel.setTblCarManufacture(null);

		return tblCarModel;
	}

}