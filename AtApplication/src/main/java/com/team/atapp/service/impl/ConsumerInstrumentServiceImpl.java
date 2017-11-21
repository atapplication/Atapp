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
import com.team.atapp.dao.BookedCarInfo;
import com.team.atapp.dao.CarManufacturerDao;
import com.team.atapp.dao.CarModelDao;
import com.team.atapp.dao.HelplineDao;
import com.team.atapp.dao.RoleDao;
import com.team.atapp.dao.ServiceProviderDao;
import com.team.atapp.dao.UserCarInfoDao;
import com.team.atapp.dao.UserInfoDao;
import com.team.atapp.domain.Role;
import com.team.atapp.domain.TblAtappKeyConfig;
import com.team.atapp.domain.TblBookedCarInfo;
import com.team.atapp.domain.TblCarManufacture;
import com.team.atapp.domain.TblCarModel;
import com.team.atapp.domain.TblHelplineContact;
import com.team.atapp.domain.TblServiceProvider;
import com.team.atapp.domain.TblUserCarInfo;
import com.team.atapp.domain.TblUserInfo;
import com.team.atapp.dto.UserLoginDTO;
import com.team.atapp.exception.AtAppException;
import com.team.atapp.logger.AtLogger;
import com.team.atapp.service.ConsumerInstrumentService;
import com.team.atapp.service.SMSService;
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
	
	@Autowired
	private UserCarInfoDao  userCarInfoDao;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private ServiceProviderDao serviceProviderDao;
	
	@Autowired
	private CarManufacturerDao carManufactureDao;
	
	@Autowired
	private CarModelDao carModelDao;
	
	@Autowired
	private HelplineDao helplineDao;
	
	
	@Autowired
	private BookedCarInfo bookedCarInfo;
	
	@Transactional
	public UserLoginDTO mobileLoginAuth(String userType, String mobilenumber, String password)  throws AtAppException{
		
		UserLoginDTO  userLoginDto=null;
		   userLoginDto=new UserLoginDTO();
	if(userType.equalsIgnoreCase("usr")){
			
		//validating account exist or not
		TblUserInfo existinguser=userInfoDao.getUserByContAndPwd(mobilenumber);
		if(existinguser!=null){
			if(existinguser.getPassword().equals(password)){
				if(existinguser.getOtpStatus()==null ){
					//existinguser.setPassword(password);
					//userInfoDao.save(existinguser);
					userLoginDto.setStatusDesc("otp not verified");
				}else if(existinguser.getEmailId()==null ){
					userLoginDto.setStatusDesc("user not registered");	
				}else if(existinguser.getTblUserCarInfos()==null || existinguser.getTblUserCarInfos().isEmpty()){
					userLoginDto.setStatusDesc("car not registered");	
				}else{
					userLoginDto.setStatusDesc("account already exist");
				}
				
				userLoginDto.setUserId(existinguser.getId());
				
				/*if(existinguser.getOtpStatus()==null ){
					userLoginDto.setStatusDesc("otp not verified");
				}else if(existinguser.getEmailId()==null ){
					userLoginDto.setStatusDesc("user not registered");	
				}else if(existinguser.getTblUserCarInfos()==null || existinguser.getTblUserCarInfos().isEmpty()){
					userLoginDto.setStatusDesc("car not registered");	
				}else{
					userLoginDto.setStatusDesc("account already exist");
				}
				
				userLoginDto.setUserId(existinguser.getId());*/
			}else{
				throw new AtAppException("InValid Username or password",HttpStatus.NOT_ACCEPTABLE);
			}
				
		}else{				
				TblUserInfo userInfo = null;
				
				Role role=null;				
					role=roleDao.getRoleByUserType(userType);
					
				 logger.debug("Roletype as",role);		
			     if(null == role) {
					throw new AtAppException("role not found in system ", HttpStatus.NOT_FOUND);
				 }
			     
		     if(userType.equalsIgnoreCase("usr")){
			     userInfo=new TblUserInfo();
			     userInfo.setContactnumber(mobilenumber);
			     userInfo.setPassword(password);
			     userInfo.setRoleBean(role);
			     userInfo.setUsertype(role.getType());
			     userInfo.setStatus(AtAppConstants.IND_Y);
			     userInfo.setCreateddt(new Date(System.currentTimeMillis()));
			     userInfo.setUpdateddt(new Date(System.currentTimeMillis()));
			     TblUserInfo user=null;
			               user=userInfoDao.save(userInfo);
			      
			     if(null==user){		
			    	 throw new AtAppException(" user not persist in system /mobileLoginAuth ", HttpStatus.EXPECTATION_FAILED);
			     }
			     //smsService.sendLoginOtpToUser(user);
			     userLoginDto.setUserId(user.getId());
				 userLoginDto.setStatusDesc("account creation successful");
		     }
			   
		}/*else end here*/
	}else if(userType.equalsIgnoreCase("sp")){
		TblServiceProvider sp=serviceProviderDao.getSpByLoginId(mobilenumber,password);
		if(sp!=null){
			userLoginDto.setStatusDesc("SP login authentication successful");
			userLoginDto.setUserId(sp.getId());
		}else{
			throw new AtAppException("InValid Username or password",HttpStatus.NOT_ACCEPTABLE);
		}
	}else{
		throw new AtAppException("Incorrect usertype",HttpStatus.NOT_ACCEPTABLE);
	}
	
	
	
		TblAtappKeyConfig atappKeyConfig = atappKeyConfigDao.getKeyConfigValue(AtAppConstants.KEY_ATAPP_MOBILE);
		
		if(null != atappKeyConfig && (atappKeyConfig.getIs_Enabled()!= null && 
				atappKeyConfig.getIs_Enabled().equalsIgnoreCase(AtAppConstants.IND_Y))) {
			
			long ttlMillis=TimeUnit.DAYS.toMillis(30);
			long ttlBaseMillis=TimeUnit.DAYS.toMillis(60);
			
			//long ttlMillis=TimeUnit.MINUTES.toMillis(10);
			//long ttlBaseMillis=TimeUnit.MINUTES.toMillis(30);
			
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
		UserLoginDTO userLoginDTO=null;
		try{
		
			TblAtappKeyConfig atappKeyConfig = atappKeyConfigDao.getKeyConfigValue(AtAppConstants.KEY_ATAPP_MOBILE);
		
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
		}catch(Exception e){
			throw new AtAppException("Getting problem while renewing the token",HttpStatus.EXPECTATION_FAILED);
		}
		return userLoginDTO;
	}


	
	public List<TblUserInfo> getUserInfosCount() throws AtAppException {
		return userInfoDao.getUserInfosCount();
	}




	public List<TblUserInfo> getUserInfos() throws AtAppException {
		return userInfoDao.getUserInfos();
	}


	public TblUserInfo getUserById(String userId) throws AtAppException {
		return userInfoDao.getUserById(userId);
	}




	public TblUserInfo updateUser(TblUserInfo userInfo) throws AtAppException {
		return userInfoDao.save(userInfo);
	}




	public TblUserInfo getUserByEmailId(String emailId,String usertype) throws AtAppException {
		return userInfoDao.getUserByEmailId(emailId,usertype);
	}



	public TblUserCarInfo getUserCarByRegNo(String regNo) throws AtAppException {
		return userCarInfoDao.getUserCarByRegNo(regNo);
	}




	public TblServiceProvider getSPById(String id) throws AtAppException {
		return serviceProviderDao.getSPById(id);
	}





	public void updateUserCarInfo(TblUserCarInfo uCarInfo) throws AtAppException {
		userCarInfoDao.save(uCarInfo);
		
	}




	public TblUserCarInfo saveCar(TblUserCarInfo newCar) throws AtAppException {
		return userCarInfoDao.save(newCar);
	}



	public List<TblServiceProvider> getSpDetails() throws AtAppException {
		
		return serviceProviderDao.getSpDetails();
	}




	
	public double getDistanceToLatLng(double curLat, double curLng, double existLat, double existLng) throws AtAppException {
		
	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(existLat - curLat);
	    double lonDistance = Math.toRadians(existLng - curLng);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(curLat)) * Math.cos(Math.toRadians(existLat))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c ; // convert to KM
  
		return distance;
	}




	
	public void updateSP(TblServiceProvider spInfo) throws AtAppException {
		serviceProviderDao.save(spInfo);
		
	}




	public List<TblCarManufacture> getCarManufacturer() throws AtAppException {
		return carManufactureDao.getCarManufacturer();
	}




	public TblCarModel getCarModelById(String id) throws AtAppException {
		return carModelDao.getCarModelById(id);
	}



	
	public void saveImageDB(TblCarModel carModel) {
		carModelDao.save(carModel);
		
	}




	public TblCarModel getCarModelByName(String carName) throws AtAppException {
		return carModelDao.getCarModelByName(carName);
	}




	
	public void deleteCarInfoByUser(TblUserCarInfo uCar) throws AtAppException {
		userCarInfoDao.delete(uCar);		
	}


	public List<TblHelplineContact> getHelplineNo() throws AtAppException {
		return helplineDao.getHelplineNo();
	}


	public TblUserInfo setGeneratedPwd(TblUserInfo userInfo) throws AtAppException {
		return userInfoDao.save(userInfo);
	}




	public String getPasswordResetMessage(TblUserInfo userInfo) throws AtAppException {
		return "Hi "
				+userInfo.getUname().toLowerCase()	
				+",<br/><br/>I heard that you forgot your Wheelcare account password. Fear not, we're here to help."
				+"<br/><br/>I've generated a new password for you below. It has some crazy characters, so I recommend that you change your password after you log back into the Wheelcare mobile app. To change your password, navigate to the 'My Profile' tab and click the Change Password link.<br/><br/> "
				+"LoginId - "+userInfo.getContactnumber()
				+"<br/>Password - "+userInfo.getPassword()
				+"<br/><br/>Much love,"
				+"<br/>The WheelCare Robot"
				+"<br/><br/><em>I'm a robot and my owners won't let me receive inbound messages. If you have any questions, please send my owners an email at wheelccare7@gmail.com.</em>"; 
				
		
	}
	
	
	public String getPasswordResetMessageForSp(TblServiceProvider spinfo) throws AtAppException {
		return "Hi "
				+spinfo.getDisplayName().toLowerCase()	
				+",<br/><br/>I heard that you forgot your Wheelcare account password. Fear not, we're here to help."
				+"<br/><br/>I've generated a new password for you below. It has some crazy characters, so I recommend that you change your password after you log back into the Wheelcare mobile app. To change your password, navigate to the 'My Profile' tab and click the Change Password link.<br/><br/> "
				+"LoginId - "+spinfo.getPhoneNumber()
				+"<br/>Password - "+spinfo.getPassword()
				+"<br/><br/>Much love,"
				+"<br/>The WheelCare Robot"
				+"<br/><br/><em>I'm a robot and my owners won't let me receive inbound messages. If you have any questions, please send my owners an email at wheelccare7@gmail.com.</em>"; 
				
		
	}

	public List<TblUserCarInfo> getUsersCarByRegNo(String regNo) throws AtAppException {
		
		return userCarInfoDao.getUsersCarByRegNo(regNo);
	}




	public TblBookedCarInfo updateBookedCarInfo(TblBookedCarInfo bookedCar) throws AtAppException {
		return bookedCarInfo.save(bookedCar);
	}




	public TblUserInfo getUserEmailId(String emailId) throws AtAppException {
		// TODO Auto-generated method stub
		return userInfoDao.getUserEmailId(emailId);
	}




	
	public TblServiceProvider getSpByEmailId(String emailId) throws AtAppException {
		// TODO Auto-generated method stub
		return serviceProviderDao.getSpByEmailId(emailId);
	}





	public TblServiceProvider setGeneratedPwdForSP(TblServiceProvider spInfo) throws AtAppException {
		return serviceProviderDao.save(spInfo);
	}




	
	public List<TblBookedCarInfo> getBookedCarByspId(String spId,String slot) throws AtAppException {
		return bookedCarInfo.getBookedCarByspId(spId,slot);
	}





	public String getCarBookedServiceMessage(TblUserInfo userInfo, TblBookedCarInfo bookedCarUpdated) throws AtAppException {
		return "Heyo "
				+userInfo.getUname().toLowerCase()	
				+",<br/><br/>I heard that you just booked a wheelcare car service."
				+"<br/><br/>Your car booking details are as:"
				+"<br/><br/>"
				+"<br/><br/> Car Reg. No : "+bookedCarUpdated.getTblUserCarInfo().getRegNo()
				+"<br/><br/> Manufacturer : "+bookedCarUpdated.getTblUserCarInfo().getTblCarModel().getTblCarManufacture().getCarManufacture()
				+"<br/><br/> Model : "+bookedCarUpdated.getTblUserCarInfo().getTblCarModel().getCarModel()
				+"<br/><br/> Service type : "+bookedCarUpdated.getServiceType()
				+"<br/><br/>"
				+"<br/><br/>If you have any questions about anything related to wheelcare services, please contact us at our helpline from the mobile application. You can also email the wheelcare team at wheelccare7@gmail.com"
				+"<br/><br/>"
				+"<br/><br/>Much love,"
				+"<br/>The Wheelcare Robot"
				+"<br/><br/><em>I'm a robot and my owners won't let me receive inbound messages. If you have any questions, please send my owners an email at wheelccare7@gmail.com.</em>"; 
				
	}


	public String getCarCancellationServiceMessage(TblUserInfo userInfo, TblBookedCarInfo bCarInfo)	throws AtAppException {
		return "Heyo "
				+userInfo.getUname().toLowerCase()	
				+",<br/><br/>Your car service has been cancelled."
				+"<br/><br/>Your car cancelled details are as:"
				+"<br/><br/>"
				+"<br/><br/> Car Reg. No : "+bCarInfo.getTblUserCarInfo().getRegNo()
				+"<br/><br/> Manufacturer : "+bCarInfo.getTblUserCarInfo().getTblCarModel().getTblCarManufacture().getCarManufacture()
				+"<br/><br/> Model : "+bCarInfo.getTblUserCarInfo().getTblCarModel().getCarModel()
				+"<br/><br/> Service type : "+bCarInfo.getServiceType()
				+"<br/><br/>"
				+"<br/><br/>If you have any questions about anything related to wheelcare services, please contact us at our helpline from the mobile application. You can also email the wheelcare team at wheelccare7@gmail.com"
				+"<br/><br/>"
				+"<br/><br/>Much love,"
				+"<br/>The Wheelcare Robot"
				+"<br/><br/><em>I'm a robot and my owners won't let me receive inbound messages. If you have any questions, please send my owners an email at wheelccare7@gmail.com.</em>"; 
				
	}



	public String getCarCompletionServiceMessage(TblUserInfo tblUserInfo, TblBookedCarInfo bookedCar)throws AtAppException {
		return "Heyo "
				+tblUserInfo.getUname().toLowerCase()	
				+",<br/><br/>Your car service has been Completed."
				+"<br/><br/>Your car completed details are as:"
				+"<br/><br/>"
				+"<br/><br/> Car Reg. No : "+bookedCar.getTblUserCarInfo().getRegNo()
				+"<br/><br/> Manufacturer : "+bookedCar.getTblUserCarInfo().getTblCarModel().getTblCarManufacture().getCarManufacture()
				+"<br/><br/> Model : "+bookedCar.getTblUserCarInfo().getTblCarModel().getCarModel()
				+"<br/><br/> Service type : "+bookedCar.getServiceType()
				+"<br/><br/>"
				+"<br/><br/>If you have any questions about anything related to wheelcare services, please contact us at our helpline from the mobile application. You can also email the wheelcare team at wheelccare7@gmail.com"
				+"<br/><br/>"
				+"<br/><br/>Much love,"
				+"<br/>The Wheelcare Robot"
				+"<br/><br/><em>I'm a robot and my owners won't let me receive inbound messages. If you have any questions, please send my owners an email at wheelccare7@gmail.com.</em>"; 
				
	}




	public String getReferMessage(TblUserInfo userInfo,String referralCode) throws AtAppException {
		return "Hi "
				+",<br/><br/>You have been referred by your friend <b>"+userInfo.getUname()+"</b> to join wheelcare."
				+"<br/><br/>"
				+"<br/><br/>Your referral code is : "+referralCode
				+"<br/><br/>If you have any questions about anything related to wheelcare services, please contact us at our helpline from the mobile application. You can also email the wheelcare team at wheelccare7@gmail.com"
				+"<br/><br/>"
				+"<br/><br/>Much love,"
				+"<br/>The Wheelcare Robot"
				+"<br/><br/><em>I'm a robot and my owners won't let me receive inbound messages. If you have any questions, please send my owners an email at wheelccare7@gmail.com.</em>"; 
				
	}




	
	public String getSpReferMessage(TblServiceProvider spInfo, String referralCode) throws AtAppException {
		return "Hi "
				+",<br/><br/>You have been referred by your friend <b>"+spInfo.getDisplayName()+"</b> to join wheelcare."
				+"<br/><br/>"
				+"<br/><br/>Your referral code is : "+referralCode
				+"<br/><br/>If you have any questions about anything related to wheelcare services, please contact us at our helpline from the mobile application. You can also email the wheelcare team at wheelccare7@gmail.com"
				+"<br/><br/>"
				+"<br/><br/>Much love,"
				+"<br/>The Wheelcare Robot"
				+"<br/><br/><em>I'm a robot and my owners won't let me receive inbound messages. If you have any questions, please send my owners an email at wheelccare7@gmail.com.</em>"; 
				
	}












	


	

	
}