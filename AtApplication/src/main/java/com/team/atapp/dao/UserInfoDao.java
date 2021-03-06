package com.team.atapp.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.atapp.domain.TblUserInfo;

public interface UserInfoDao extends JpaRepository<TblUserInfo, Serializable> {

	@Query("Select u From TblUserInfo u where u.contactnumber=:contactnumber and u.status='Y'")
	TblUserInfo getUserByContAndPwd(@Param("contactnumber") String contactnumber);

	@Query("Select count(u.id) From TblUserInfo u")
	List<TblUserInfo> getUserInfosCount();

	@Query("Select u From TblUserInfo u")
	List<TblUserInfo> getUserInfos();

	@Query("Select u From TblUserInfo u where u.id=:userId and u.status='Y'")
	TblUserInfo getUserById(@Param("userId") String userId);

	@Query("Select u From TblUserInfo u where u.emailId=:emailId and u.usertype=:usertype and u.status='Y'")
	TblUserInfo getUserByEmailId(@Param("emailId") String emailId,@Param("usertype") String usertype);

	@Query("Select u From TblUserInfo u where u.emailId=:emailId and u.status='Y'")
	TblUserInfo getUserEmailId(@Param("emailId") String emailId);

}
