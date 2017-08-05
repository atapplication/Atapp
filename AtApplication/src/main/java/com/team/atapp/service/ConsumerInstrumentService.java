package com.team.atapp.service;

import com.team.atapp.dto.UserLoginDTO;
import com.team.atapp.exception.AtAppException;

/**
 * 
 * @author Vikky
 *
 */
public interface ConsumerInstrumentService {
	
	public UserLoginDTO mobileLoginAuth(String userType, String mobilenumber, String password) throws AtAppException;

	public UserLoginDTO getRefreshTokenOnBaseToken()throws AtAppException;
	
}
