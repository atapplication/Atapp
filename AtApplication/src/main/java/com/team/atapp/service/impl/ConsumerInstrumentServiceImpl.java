package com.team.atapp.service.impl;

import java.util.Date;
import java.util.List;
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
	public UserLoginDTO mobileLoginAuth(String userType, String mobilenumber, String password)  throws AtAppException{
		
		UserLoginDTO  userLoginDto=null;
		   userLoginDto=new UserLoginDTO();
			
		//validating account exist or not
		TblUserInfo existinguser=userInfoDao.getUserByContAndPwd(mobilenumber,password);
		if(existinguser!=null){
			userLoginDto.setStatusDesc("account already exist");
			userLoginDto.setUserId(existinguser.getId());
				
		}else{				
				TblUserInfo userInfo = null;
				
				Role role=null;				
					role=roleDao.getRoleByUserType(userType);
					
				 logger.debug("Roletype as",role);		
			     if(null == role) {
					throw new AtAppException("role not found in system ", HttpStatus.NOT_FOUND);
				 }
			     
			     userInfo=new TblUserInfo();
			     userInfo.setContactnumber(mobilenumber);
			     userInfo.setPassword(password);
			     userInfo.setRoleBean(role);
			     userInfo.setUsertype(role.getName());
			     userInfo.setStatus(AtAppConstants.IND_Y);
			     userInfo.setCreateddt(new Date(System.currentTimeMillis()));
			     userInfo.setUpdateddt(new Date(System.currentTimeMillis()));
			     TblUserInfo user=null;
			               user=userInfoDao.save(userInfo);
			      
			     if(null==user){		
			    	 throw new AtAppException(" user not persist in system /mobileLoginAuth ", HttpStatus.EXPECTATION_FAILED);
			     }
			     
			     userLoginDto.setUserId(user.getId());
				 userLoginDto.setStatusDesc("account creation successful");
			     
			   
		}/*else end here*/
		
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
			
									
			userLoginDto.setAccessToken(accessToken.getApiToken());
			userLoginDto.setAccessTokenExpDate(accessToken.getAccessTokenExpDate());
			
			userLoginDto.setBaseToken(baseToken.getBaseToken());
			userLoginDto.setBaseTokenExpDate(baseToken.getBaseTokenExpDate());
			userLoginDto.setStatusCode(HttpStatus.OK.toString());
		}else{
			throw new AtAppException(" Auth key not enabled in system /mobileLoginAuth ", HttpStatus.NOT_FOUND);
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


	
	public List<TblUserInfo> getUserInfosCount() throws AtAppException {
		return userInfoDao.getUserInfosCount();
	}




	public List<TblUserInfo> getUserInfos() throws AtAppException {
		return userInfoDao.getUserInfos();
	}


	

	
}