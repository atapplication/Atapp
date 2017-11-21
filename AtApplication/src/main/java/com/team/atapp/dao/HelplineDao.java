package com.team.atapp.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.team.atapp.domain.TblHelplineContact;

public interface HelplineDao extends JpaRepository<TblHelplineContact, Serializable> {

	@Query("from TblHelplineContact hc")
	List<TblHelplineContact> getHelplineNo();

}
