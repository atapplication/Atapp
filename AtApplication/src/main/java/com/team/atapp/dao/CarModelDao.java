package com.team.atapp.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.atapp.domain.TblCarModel;

public interface CarModelDao extends JpaRepository<TblCarModel, Serializable> {

	@Query("Select cm From TblCarModel cm where cm.id=:id")
	TblCarModel getCarModelById(@Param("id") String id);

	@Query("Select cm From TblCarModel cm where cm.carModel=:carModel")
	TblCarModel getCarModelByName(@Param("carModel") String carModel);

}
