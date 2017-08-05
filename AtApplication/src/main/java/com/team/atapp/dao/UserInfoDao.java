package com.team.atapp.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.atapp.domain.TblUserInfo;

public interface UserInfoDao extends JpaRepository<TblUserInfo, Serializable> {

}
