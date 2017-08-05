package com.team.atapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.team.atapp.constant.AtAppConstants;
import com.team.atapp.dao.AtappKeyConfigDao;
import com.team.atapp.domain.TblAtappKeyConfig;
import com.team.atapp.exception.AtAppException;
import com.team.atapp.service.AtappCommonService;
import com.team.atapp.utils.JWTKeyGenerator;

@Service("mightyCommonServiceImpl")
public class AtappCommonServiceImpl implements AtappCommonService {

	@Autowired
	private AtappKeyConfigDao atappKeyConfigDao;

	@Override
	public TblAtappKeyConfig getKeyConfigByKey(String keyName) {
				return atappKeyConfigDao.getKeyConfigValue(keyName);
	}

	@Override
	public void validateXToken(String servicInvoker, String jwtToken) throws AtAppException {
		TblAtappKeyConfig mightyConfig = getKeyConfigByKey(servicInvoker);
		
		if(mightyConfig == null || mightyConfig.getAtapp_key_value() == null) {
			throw new AtAppException("Invalid Service Invoker Value", HttpStatus.UNAUTHORIZED);
		}
		
		if(mightyConfig.getStatus().equalsIgnoreCase(AtAppConstants.IND_D)) {
			throw new AtAppException("Service Invoker Config is invalid", HttpStatus.NOT_IMPLEMENTED);
		}
		
		JWTKeyGenerator.validateJWTToken(mightyConfig.getAtapp_key_value(), jwtToken);
		
	}

}
