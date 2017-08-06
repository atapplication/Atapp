package com.team.atapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.team.atapp.domain.TblUserInfo;
import com.team.atapp.logger.AtLogger;
import com.team.atapp.service.ConsumerInstrumentService;

@Controller
public class UserInfoController {
	
	private static final AtLogger logger = AtLogger.getLogger(LoginController.class);
	
	@Autowired
	private ConsumerInstrumentService consumerInstrumentServiceImpl;
	
	@RequestMapping(value= {"/userInfoHistory"}, method=RequestMethod.GET)
    public String userInfoHistoryHandler(Map<String,Object> map) {
			List<TblUserInfo> userInfos=consumerInstrumentServiceImpl.getUserInfos();
				map.put("userInfos", userInfos);
					return "userInfo";
		 
	 }

}
