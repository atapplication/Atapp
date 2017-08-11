package com.team.atapp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    					
    					if(userInfo.getLoginOTP().equalsIgnoreCase(obj.get("loginOTP").toString())){
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
    					if(user.getEmailId().equalsIgnoreCase(obj.get("emailId").toString())){
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
					statusDto.setStatusDesc("userId/referralCode/emailId/username any or all in /loginOtpValidate null");
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
	
	
	
	
}
	