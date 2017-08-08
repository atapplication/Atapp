package com.team.atapp.service;

import com.team.atapp.domain.TblUserInfo;
import com.team.atapp.exception.AtAppException;

public interface SMSService {

	TblUserInfo sendLoginOtpToUser(TblUserInfo user) throws AtAppException;
	
}
