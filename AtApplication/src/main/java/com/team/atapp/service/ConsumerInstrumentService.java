package com.team.atapp.service;

import java.util.List;

import com.team.atapp.domain.TblUserInfo;
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

	public List<TblUserInfo> getUserInfosCount()throws AtAppException;

	public List<TblUserInfo> getUserInfos()throws AtAppException;

	public TblUserInfo getUserById(String userId)throws AtAppException;

	public TblUserInfo updateUser(TblUserInfo userInfo)throws AtAppException;

	public TblUserInfo getUserByEmailId(String emailId)throws AtAppException;
	
}
