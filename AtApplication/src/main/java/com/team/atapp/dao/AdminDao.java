package com.team.atapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.atapp.domain.AdminUser;

public interface AdminDao extends JpaRepository<AdminUser, Integer> {

}
