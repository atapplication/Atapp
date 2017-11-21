package com.team.atapp.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.atapp.domain.TblBookedCarInfo;

public interface BookedCarInfo extends JpaRepository<TblBookedCarInfo, Serializable> {

	@Query(value="select * from tbl_booked_car_info bc where bc.spId=?1 and DATE(slot)=?2 and bc.service_status!='cancelled' and bc.service_status!='done'",nativeQuery = true)
	List<TblBookedCarInfo> getBookedCarByspId(@Param("spId") String spId, @Param("slot") String slot);


}
