package com.team.atapp.service.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.team.atapp.constant.AtAppConstants;
import com.team.atapp.dao.AtappKeyConfigDao;
import com.team.atapp.dao.RoleDao;
import com.team.atapp.dao.UserInfoDao;
import com.team.atapp.domain.Role;
import com.team.atapp.domain.TblAtappKeyConfig;
import com.team.atapp.domain.TblUserInfo;
import com.team.atapp.dto.UserLoginDTO;
import com.team.atapp.exception.AtAppException;
import com.team.atapp.logger.AtLogger;
import com.team.atapp.service.ConsumerInstrumentService;
import com.team.atapp.utils.JWTKeyGenerator;

/**
 * 
 * @author Vikky
 *
 */
@Service("consumerInstrumentServiceImpl")
public class ConsumerInstrumentServiceImpl implements ConsumerInstrumentService {

	private final AtLogger logger = AtLogger.getLogger(ConsumerInstrumentServiceImpl.class);
	
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private AtappKeyConfigDao  atappKeyConfigDao;
	
	@Transactional
	public UserLoginDTO mobileLoginAuth(String userType, String mobilenumber, String password) throws AtAppException {
		
		TblUserInfo userInfo = null;
		Role role=null;
		UserLoginDTO  userLoginDto=null;
		try{
			 role=roleDao.getRoleByUserType(userType);
					
		     if(null == role) {
				throw new AtAppException(" role not found in system ", HttpStatus.NOT_FOUND);
			 }
		     
		     userInfo=new TblUserInfo();
		     userInfo.setContactnumber(mobilenumber);
		     userInfo.setPassword(password);
		     userInfo.setRoleBean(role);
		     userInfo.setStatus(AtAppConstants.IND_N);
		     userInfo.setCreateddt(new Date(System.currentTimeMillis()));
		     userInfo.setUpdateddt(new Date(System.currentTimeMillis()));
		     TblUserInfo user=null;
		               user=userInfoDao.save(userInfo);
		      
		     if(null==user){		
		    	 throw new AtAppException(" user not persist in system /mobileLoginAuth ", HttpStatus.EXPECTATION_FAILED);
		     }
		     
		     TblAtappKeyConfig atappKeyConfig = atappKeyConfigDao.getKeyConfigValue(AtAppConstants.KEY_ATAPP_MOBILE);
		
		     
		
			if(null != atappKeyConfig && (atappKeyConfig.getIs_Enabled()!= null && 
					atappKeyConfig.getIs_Enabled().equalsIgnoreCase(AtAppConstants.IND_Y))) {
				
				//long ttlMillis=TimeUnit.DAYS.toMillis(30);
				//long ttlBaseMillis=TimeUnit.DAYS.toMillis(60);
				
				long ttlMillis=TimeUnit.MINUTES.toMillis(10);
				long ttlBaseMillis=TimeUnit.MINUTES.toMillis(30);
				
				logger.debug("ttlMillisVal",ttlMillis);
				logger.debug("ttlBaseMillisVal",ttlBaseMillis);
				
				UserLoginDTO accessToken = JWTKeyGenerator.createJWTAccessToken(atappKeyConfig.getAtapp_key_value(), AtAppConstants.TOKEN_LOGN_ID,
						AtAppConstants.SUBJECT_SECURE, ttlMillis);
				
				UserLoginDTO baseToken = JWTKeyGenerator.createJWTBaseToken(atappKeyConfig.getAtapp_key_value(), AtAppConstants.TOKEN_LOGN_ID,
						AtAppConstants.SUBJECT_SECURE, ttlBaseMillis);
				
				userLoginDto=new UserLoginDTO();
							
				userLoginDto.setAccessToken(accessToken.getApiToken());
				userLoginDto.setAccessTokenExpDate(accessToken.getAccessTokenExpDate());
				
				userLoginDto.setBaseToken(baseToken.getBaseToken());
				userLoginDto.setBaseTokenExpDate(baseToken.getBaseTokenExpDate());
			}
			
		}catch(Exception e){
			logger.error("Exception in service for /mobileLoginAuth",e);
		}
		
		return userLoginDto;
	}

	
	public UserLoginDTO getRefreshTokenOnBaseToken() throws AtAppException {
		TblAtappKeyConfig atappKeyConfig = atappKeyConfigDao.getKeyConfigValue(AtAppConstants.KEY_ATAPP_MOBILE);
		UserLoginDTO userLoginDTO=null;
		
		if(null != atappKeyConfig && (atappKeyConfig.getIs_Enabled() != null && 
				atappKeyConfig.getIs_Enabled().equalsIgnoreCase(AtAppConstants.IND_Y))) {
			
			userLoginDTO=new UserLoginDTO();
			userLoginDTO.setStatusCode(HttpStatus.OK.toString());
				
			//long ttlMillis=TimeUnit.DAYS.toMillis(30);
			//long ttlBaseMillis=TimeUnit.DAYS.toMillis(60);
			long ttlMillis=TimeUnit.MINUTES.toMillis(10);
			long ttlBaseMillis=TimeUnit.DAYS.toMillis(30);		
	
						
			UserLoginDTO newAccessToken = JWTKeyGenerator.createJWTAccessToken(atappKeyConfig.getAtapp_key_value(), AtAppConstants.TOKEN_LOGN_ID,
					AtAppConstants.SUBJECT_SECURE, ttlMillis);
			
			UserLoginDTO newBaseToken = JWTKeyGenerator.createJWTBaseToken(atappKeyConfig.getAtapp_key_value(), AtAppConstants.TOKEN_LOGN_ID,
					AtAppConstants.SUBJECT_SECURE, ttlBaseMillis);
			
			userLoginDTO.setAccessToken(newAccessToken.getApiToken());
			userLoginDTO.setAccessTokenExpDate(newAccessToken.getAccessTokenExpDate());
			
			userLoginDTO.setBaseToken(newBaseToken.getBaseToken());
			userLoginDTO.setBaseTokenExpDate(newBaseToken.getBaseTokenExpDate());
						
		}
		
		return userLoginDTO;
	}


	

	
}