package com.team.atapp.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.team.atapp.constant.AtAppConstants;
import com.team.atapp.domain.TblServiceProvider;
import com.team.atapp.domain.TblUserCarInfo;
import com.team.atapp.domain.TblUserInfo;
import com.team.atapp.dto.StatusDto;
import com.team.atapp.dto.UserLoginDTO;
import com.team.atapp.exception.AtAppException;
import com.team.atapp.logger.AtLogger;
import com.team.atapp.service.AtappCommonService;
import com.team.atapp.service.ConsumerInstrumentService;
import com.team.atapp.service.SMSService;
import com.team.atapp.utils.JWTKeyGenerator;
import com.team.atapp.utils.JsonUtil;

/**
 * 
 * @author Vikky
 *
 */

@RestController
@RequestMapping(AtAppConstants.CONSUMER_API)
public class ConsumerInstrumentController {
	
	@Autowired
	private ConsumerInstrumentService consumerInstrumentServiceImpl;
	
	@Autowired
	private AtappCommonService atAppCommonService;
	
	@Autowired
	private SMSService smsService;
	
	private static final AtLogger logger = AtLogger.getLogger(ConsumerInstrumentController.class);

	@RequestMapping(value = "/mobileLoginAuth", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> userLoginFromApp(@RequestBody String received){
		logger.info("Inside in /mobileLoginAuth ");

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		HttpHeaders httpHeaders =null;
		UserLoginDTO userLoginDTO=null;
		
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /mobileLoginAuth", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
			
			if(obj.get("usertype").toString()!=null && !obj.get("usertype").toString().isEmpty() 
				&& obj.get("mobilenumber").toString()!=null && !obj.get("mobilenumber").toString().isEmpty() 
    				&& obj.get("password").toString()!=null && !obj.get("password").toString().isEmpty() ){
    					
    				logger.debug("usertype for /mobileLoginAuth :",obj.get("usertype").toString());
    				logger.debug("mobilenumber for /mobileLoginAuth :",obj.get("mobilenumber").toString());
    				logger.debug("password for /mobileLoginAuth :",obj.get("password").toString());
			
    				httpHeaders=new HttpHeaders();
			
    				userLoginDTO = consumerInstrumentServiceImpl.mobileLoginAuth(obj.get("usertype").toString(),obj.get("mobilenumber").toString(),obj.get("password").toString());
    				
    				    				
    				httpHeaders.add(AtAppConstants.HTTP_HEADER_TOKEN_NAME, userLoginDTO.getAccessToken());
    				httpHeaders.add(AtAppConstants.HTTP_HEADER_BASE_TOKEN_NAME, userLoginDTO.getBaseToken());
    				
    						
					try {
						/*Epoch format for Access,Base Token Expiration Date*/
						httpHeaders.add("AccessTokenExpiration", String.valueOf(new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
								.parse(userLoginDTO.getAccessTokenExpDate().toString()).getTime()));
						httpHeaders.add("BaseTokenExpiration", String.valueOf(new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
												.parse(userLoginDTO.getBaseTokenExpDate().toString()).getTime()));
					
					}catch(Exception e){
						logger.error("Exception in controller for /mobileLoginAuth",e);
					}
										
					String response = JsonUtil.objToJson(userLoginDTO);
			
					responseEntity = new ResponseEntity<String>(response,httpHeaders, HttpStatus.OK);
			
			}else{
				responseEntity = new ResponseEntity<String>("Any or all in usertype/mobileNo/pwd is null",HttpStatus.EXPECTATION_FAILED);
			}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /mobileLoginAuth");
			userLoginDTO=new UserLoginDTO();
			userLoginDTO.setStatusDesc(ae.getMessage());
			userLoginDTO.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(userLoginDTO);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		return responseEntity;
	}
	
	@RequestMapping(value = "/getRefreshToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getRefreshTokenHandler(@RequestHeader(value = AtAppConstants.HTTP_HEADER_BASE_TOKEN_NAME) String refreshToken){
		logger.info("Inside /getRefreshToken");
		ResponseEntity<String> responseEntity = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		UserLoginDTO userLoginDTO=null;
		try {
			//Validate BASE-TOKEN Value
			JWTKeyGenerator.validateXToken(refreshToken);
			
			// Validate Expriy Date
			atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, refreshToken);
			
			userLoginDTO = consumerInstrumentServiceImpl.getRefreshTokenOnBaseToken();
			String response = JsonUtil.objToJson(userLoginDTO);
			httpHeaders.add(AtAppConstants.HTTP_HEADER_TOKEN_NAME, userLoginDTO.getAccessToken());
			httpHeaders.add(AtAppConstants.HTTP_HEADER_BASE_TOKEN_NAME, userLoginDTO.getBaseToken());
			//httpHeaders.add("APITokenExpiration:", userLoginDTO.getAccessTokenExpDate().toString().trim());
			try{
			httpHeaders.add("BaseTokenExpiration", String.valueOf(new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
				.parse(userLoginDTO.getBaseTokenExpDate().toString()).getTime()));	
			
			httpHeaders.add("AccessTokenExpiration", String.valueOf(new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
			.parse(userLoginDTO.getAccessTokenExpDate().toString()).getTime()));
			}catch(Exception e){
				logger.error("Exception in controller for /getRefreshToken",e);
			}
			responseEntity = new ResponseEntity<String>(response,httpHeaders, HttpStatus.OK);
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /getRefreshToken");
			userLoginDTO=new UserLoginDTO();
			userLoginDTO.setStatusDesc(ae.getMessage());
			userLoginDTO.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(userLoginDTO);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		return responseEntity;
	}
	
	
	@RequestMapping(value = "/loginOTP", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> loginOTPHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /loginOTP ");
		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /loginOTP", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty()){
					
    				logger.debug("userId for /loginOTP :",obj.get("userId").toString());
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){
    					TblUserInfo user=smsService.sendLoginOtpToUser(userInfo);
    					if(user!=null){
    						statusDto.setStatusCode(HttpStatus.OK.toString());
    						statusDto.setStatusDesc("SMS sent successfully");
    						String response = JsonUtil.objToJson(statusDto);
    						responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    					}
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
						statusDto.setStatusDesc("userId not exist in system");
						String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    				
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("userId in /loginOTP null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /loginOTP");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}	
	
	
	@RequestMapping(value = "/loginOTPValidate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> validateOTPHanlder(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /loginOtpValidate ");

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /loginOtpValidate", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("loginOTP").toString()!=null && !obj.get("loginOTP").toString().isEmpty() &&
						obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() ){
					
    				logger.debug("loginOTP for /loginOtpValidate :",obj.get("loginOTP").toString());
    				logger.debug("userId for /loginOtpValidate :",obj.get("userId").toString());
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){
    					userInfo.setOtpStatus("verified");
    					consumerInstrumentServiceImpl.updateUser(userInfo);
    					if(userInfo.getLoginotp().equalsIgnoreCase(obj.get("loginOTP").toString())){
    						statusDto.setStatusCode(HttpStatus.OK.toString());
    						statusDto.setStatusDesc("otp validate successfully");
    						String response = JsonUtil.objToJson(statusDto);
    						responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    					}else{
    						statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
    						statusDto.setStatusDesc("otp not valid");
    						String response = JsonUtil.objToJson(statusDto);
        					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    					}
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
						statusDto.setStatusDesc("userId not exist in system");
						String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
    				}
    				
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("userId/loginOTP in /loginOtpValidate null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /loginOtpValidate");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}	
	
	
	@RequestMapping(value = "/doRegistration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> doRegistrationHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /doRegistration ");

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /doRegistration", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("username").toString()!=null && !obj.get("username").toString().isEmpty() &&
						obj.get("referralCode").toString()!=null &&
							obj.get("emailId").toString()!=null && !obj.get("emailId").toString().isEmpty() &&
						          obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() ){
					
    				logger.debug("username for /doRegistration :",obj.get("username").toString());
    				logger.debug("referralCode for /doRegistration :",obj.get("referralCode").toString());
    				logger.debug("emailId for /doRegistration :",obj.get("emailId").toString());
    				logger.debug("userId for /doRegistration :",obj.get("userId").toString());
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){
    					TblUserInfo user=consumerInstrumentServiceImpl.getUserByEmailId(obj.get("emailId").toString());
    					if(user!=null){
	    						statusDto.setStatusCode(HttpStatus.CONFLICT.toString());
		    					statusDto.setStatusDesc("emailId already exist");
		    					String response = JsonUtil.objToJson(statusDto);
		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.CONFLICT);
	    					
	    				}else{
	    						userInfo.setEmailId(obj.get("emailId").toString());
	    						userInfo.setReferralCode(obj.get("referralCode").toString());
	    						userInfo.setUname(obj.get("username").toString());
	    						userInfo.setUpdateddt(new Date(System.currentTimeMillis()));
	    						TblUserInfo u=consumerInstrumentServiceImpl.updateUser(userInfo);
	    						 if(u!=null){    							
	    	    						statusDto.setStatusCode(HttpStatus.OK.toString());
	    	    						statusDto.setStatusDesc("account registered successfully");
	    	    						String response = JsonUtil.objToJson(statusDto);
	    	    						responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
	    						 }else{
	    						    statusDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	 	    						statusDto.setStatusDesc("user persist fail in system /doRegistration");
	 	    						String response = JsonUtil.objToJson(statusDto);
	 	    						responseEntity = new ResponseEntity<String>(response,HttpStatus.INTERNAL_SERVER_ERROR);
	    						 }
	    					}	
    						
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
						statusDto.setStatusDesc("userId not exist in system");
						String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    				
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("userId/referralCode/emailId/username any or all in /doRegistration null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /doRegistration");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}	
	
	
	@RequestMapping(value = "/carRegistration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> carRegistrationHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /carRegistration ");

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /carRegistration", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("carManufacture").toString()!=null && !obj.get("carManufacture").toString().isEmpty() &&
						obj.get("regNo").toString()!=null && !obj.get("regNo").toString().isEmpty() &&
							obj.get("carName").toString()!=null && !obj.get("carName").toString().isEmpty() &&
									obj.get("carType").toString()!=null && !obj.get("carType").toString().isEmpty() &&
						          obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() ){
					
    				logger.debug("carName for /carRegistration :",obj.get("carName").toString());
    				logger.debug("carManufacture for /carRegistration :",obj.get("carManufacture").toString());
    				logger.debug("regNo for /carRegistration :",obj.get("regNo").toString());
    				logger.debug("carType for /carRegistration :",obj.get("carType").toString());
    				logger.debug("userId for /carRegistration :",obj.get("userId").toString());
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){
    					
    					/*Car Validation*/
    					List<TblUserCarInfo> userCarInfo=userInfo.getTblUserCarInfos();
    					if(userCarInfo!=null && !userCarInfo.isEmpty()){
    						for(TblUserCarInfo userc : userCarInfo){
    							if(userc.getRegNo().equalsIgnoreCase(obj.get("regNo").toString())){
    								statusDto.setStatusCode(HttpStatus.CONFLICT.toString());
    		    					statusDto.setStatusDesc("user already mapped to this regNo");
    		    					String response = JsonUtil.objToJson(statusDto);
    		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.CONFLICT);
    		    					return responseEntity;
    							}
    						}
    					}
    					
    					TblUserCarInfo carReg=consumerInstrumentServiceImpl.getUserCarByRegNo(obj.get("regNo").toString());
    					if(carReg!=null){
    						statusDto.setStatusCode(HttpStatus.CONFLICT.toString());
    						statusDto.setStatusDesc("regNo already exist");
    						String response = JsonUtil.objToJson(statusDto);
        					responseEntity = new ResponseEntity<String>(response,HttpStatus.CONFLICT);
    					}else{
    						TblUserCarInfo newCar=null;
    									newCar= new TblUserCarInfo();
    						  newCar.setCarManufacture(obj.get("carManufacture").toString());
    						  newCar.setCarName(obj.get("carName").toString());
    						  newCar.setRegNo(obj.get("regNo").toString());
    						  newCar.setCarType(obj.get("carType").toString());
    						  newCar.setTblUserInfo(userInfo);
    						  newCar.setServiceStatus("pending");
    						  newCar.setServiceType("not_assigned");
    						  newCar.setCreatedDt(new Date(System.currentTimeMillis()));
    						  newCar.setUpdatedDt(new Date(System.currentTimeMillis()));
    						  TblUserCarInfo addedCar=consumerInstrumentServiceImpl.saveCar(newCar);
    						  if(addedCar!=null){
    							  	statusDto.setStatusCode(HttpStatus.OK.toString());
    								statusDto.setStatusDesc("car registered successfully");
    								String response = JsonUtil.objToJson(statusDto);
    		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    						  }else{
    							  	statusDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
  									statusDto.setStatusDesc("car registered successfully");
  									String response = JsonUtil.objToJson(statusDto);
  									responseEntity = new ResponseEntity<String>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    						  }
    					}
    						
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
						statusDto.setStatusDesc("userId not exist in system");
						String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    				
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("carModal/carType/regNo/userId any or all in /carRegistration null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /carRegistration");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/spInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> spInfoHanlder(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken) throws ParseException{
		logger.info("Inside in /spInfo ");

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /spInfo", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty()){
					
    				logger.debug("spId for /spInfo :",obj.get("userId").toString());
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("userId").toString());
    				
    				if(spInfo!=null){
    					List<TblUserCarInfo> usrCarBySp=spInfo.getTblUserCarInfos();
    						if(usrCarBySp!=null && !usrCarBySp.isEmpty()){
    							JSONArray arr=new JSONArray();
    							JSONObject result=null;
    									result=new JSONObject();
    							for(TblUserCarInfo ucsp :usrCarBySp){
    								logger.debug("In for loop ");
    								JSONObject json=null;
    									json=new JSONObject();
    									json.put("uId",ucsp.getTblUserInfo().getId());
    									json.put("user_name",ucsp.getTblUserInfo().getUname());
    									json.put("reg_no", ucsp.getRegNo());
    									json.put("code", ucsp.getCode());
    									json.put("spId",spInfo.getId());
    									json.put("sp_company",spInfo.getCompany());
    									json.put("service_status", ucsp.getServiceStatus());
    									json.put("service_type",ucsp.getServiceType());
    									json.put("issue",ucsp.getIssue()); 
    									json.put("comment",ucsp.getComment());
    									try{
    										json.put("slot", String.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    											.parse(ucsp.getSlot().toString()).getTime()));
    									}catch(Exception e){
    										logger.error(e);
    									}
    									arr.add(json);
    		    				}
    							logger.debug("/result JSONArray ",arr);
    							result.put("services", arr);
    							result.put("open_status", spInfo.getOpenStatus());
    							result.put("statusCode",HttpStatus.OK.toString());
    					
    	    					String response = JsonUtil.objToJson(result);
    	    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    						}else{
    							statusDto.setStatusCode(HttpStatus.NO_CONTENT.toString());
    	    					statusDto.setStatusDesc("No user car associated to sp");
    	    					String response = JsonUtil.objToJson(statusDto);
    	    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NO_CONTENT);
    						}
    				
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
						statusDto.setStatusDesc("userId not exist in system");
						String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    					
    				}
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("userId is empty or null in /spInfo ");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /spInfo");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}	
	
	@RequestMapping(value = "/serviceStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> serviceStatusHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /serviceStatus ");

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /serviceStatus", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("reg_no").toString()!=null && !obj.get("reg_no").toString().isEmpty() &&
						obj.get("service_status").toString()!=null && !obj.get("service_status").toString().isEmpty() &&
								obj.get("comment").toString()!=null && !obj.get("comment").toString().isEmpty() &&
										obj.get("issue").toString()!=null && !obj.get("issue").toString().isEmpty() 
								){
					
    				logger.debug("reg_no for /serviceStatus :",obj.get("reg_no").toString());
    				logger.debug("comment for /serviceStatus :",obj.get("comment").toString());
    				logger.debug("service_status for /serviceStatus :",obj.get("service_status").toString());
    				logger.debug("issue for /serviceStatus :",obj.get("issue").toString());
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				
    				TblUserCarInfo uCarInfo=consumerInstrumentServiceImpl.getUserCarByRegNo(obj.get("reg_no").toString());
    				if(uCarInfo!=null){
    					uCarInfo.setServiceStatus(obj.get("service_status").toString());
    					uCarInfo.setIssue(obj.get("issue").toString());
    					uCarInfo.setIssue(obj.get("comment").toString());
    					consumerInstrumentServiceImpl.updateUserCarInfo(uCarInfo);
    					statusDto.setStatusCode(HttpStatus.OK.toString());
    					statusDto.setStatusDesc("Service status has been changed succefully");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    				}else{
    					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
    					statusDto.setStatusDesc("incorrect reg_no received");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
    				}
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("reg_no/comment/issue/service_status any or all in /serviceStatus null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /serviceStatus");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getSPInfos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getSPInfosHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /getSPInfos ");

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /serviceStatus", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() &&
					obj.get("lat").toString()!=null && !obj.get("lat").toString().isEmpty() &&
						obj.get("lng").toString()!=null && !obj.get("lng").toString().isEmpty()){
    				logger.debug("userId for /getSPInfos :",obj.get("userId").toString());
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){
    					
						JSONObject res=null;
								res=new JSONObject();
								
								JSONArray arr=null;
										arr=new JSONArray();  
								
								
    					List<TblUserCarInfo> userCarInfo=userInfo.getTblUserCarInfos();
    					if(userCarInfo!=null && !userCarInfo.isEmpty()){
    						
    						for(TblUserCarInfo userCar : userCarInfo){
    							logger.debug("Car INfor=",userCar.getRegNo());
    							 List<TblServiceProvider> spCarInfos=userCar.getTblServiceProviders();
    								if(spCarInfos!=null && !spCarInfos.isEmpty()){  									     									
    									for(TblServiceProvider spCar : spCarInfos){
    										logger.debug("SP INfor=",spCar.getId());
    										JSONObject json=null;
        							 		json=new JSONObject();
    										json.put("carName", userCar.getCarName());
    										json.put("regNo", userCar.getRegNo());
    										json.put("comment", userCar.getComment());
    										json.put("code", userCar.getCode());
    										json.put("issue", userCar.getIssue());
    										json.put("serviceType", userCar.getServiceType());
    										json.put("serviceStatus", userCar.getServiceStatus());
    										//json.put("bookedAt", userCar.getBookedAt());
    										json.put("slot", userCar.getSlot());
    										json.put("company",spCar.getCompany());
    										json.put("contactNo", spCar.getPhoneNumber());
    										json.put("website", spCar.getWebsite());
    										json.put("address",spCar.getAddress());
    										
    										arr.add(json);
    									}
    									
    								}
    							  							
    						}
    						
    						
    					}
    					
    					List<TblServiceProvider> spInfos=consumerInstrumentServiceImpl.getSpDetails();
    					
    					
						JSONObject resp=null;
							resp=new JSONObject();
							
						JSONArray resultant=null;
								resultant=new JSONArray();
    					if(spInfos!=null && !spInfos.isEmpty()){
    						for(TblServiceProvider sp : spInfos){
    							double curLat=0.0;
    							double curLng=0.0;
    							double existLat=0.0;	
    							double existLng=0.0;
    							try{
	    							 curLat=Double.parseDouble(obj.get("lat").toString());
	    							 curLng=Double.parseDouble(obj.get("lng").toString());
	    							 existLat=Double.parseDouble(sp.getLatitude());
	    							 existLng=Double.parseDouble(sp.getLongitude());
    							}catch(Exception e){
    								;
    							}
    							logger.debug("curLat",curLat);
    							logger.debug("curLng",curLng);
    							logger.debug("existLat",existLat);
    							logger.debug("existLng",existLng);
    							
	    						double dist=consumerInstrumentServiceImpl.getDistanceToLatLng(curLat,curLng,existLat,existLng);
	    						logger.debug("Distance measurement=",dist);
	    						if(dist<20.0){
	    							resp.put("company",sp.getCompany());
	    							resp.put("emailId", sp.getEmailId());
	    							resp.put("contactNo", sp.getPhoneNumber());
	    							resp.put("website", sp.getWebsite());
	    							resp.put("address",sp.getAddress());
	    							resp.put("lat", sp.getLatitude());
	    							resp.put("lng", sp.getLongitude());
	    							resp.put("wheel_alignment_service", sp.getWheelAlignmentService());
	    							resp.put("wheel_balancing_service", sp.getWheelBalancingService());
	    							resultant.add(resp);
	    						}
    						}
    						
    						
    					}
    					
    					res.put("userCarSPInfo", arr);
    					res.put("spInfos", resultant);
						String response = JsonUtil.objToJson(res);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    				}
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("userId/lat/long is null or empty in /getSPInfos");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /getSPInfos");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}	
	
	
	@RequestMapping(value = "/getSlot", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getSlotHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /getSlot ");

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /getSlot", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() &&
						obj.get("slotDateTime").toString()!=null && !obj.get("slotDateTime").toString().isEmpty() &&	
								obj.get("spId").toString()!=null && !obj.get("spId").toString().isEmpty()
								){
					
    				logger.debug("userId for /getSlot :",obj.get("userId").toString());
    				logger.debug("slotDateTime for /getSlot :",obj.get("slotDateTime").toString());
    				logger.debug("spId for /getSlot :",obj.get("spId").toString());
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("spId").toString());
    				
    				
    				TblUserCarInfo uCarInfo=consumerInstrumentServiceImpl.getUserCarByRegNo(obj.get("reg_no").toString());
    				if(uCarInfo!=null){
    					uCarInfo.setServiceStatus(obj.get("service_status").toString());
    					uCarInfo.setIssue(obj.get("issue").toString());
    					uCarInfo.setIssue(obj.get("comment").toString());
    					consumerInstrumentServiceImpl.updateUserCarInfo(uCarInfo);
    					statusDto.setStatusCode(HttpStatus.OK.toString());
    					statusDto.setStatusDesc("Service status has been changed succefully");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    				}else{
    					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
    					statusDto.setStatusDesc("incorrect reg_no received");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
    				}
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("reg_no/comment/issue/service_status any or all in /serviceStatus null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /serviceStatus");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}	
	
	
	@RequestMapping(value = "/carBookingService", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> carBookingServiceHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /carBookingService ");

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /carBookingService", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() &&
						obj.get("slotDateTime").toString()!=null && !obj.get("slotDateTime").toString().isEmpty() &&	
							obj.get("serviceType").toString()!=null && !obj.get("serviceType").toString().isEmpty() 						
								){
					
    				logger.debug("userId for /serviceStatus :",obj.get("userId").toString());
    				logger.debug("slotDateTime for /serviceStatus :",obj.get("slotDateTime").toString());
    				logger.debug("spId for /serviceStatus :",obj.get("spId").toString());
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				
    				TblUserCarInfo uCarInfo=consumerInstrumentServiceImpl.getUserCarByRegNo(obj.get("reg_no").toString());
    				if(uCarInfo!=null){
    					uCarInfo.setServiceStatus(obj.get("service_status").toString());
    					uCarInfo.setIssue(obj.get("issue").toString());
    					uCarInfo.setIssue(obj.get("comment").toString());
    					consumerInstrumentServiceImpl.updateUserCarInfo(uCarInfo);
    					statusDto.setStatusCode(HttpStatus.OK.toString());
    					statusDto.setStatusDesc("Service status has been changed succefully");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    				}else{
    					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
    					statusDto.setStatusDesc("incorrect reg_no received");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
    				}
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("reg_no/comment/issue/service_status any or all in /serviceStatus null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /serviceStatus");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}	
	
}
	