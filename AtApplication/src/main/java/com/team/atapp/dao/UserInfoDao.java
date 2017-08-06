package com.team.atapp.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.atapp.domain.TblUserInfo;

public interface UserInfoDao extends JpaRepository<TblUserInfo, Serializable> {

	@Query("Select u From TblUserInfo u where u.contactnumber=:contactnumber and u.password=:password and u.status='Y'")
	TblUserInfo getUserByContAndPwd(@Param("contactnumber") String contactnumber,@Param("password") String password);

	@Query("Select count(u.id) From TblUserInfo u")
	List<TblUserInfo> getUserInfosCount();

	@Query("Select u From TblUserInfo u")
	List<TblUserInfo> getUserInfos();

}
