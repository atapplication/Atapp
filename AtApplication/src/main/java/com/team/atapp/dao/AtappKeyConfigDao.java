package com.team.atapp.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.atapp.domain.TblAtappKeyConfig;

public interface AtappKeyConfigDao extends JpaRepository<TblAtappKeyConfig, Serializable> {
	
	@Query("Select key From TblAtappKeyConfig key where key.atapp_key_name=:keyMightyMobile")
	TblAtappKeyConfig getKeyConfigValue(@Param("keyMightyMobile") String keyMightyMobile);

}
