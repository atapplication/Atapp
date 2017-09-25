package com.team.atapp.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.atapp.domain.TblServiceProvider;

public interface ServiceProviderDao extends JpaRepository<TblServiceProvider, Serializable> {

	@Query("Select sp From TblServiceProvider sp where sp.phoneNumber=:phoneNumber and sp.password=:password ")
	TblServiceProvider getSpByLoginId(@Param("phoneNumber") String phoneNumber,@Param("password") String password);
	
	@Query("Select sp From TblServiceProvider sp where sp.id=:id")
	TblServiceProvider getSPById(@Param("id") String id);

	@Query("Select sp From TblServiceProvider sp where sp.openStatus='unfreeze'")
	List<TblServiceProvider> getSpDetails();


}
