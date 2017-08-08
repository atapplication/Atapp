package com.team.atapp.service.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.team.atapp.config.GetMessages;
import com.team.atapp.config.SendSMS;
import com.team.atapp.config.SendSMSFactory;
import com.team.atapp.dao.UserInfoDao;
import com.team.atapp.domain.TblUserInfo;
import com.team.atapp.exception.AtAppException;
import com.team.atapp.logger.AtLogger;
import com.team.atapp.service.SMSService;

@Service
public class SMSServiceImpl implements SMSService {
	private static final AtLogger logger = AtLogger.getLogger(SMSServiceImpl.class);

	@Autowired
	private UserInfoDao userInfoDao;
		
	public TblUserInfo sendLoginOtpToUser(TblUserInfo user) throws AtAppException {
				logger.debug("In service /SMSServiceImpl");
				Random random = new Random();
				String otp = String.format("%04d", random.nextInt(10000));
				
				TblUserInfo u=null;
				String message="";
				
				if(otp!=null && !otp.isEmpty()){
					user.setLoginOTP(otp);
					u=userInfoDao.save(user);
				}else{
					throw new AtAppException("otp not created in system ", HttpStatus.EXPECTATION_FAILED);
				}
				
				if(null==u){
					throw new AtAppException("otp not updated in system ", HttpStatus.EXPECTATION_FAILED);
				}
				
				message=GetMessages.getLoginOtpMsg(u.getLoginOTP());
				
				try{
					SendSMS sendSMS = SendSMSFactory.getSMSInstance();
					sendSMS.send(user.getContactnumber(), message);
				}catch(Exception e){
					logger.error("Exception in sendLoginOtpToUser service /",e);
				}
				
				return u;
					
	}
	
	


}

