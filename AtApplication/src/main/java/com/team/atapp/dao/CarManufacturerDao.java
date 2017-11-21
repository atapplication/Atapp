package com.team.atapp.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.team.atapp.domain.TblCarManufacture;

public interface CarManufacturerDao extends JpaRepository<TblCarManufacture, Serializable> {

	@Query("From TblCarManufacture cm")
	List<TblCarManufacture> getCarManufacturer();

}
