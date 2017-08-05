package com.team.atapp.service;

import com.team.atapp.domain.TblAtappKeyConfig;
import com.team.atapp.exception.AtAppException;

public interface AtappCommonService {

	public TblAtappKeyConfig getKeyConfigByKey(String keyName);
	
	public void validateXToken(String servicInvoker, String jwtToken) throws AtAppException;
}
