package com.team.atapp.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.atapp.domain.TblUserCarInfo;

public interface UserCarInfoDao extends JpaRepository<TblUserCarInfo, Serializable> {

	@Query("Select uc From TblUserCarInfo uc where uc.regNo=:regNo ")
	TblUserCarInfo getUserCarByRegNo(@Param("regNo")String regNo);

}
