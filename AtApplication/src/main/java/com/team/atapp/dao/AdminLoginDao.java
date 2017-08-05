package com.team.atapp.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.atapp.domain.AdminUser;

public interface AdminLoginDao extends JpaRepository<AdminUser, Serializable> {
	
	@Query("Select admin From AdminUser admin where admin.loginId=:loginId and admin.password=:password")
	public AdminUser getUserLogin(@Param("loginId") String loginId,@Param("password") String password)throws Exception;
}
