package com.team.atapp.controller;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

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

import com.paytm.pg.merchant.CheckSumServiceHelper;
import com.team.atapp.constant.AtAppConstants;
import com.team.atapp.constant.PasswordGenerator;
import com.team.atapp.domain.TblBookedCarInfo;
import com.team.atapp.domain.TblCarManufacture;
import com.team.atapp.domain.TblCarModel;
import com.team.atapp.domain.TblHelplineContact;
import com.team.atapp.domain.TblServiceProvider;
import com.team.atapp.domain.TblUserCarInfo;
import com.team.atapp.domain.TblUserInfo;
import com.team.atapp.dto.StatusDto;
import com.team.atapp.dto.UserLoginDTO;
import com.team.atapp.exception.AtAppException;
import com.team.atapp.logger.AtLogger;
import com.team.atapp.notification.SendMail;
import com.team.atapp.service.AtappCommonService;
import com.team.atapp.service.ConsumerInstrumentService;
import com.team.atapp.service.SMSService;
import com.team.atapp.utils.DateUtil;
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
		logger.info("In /mobileLoginAuth ");
		logger.info("/mobileLoginAuth body ",received);

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
		logger.info("/loginOTP body ",received);
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
		logger.info("/loginOtpValidate body",received);

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
						obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty()){
					
    				logger.debug("loginOTP for /loginOtpValidate :",obj.get("loginOTP").toString());
    				logger.debug("userId for /loginOtpValidate :",obj.get("userId").toString());
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){
    					userInfo.setOtpStatus(AtAppConstants.VERIFIED);
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
		}catch(AtAppException ae){
			logger.debug("IN contoller catch block /loginOtpValidate");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/checkSum", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkSumHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /checkSum ");
		logger.info("/checkSum body",received);

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /checkSum", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				
    				if(obj.get("ORDER_ID").toString()!=null && !obj.get("ORDER_ID").toString().isEmpty() &&
    						obj.get("CUST_ID").toString()!=null && !obj.get("CUST_ID").toString().isEmpty() &&
    							obj.get("TXN_AMOUNT").toString()!=null && !obj.get("TXN_AMOUNT").toString().isEmpty()){
    					
        				logger.debug("ORDER_ID for /checkSum :",obj.get("ORDER_ID").toString());
        				logger.debug("CUST_ID for /checkSum :",obj.get("CUST_ID").toString());
        				logger.debug("TXN_AMOUNT for /checkSum :",obj.get("TXN_AMOUNT").toString());
        				
    				
    				 String MID = "Wheelc40923916143942"; 
    				 String MercahntKey = "m7XeF8cBlTF9XMnp";
    				 String INDUSTRY_TYPE_ID = "Retail";
    				 String CHANNLE_ID = "WAP";
    				 String WEBSITE = "wheelcare";
    				 String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
    				 
    				 
    				 TreeMap<String,String> paramMap = new TreeMap<String,String>();
    					paramMap.put("MID" , MID);
    					paramMap.put("ORDER_ID" , "WCOrder100008548");
    					paramMap.put("CUST_ID" , "WCUser31");
    					paramMap.put("INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
    					paramMap.put("CHANNEL_ID" , CHANNLE_ID);
    					paramMap.put("TXN_AMOUNT" ,  "1.00");
    					paramMap.put("WEBSITE" , WEBSITE);
    					paramMap.put("CALLBACK_URL" , CALLBACK_URL);
    					
    					String checkSum="";
    					try{
    						
    						
    						checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MercahntKey, paramMap);
    						
    					}catch(Exception e) {
    						e.printStackTrace();
    					}
    					
    					logger.debug("checkSum Val :",checkSum);
    					
    					if(!checkSum.equals("")){
    						JSONObject json=null;
    								json= new JSONObject();
    									json.put("checkSum", checkSum);    						
    						String response = JsonUtil.objToJson(json);
    						responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    					}
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
						statusDto.setStatusDesc("ORDER_ID/CUST_ID/TXN_AMOUNT is null in the system");
						String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    					
    				
			
		}catch(AtAppException ae){
			logger.debug("IN contoller catch block /checkSum");
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
		logger.info("/doRegistration body",received);
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
    					TblUserInfo user=consumerInstrumentServiceImpl.getUserEmailId(obj.get("emailId").toString());
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/carInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> carInfoDetails(@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /carInfo ");
		
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
				
		try {			
				
    			
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken); 
    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				List<TblCarManufacture> carManufacturer=consumerInstrumentServiceImpl.getCarManufacturer();
    				
    				if(carManufacturer!=null && !carManufacturer.isEmpty()){
    					JSONArray arr=new JSONArray();
						JSONObject result=null;
								result=new JSONObject();
								
    					for(TblCarManufacture cm : carManufacturer){
    						List<TblCarModel> carModals=cm.getTblCarModels();
    							if(carModals!=null && !carModals.isEmpty()){
    								JSONArray jsubarr=null;
										jsubarr=new JSONArray();
									JSONObject json=null;
										json=new JSONObject();	
									
									json.put("manufacturer_name", cm.getCarManufacture());										
    								for(TblCarModel c : carModals){
    									JSONObject jsub=null;
    											jsub=new JSONObject();
    											
    											jsub.put("model_id", c.getId());
    											jsub.put("car_model", c.getCarModel());
    											jsubarr.add(jsub);
    											  											
    											 																
    								}
    								
    								json.put("models", jsubarr);
    								arr.add(json);
    							}
    					}
    					
    					result.put("carInfos",arr);
    					String response = JsonUtil.objToJson(result);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
					
    					
    				}else{
    				
						statusDto.setStatusCode(HttpStatus.NO_CONTENT.toString());
    					statusDto.setStatusDesc("No car infos found");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NO_CONTENT);
					}
    					
    				
    				
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /carInfo");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/carImg", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> carImgHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken) throws SQLException{
		logger.info("Inside in /carImg ");
		logger.info("/carImg body",received);
		
		ResponseEntity<String> responseEntity = null;
		JSONObject obj=null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
			
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /carImg", HttpStatus.BAD_REQUEST);
		}
		
				
				
				
				
		try {			
			if(obj.get("model_id").toString()!=null && !obj.get("model_id").toString().isEmpty()){
    			
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken); 
    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblCarModel carModel=consumerInstrumentServiceImpl.getCarModelById(obj.get("model_id").toString());
    				
    				
    				if(carModel!=null){
    					JSONObject result=null;
								result=new JSONObject();
						if(carModel.getImg()!=null){
							//result.put("img",Base64.encodeBase64(carModel.getImg().getBytes(1, (int) carModel.getImg().length())));
							result.put("img", Base64.getEncoder().encodeToString(carModel.getImg().getBytes(1, (int) carModel.getImg().length())));
						}else{
							result.put("img","");
						}
    					
    					String response = JsonUtil.objToJson(result);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
					
    					
    				}else{
    				
						statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
    					statusDto.setStatusDesc("model_id is empty/null");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
					}
			}else{
				statusDto.setStatusCode(HttpStatus.NO_CONTENT.toString());
				statusDto.setStatusDesc("No car infos found");
				String response = JsonUtil.objToJson(statusDto);
				responseEntity = new ResponseEntity<String>(response,HttpStatus.NO_CONTENT);
			}
    				
    				
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /carImg");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/myCar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> myCarHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken) throws SQLException{
		logger.info("Inside in /myCar ");
		logger.info("/myCar body",received);
		
		ResponseEntity<String> responseEntity = null;
		JSONObject obj=null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
			
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /myCar", HttpStatus.BAD_REQUEST);
		}
		
				
				
				
				
		try {			
			if(obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty()){
    			
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken); 
    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userCarInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				
    				if(userCarInfo!=null){
    					logger.debug("GEt Inside");
    					List<TblUserCarInfo> userCars=userCarInfo.getTblUserCarInfos();
    					if(userCars!=null && !userCars.isEmpty()){
    						logger.debug("logger size",userCars.size());
    						JSONArray arr=new JSONArray();
    						JSONObject json=null;
    							json=new JSONObject();
    						
    						for(TblUserCarInfo uCar: userCars){
    						 if(uCar.getActive().equalsIgnoreCase(AtAppConstants.ACTIVE)){
    							 TblCarModel carModel=uCar.getTblCarModel();
    							 if(carModel!=null){
    								 logger.debug("1111111");
    								 JSONObject result=null;
 									result=new JSONObject();
 									result.put("reg_no", uCar.getRegNo());
 									result.put("veh_type", carModel.getCarType());
 									result.put("img", Base64.getEncoder().encodeToString(carModel.getImg().getBytes(1, (int) carModel.getImg().length())));
 									result.put("model_id", carModel.getId());
 									result.put("car_model", carModel.getCarModel());
 									result.put("manufacturer", carModel.getTblCarManufacture().getCarManufacture());
 									try{
 										if(uCar.getValidity()==null){
 											result.put("validity",0);
 										}else{
 											result.put("validity",Long.parseLong(uCar.getValidity()));
 										}
 										
 										if(uCar.getValidCount()==null){
 											result.put("valid_count",0);
 										}else{
 											result.put("valid_count",Long.parseLong(uCar.getValidCount()));
 										}
 								    }catch(Exception e){
 								    	e.printStackTrace();
 								    }
    							 
    							 List<TblServiceProvider> spCarInfos=uCar.getTblServiceProviders();
    								if(spCarInfos!=null && !spCarInfos.isEmpty()){  									     									
    									for(TblServiceProvider spCar : spCarInfos){							
    								 									
    									List<TblBookedCarInfo> bookedCars=uCar.getTblBookedCarInfos();
											if(bookedCars!=null && !bookedCars.isEmpty()){
												for(TblBookedCarInfo bookedCar: bookedCars){
													if(!bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.DONE) && !bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.CANCELLED)){
														result.put("code", bookedCar.getCode());
														result.put("serviceType", bookedCar.getServiceType());
														result.put("serviceStatus", bookedCar.getServiceStatus());
														result.put("companyName", spCar.getCompany());
														result.put("address", spCar.getAddress());
														result.put("contactNumber", spCar.getPhoneNumber());
														result.put("website", spCar.getWebsite());
			    										try{
			    											result.put("slot", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			        											.parse(bookedCar.getSlot().toString()).getTime()));
			        									}catch(Exception e){
			        										logger.error(e);
			        									}
													}else{
														result.put("serviceStatus", AtAppConstants.PENDING);
													}
												}
											}    								
    								     }
	    							}else{
	    								result.put("serviceStatus", AtAppConstants.PENDING);
									}
    								
    								arr.add(result);							
    						    }		
    						  }
    						}
    						json.put("myCars", arr);
    						String response = JsonUtil.objToJson(json);
        					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    						
    					}
    				  					
    				}else{
    					statusDto.setStatusCode(HttpStatus.BAD_REQUEST.toString());
    					statusDto.setStatusDesc("userId not found");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.BAD_REQUEST);
    				}
			}else{
				statusDto.setStatusCode(HttpStatus.NO_CONTENT.toString());
				statusDto.setStatusDesc("userId empty or null");
				String response = JsonUtil.objToJson(statusDto);
				responseEntity = new ResponseEntity<String>(response,HttpStatus.NO_CONTENT);
			}
    				
    				
		}catch(AtAppException ae){
			logger.debug("IN contoller catch block /myCar");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	@RequestMapping(value = "/rmCar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> removeCarHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken) throws SQLException{
		logger.info("Inside in /rmCar ");
		logger.info("/rmCar body",received);
		
		ResponseEntity<String> responseEntity = null;
		JSONObject obj=null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
			
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /rmCar", HttpStatus.BAD_REQUEST);
		}
		
				
				
				
				
		try {			
			if(obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() &&
					obj.get("reg_no").toString()!=null && !obj.get("reg_no").toString().isEmpty()){
    			
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken); 
    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				
    				if(userInfo!=null){
    					
    					List<TblUserCarInfo> userCars=userInfo.getTblUserCarInfos();
    					if(userCars!=null && !userCars.isEmpty()){
    						for(TblUserCarInfo uCar: userCars){
    							if(uCar.getRegNo().equalsIgnoreCase(obj.get("reg_no").toString())){
    								if(uCar.getValidity().equals("0")){
    								List<TblBookedCarInfo> bookedCarInfos=uCar.getTblBookedCarInfos();
	    								if(bookedCarInfos!=null && !bookedCarInfos.isEmpty()){
	    									for(TblBookedCarInfo bookedCar : bookedCarInfos){
	    										if(bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.DONE) || bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.CANCELLED)){
	    											uCar.setActive(AtAppConstants.DEACTIVE);
	    											consumerInstrumentServiceImpl.updateUserCarInfo(uCar);
	    											responseEntity = new ResponseEntity<String>(HttpStatus.OK);	 
	    										}else{
	    											responseEntity = new ResponseEntity<String>(HttpStatus.CONFLICT);
	    											return responseEntity;
	    										}
	    									}
	    									
	    									
	    										   									
	    								}else{
	    									consumerInstrumentServiceImpl.deleteCarInfoByUser(uCar);
	    										responseEntity = new ResponseEntity<String>(HttpStatus.OK);
	    								}
    								}else{
    									responseEntity = new ResponseEntity<String>(HttpStatus.METHOD_NOT_ALLOWED);
    								}
    							}
    						}	
    					}	
    							
    				  							
    				}else{
    					statusDto.setStatusCode(HttpStatus.BAD_REQUEST.toString());
    					statusDto.setStatusDesc("userId not found");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.BAD_REQUEST);
    				}
			}else{
				statusDto.setStatusCode(HttpStatus.NO_CONTENT.toString());
				statusDto.setStatusDesc("userId/reg_no empty or null");
				String response = JsonUtil.objToJson(statusDto);
				responseEntity = new ResponseEntity<String>(response,HttpStatus.NO_CONTENT);
			}
    				
    				
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /rmCar");
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
		logger.info("/carRegistration body",received);
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
				if(	obj.get("regNo").toString()!=null && !obj.get("regNo").toString().isEmpty() &&
							obj.get("model_id").toString()!=null && !obj.get("model_id").toString().isEmpty() &&
									obj.get("carType").toString()!=null && !obj.get("carType").toString().isEmpty() &&
											obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() ){
					
    				
    				logger.debug("regNo for /carRegistration :",obj.get("regNo").toString());
    				logger.debug("model_id for /carRegistration :",obj.get("model_id").toString());
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
    							logger.debug("I'm in IF");
    						for(TblUserCarInfo userc : userCarInfo){
    							if(userc.getRegNo().equalsIgnoreCase(obj.get("regNo").toString())){
    								if(userc.getActive().equals(AtAppConstants.ACTIVE)){    							
	    								statusDto.setStatusCode(HttpStatus.CONFLICT.toString());
	    		    					statusDto.setStatusDesc("user already mapped to this regNo");
	    		    					String response = JsonUtil.objToJson(statusDto);
	    		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.CONFLICT);
	    		    					return responseEntity;
	    		    					
    								}else if(userc.getActive().equals(AtAppConstants.DEACTIVE)){
    									userc.setActive(AtAppConstants.ACTIVE);
    									userc.setUpdatedDt(new Date(System.currentTimeMillis()));
    									consumerInstrumentServiceImpl.updateUserCarInfo(userc);
    									statusDto.setStatusCode(HttpStatus.CONFLICT.toString());
	    		    					statusDto.setStatusDesc("user regNo status updated");
	    		    					String response = JsonUtil.objToJson(statusDto);
	    		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.CONFLICT);
	    		    					return responseEntity;
    								}
    								
    							}
    						}
    					}
    					
    					
    					
    					List<TblUserCarInfo> carExistOrNot=consumerInstrumentServiceImpl.getUsersCarByRegNo(obj.get("regNo").toString());
    						logger.debug("Im else carExistOrNot list size",carExistOrNot.size());
    						if(carExistOrNot!=null && !carExistOrNot.isEmpty()){    						
    							statusDto.setStatusCode(HttpStatus.LOCKED.toString());
								statusDto.setStatusDesc("car already registerd/activated on another account ");
								String response = JsonUtil.objToJson(statusDto);
								responseEntity = new ResponseEntity<String>(response,HttpStatus.LOCKED);
    						}else{
    							TblUserCarInfo newCar=null;
								newCar= new TblUserCarInfo();
							TblCarModel carModel=consumerInstrumentServiceImpl.getCarModelById(obj.get("model_id").toString());			
							  newCar.setRegNo(obj.get("regNo").toString());
							  newCar.setTblUserInfo(userInfo);
							  newCar.setActive(AtAppConstants.ACTIVE);
							  //newCar.setServiceStatus(AtAppConstants.PENDING);
							  newCar.setTblCarModel(carModel);
							  newCar.setCreatedDt(new Date(System.currentTimeMillis()));
							  newCar.setUpdatedDt(new Date(System.currentTimeMillis()));
							  newCar.setValidity("0");
							  newCar.setValidCount("0");
							  TblUserCarInfo addedCar=consumerInstrumentServiceImpl.saveCar(newCar);
							  if(addedCar!=null){
								  	statusDto.setStatusCode(HttpStatus.OK.toString());
									statusDto.setStatusDesc("car registered successfully");
									String response = JsonUtil.objToJson(statusDto);
			    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
							  }else{
								  		statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
										statusDto.setStatusDesc("car registered failed");
										String response = JsonUtil.objToJson(statusDto);
										responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
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
					statusDto.setStatusDesc("model_id/carType/regNo/userId any or all in /carRegistration null");
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
	public ResponseEntity<String> spInfoHanlder(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken) {
		logger.info("Inside in /spInfo ");
		logger.info("/spInfo ",received);

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
    								List<TblBookedCarInfo> bookedCarInfo=ucsp.getTblBookedCarInfos();
	    								if(bookedCarInfo!=null && !bookedCarInfo.isEmpty()){
	    								  for(TblBookedCarInfo bookedCars : bookedCarInfo){
	    									  if(!bookedCars.getServiceStatus().equalsIgnoreCase(AtAppConstants.DONE) &&  !bookedCars.getServiceStatus().equalsIgnoreCase(AtAppConstants.CANCELLED)){
	    										  JSONObject json=null;
		    									json=new JSONObject();
		    									json.put("uId",ucsp.getTblUserInfo().getId());
		    									json.put("user_name",ucsp.getTblUserInfo().getUname());
		    									json.put("reg_no", ucsp.getRegNo());
		    									
		    									json.put("code", bookedCars.getCode());
		    									json.put("spId",spInfo.getId());
		    										    									
		    									json.put("sp_company",spInfo.getCompany());
		    									json.put("service_status", bookedCars.getServiceStatus());
		    									json.put("service_type",bookedCars.getServiceType());
		    									json.put("model_id",bookedCars.getTblUserCarInfo().getTblCarModel().getId());
		    									if(bookedCars.getIssue()==null){
		    										json.put("issue","");
		    									}else{
		    										json.put("issue",bookedCars.getIssue()); 
		    									}
		    									
		    									if(bookedCars.getComment()==null){
		    										json.put("comment","");
		    									}else{
		    										json.put("comment",bookedCars.getComment()); 
		    									}
		    								
		    									
		    									try{
		    										json.put("slot", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		    											.parse(bookedCars.getSlot().toString()).getTime()));
		    													
		    									}catch(Exception e){
		    										logger.error(e);
		    									}
		    									arr.add(json);
	    									  }	
	    								   }	
	    								}
	    								
	    								
    		    				}
    							logger.debug("/result JSONArray ",arr);
    							result.put("services", arr);
    							
    							try{
    								if(spInfo.getTempFreezeStart()==null){
    									result.put("temp_freeze_start","0");
    								}else{
    									result.put("temp_freeze_start", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    											.parse(spInfo.getTempFreezeStart().toString()).getTime()));
    								}
    								
    								if(spInfo.getTempFreezeEnd()==null){
    									result.put("temp_freeze_end","0");
    								}else{
    									result.put("temp_freeze_end", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    											.parse(spInfo.getTempFreezeEnd().toString()).getTime()));
    								}    								
									
    								
								}catch(Exception e){
									e.printStackTrace();
								}
    							result.put("open_status", spInfo.getOpenStatus());
    							
    							try{
    								if(spInfo.getTempFreezeStart()==null && spInfo.getTempFreezeEnd()==null)
    								{
    									result.put("temp_freeze_start","0");
    									result.put("temp_freeze_end","0");
    								}else{
    									
	    								result.put("temp_freeze_start", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.parse(spInfo.getTempFreezeStart().toString()).getTime()));
									
	    								result.put("temp_freeze_end", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.parse(spInfo.getTempFreezeEnd().toString()).getTime()));
    								}
    							}catch(Exception e){
									logger.error(e);
								}
    							
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
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/spHistory", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> spHistoryHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken) {
		logger.info("Inside in /spHistory ");
		logger.info("/spHistory ",received);

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /spHistory", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("spId").toString()!=null && !obj.get("spId").toString().isEmpty()){
					
    				logger.debug("spId for /spHistory :",obj.get("spId").toString());
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("spId").toString());
    				
    				if(spInfo!=null){
    					List<TblUserCarInfo> usrCarBySp=spInfo.getTblUserCarInfos();
    						if(usrCarBySp!=null && !usrCarBySp.isEmpty()){
    							JSONArray arr=new JSONArray();
    							JSONObject result=null;
    									result=new JSONObject();
    							for(TblUserCarInfo ucsp :usrCarBySp){
    								List<TblBookedCarInfo> bookedCarInfo=ucsp.getTblBookedCarInfos();
	    								if(bookedCarInfo!=null && !bookedCarInfo.isEmpty()){
	    								  for(TblBookedCarInfo bookedCars : bookedCarInfo){
	    									  if(bookedCars.getServiceStatus().equalsIgnoreCase(AtAppConstants.CANCELLED) ||  bookedCars.getServiceStatus().equalsIgnoreCase(AtAppConstants.DONE)){
	    										  JSONObject json=null;
		    									json=new JSONObject();
		    									json.put("uId",ucsp.getTblUserInfo().getId());
		    									json.put("user_name",ucsp.getTblUserInfo().getUname());
		    									json.put("reg_no", ucsp.getRegNo());
		    									
		    									json.put("code", bookedCars.getCode());
		    									json.put("spId",spInfo.getId());
		    										    									
		    									json.put("sp_company",spInfo.getCompany());
		    									json.put("service_status", bookedCars.getServiceStatus());
		    									json.put("service_type",bookedCars.getServiceType());
		    									json.put("model_id",bookedCars.getTblUserCarInfo().getTblCarModel().getId());
		    									if(bookedCars.getIssue()==null){
		    										json.put("issue","");
		    									}else{
		    										json.put("issue",bookedCars.getIssue()); 
		    									}
		    									
		    									if(bookedCars.getComment()==null){
		    										json.put("comment","");
		    									}else{
		    										json.put("comment",bookedCars.getComment()); 
		    									}
		    								
		    									
		    									try{
		    										json.put("slot", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		    											.parse(bookedCars.getSlot().toString()).getTime()));
		    													
		    									}catch(Exception e){
		    										logger.error(e);
		    									}
		    									arr.add(json);
	    									  }	
	    								   }	
	    								}
	    								
	    								
    		    				}
    							logger.debug("/result JSONArray ",arr);
    							result.put("services", arr);
    							
    							try{
    								if(spInfo.getTempFreezeStart()==null){
    									result.put("temp_freeze_start","0");
    								}else{
    									result.put("temp_freeze_start", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    											.parse(spInfo.getTempFreezeStart().toString()).getTime()));
    								}
    								
    								if(spInfo.getTempFreezeEnd()==null){
    									result.put("temp_freeze_end","0");
    								}else{
    									result.put("temp_freeze_end", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    											.parse(spInfo.getTempFreezeEnd().toString()).getTime()));
    								}    								
									
    								
								}catch(Exception e){
									e.printStackTrace();
								}
    							result.put("open_status", spInfo.getOpenStatus());
    							
    							try{
    								if(spInfo.getTempFreezeStart()==null && spInfo.getTempFreezeEnd()==null)
    								{
    									result.put("temp_freeze_start","0");
    									result.put("temp_freeze_end","0");
    								}else{
    									
	    								result.put("temp_freeze_start", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.parse(spInfo.getTempFreezeStart().toString()).getTime()));
									
	    								result.put("temp_freeze_end", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.parse(spInfo.getTempFreezeEnd().toString()).getTime()));
    								}
    							}catch(Exception e){
									logger.error(e);
								}
    							
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
	
	
	@RequestMapping(value = "/tempFreeze", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> tempFreezeHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken) {
		logger.info("Inside in /tempFreeze ");
		logger.info("/tempFreeze ",received);

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /tempFreeze", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("spId").toString()!=null && !obj.get("spId").toString().isEmpty() && 
						obj.get("freeze_time_start").toString()!=null && !obj.get("freeze_time_start").toString().isEmpty() && 
							obj.get("freeze_time_end").toString()!=null && !obj.get("freeze_time_end").toString().isEmpty()){
					
    				logger.debug("spId for /tempFreeze :",obj.get("spId").toString());
    				logger.debug("freeze_time_start for /tempFreeze :",obj.get("freeze_time_start").toString());
    				logger.debug("freeze_time_end for /tempFreeze :",obj.get("freeze_time_end").toString());
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("spId").toString());
    				
    				if(spInfo!=null){
    					
    					try{
							/*Date date = new Date(Long.parseLong(obj.get("slotDateTime").toString()));
							  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							  logger.debug( dateFormat.format (date));
							  spInfo.setTempFreezeStart(new Date());
							  uc.setSlot(dateFormat.format(date));*/
							
							spInfo.setTempFreezeStart(DateUtil.convertLongToDate(Long.parseLong(obj.get("freeze_time_start").toString()), "yyyy-MM-dd HH:mm:ss"));
							spInfo.setTempFreezeEnd(DateUtil.convertLongToDate(Long.parseLong(obj.get("freeze_time_end").toString()), "yyyy-MM-dd HH:mm:ss"));
							
						}catch(Exception e){
							e.printStackTrace();
						}
    					
    							consumerInstrumentServiceImpl.updateSP(spInfo);
    	    					responseEntity = new ResponseEntity<String>(HttpStatus.OK);
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
						statusDto.setStatusDesc("sI not exist in system");
						String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    					
    				}
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("sI/freeze_time_start/freeze_time_end  is empty or null in /tempFreeze ");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /tempFreeze");
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
		logger.info("/serviceStatus ",received);
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
								obj.get("comment").toString()!=null &&
										obj.get("issue").toString()!=null  
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
    					List<TblBookedCarInfo> bookedList=uCarInfo.getTblBookedCarInfos();
    					if(bookedList!=null && !bookedList.isEmpty()){
    					 for(TblBookedCarInfo bookedCar : bookedList){
    						 if(!bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.CANCELLED) && !bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.DONE)){ 
	    						if(obj.get("service_status").toString().equalsIgnoreCase(AtAppConstants.DONE)){
	    							uCarInfo.setValidity("0");
	    							consumerInstrumentServiceImpl.updateUserCarInfo(uCarInfo);
	    						}
    							bookedCar.setServiceStatus(obj.get("service_status").toString());
	    						bookedCar.setIssue(obj.get("issue").toString());
	    						bookedCar.setIssue(obj.get("comment").toString());
		    					consumerInstrumentServiceImpl.updateBookedCarInfo(bookedCar);
		    					
		    					logger.debug("/inside done car service Mail");
								 
								String subject = "Completion confirmation !! ("+uCarInfo.getRegNo()+")";
								TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(bookedCar.getSpId());
								String message = consumerInstrumentServiceImpl.getCarCompletionServiceMessage(uCarInfo.getTblUserInfo(),bookedCar);
								
								SendMail mail=com.team.atapp.notification.SendMailFactory.getMailInstance();
								try{
									logger.debug("/inside try block of booking service send Mail");
									
									String[] arr={uCarInfo.getTblUserInfo().getEmailId(),"wheelccare7@gmail.com",spInfo.getEmailId()};
									logger.debug("subject",subject);
									for(String s :arr){
										logger.debug("mailing dest",s);
										mail.send(s, subject, message);
									}
								
								
								
								}catch(Exception ex){
									logger.error("/Mail Booking completed Car Service System Error,",ex);
								}
		    					statusDto.setStatusCode(HttpStatus.OK.toString());
		    					statusDto.setStatusDesc("Service status has been changed succefully");
		    					String response = JsonUtil.objToJson(statusDto);
		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
		    					return responseEntity;
		    					
    						 }	
    					 }
    					}
    				}else{
    					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
    					statusDto.setStatusDesc("incorrect reg_no received");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
    				}
    				
				}else{
					statusDto.setStatusCode(HttpStatus.BAD_REQUEST.toString());
					statusDto.setStatusDesc("reg_no/comment/issue/service_status any or all in /serviceStatus null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.BAD_REQUEST);
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
		logger.info("/getSPInfos body ",received);
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
								
								
    					/*List<TblUserCarInfo> userCarInfo=userInfo.getTblUserCarInfos();
    					if(userCarInfo!=null && !userCarInfo.isEmpty()){    						
    						for(TblUserCarInfo userCar : userCarInfo){    							
    							 List<TblServiceProvider> spCarInfos=userCar.getTblServiceProviders();
    							 TblCarModel cm=userCar.getTblCarModel();
    								if(spCarInfos!=null && !spCarInfos.isEmpty()){  									     									
    									for(TblServiceProvider spCar : spCarInfos){
    										JSONObject json=null;
        							 			json=new JSONObject();        							 			
        							 			    										
    										List<TblBookedCarInfo> bookedCars=userCar.getTblBookedCarInfos();
    										if(bookedCars!=null && !bookedCars.isEmpty()){
    											for(TblBookedCarInfo bookedCar: bookedCars){
    												if(!bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.DONE) && !bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.CANCELLED)){
    													json.put("spId", spCar.getId());
    													json.put("carName", cm.getCarModel());
    		    										json.put("regNo", userCar.getRegNo());
    		    										json.put("carManufacture",cm.getTblCarManufacture().getCarManufacture());
    		    										json.put("carType", cm.getCarType());
    													json.put("code", bookedCar.getCode());
    		    										json.put("serviceType", bookedCar.getServiceType());
    		    										json.put("serviceStatus", bookedCar.getServiceStatus());
    		    										try{
    		        										json.put("slot", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    		        											.parse(bookedCar.getSlot().toString()).getTime()));
    		        									}catch(Exception e){
    		        										logger.error(e);
    		        									}
    												}
    											}
    										}
    										
    										
    										
    										
    										
    										arr.add(json);
    									}
    									
    								}   							  							
    						}
    						
    						
    					}*/
    					
    					List<TblServiceProvider> spInfos=consumerInstrumentServiceImpl.getSpDetails();
    										
							
						JSONArray resultant=null;
								resultant=new JSONArray();
    					if(spInfos!=null && !spInfos.isEmpty()){
    						for(TblServiceProvider sp : spInfos){
    							JSONObject resp=null;
    								resp=new JSONObject();
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
	    						if(dist<70.0){
	    							resp.put("spId",sp.getId());
	    							resp.put("company",sp.getCompany());
	    							resp.put("emailId", sp.getEmailId());
	    							resp.put("contactNo", sp.getPhoneNumber());
	    							resp.put("website", sp.getWebsite());
	    							resp.put("address",sp.getAddress());
	    							resp.put("lat", sp.getLatitude());
	    							resp.put("lng", sp.getLongitude());
	    							resp.put("balancing_alignment_service", sp.getBalancingAlignmentService());
	    							resp.put("3D", sp.getThreeD());
	    							resp.put("Manual", sp.getManual());
	    							resp.put("openTime", sp.getOpenTime());
	    							resp.put("closeTime", sp.getCloseTime());
	    							
	    							
	    							
	    							/*try{
										json.put("slot", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.parse(bookedCar.getSlot().toString()).getTime()));
									}catch(Exception e){
										logger.error(e);
									}*/
	    							
	    							resultant.add(resp);
	    						}
    						}
    						
    						
    					}
    					
    					//res.put("userCarSPInfo", arr);
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
		logger.info("/getSlot body ",received);
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
				if(	obj.get("slotDateTime").toString()!=null && !obj.get("slotDateTime").toString().isEmpty() &&	
								obj.get("spId").toString()!=null && !obj.get("spId").toString().isEmpty()
								){
					
    				logger.debug("slotDateTime for /getSlot :",obj.get("slotDateTime").toString());
    				logger.debug("spId for /getSlot :",obj.get("spId").toString());
    				
    				
    				
    				//validate X-MIGHTY-TOKEN value
    				//JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				//atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("spId").toString());
    				
    				
    				if(spInfo!=null){
	    				Date reqdt=DateUtil.convertLongToDate(Long.parseLong(obj.get("slotDateTime").toString()), "yyyy-MM-dd");
	    				String reqdtstr=DateUtil.changeDateFromat(reqdt);
    				
	    				Date reqdt1=DateUtil.convertLongToDate(Long.parseLong(obj.get("slotDateTime").toString()), "yyyy-MM-dd HH:mm");
    				
    				    				
    				String tmpDtStr="";
    				 if(spInfo.getTempFreezeStart()!=null){
    					tmpDtStr=DateUtil.changeDateFromat1(spInfo.getTempFreezeStart());
    					
    				 }
    				    				
    				Date tmpDt=null;
    				 if(!tmpDtStr.equals("")){
    					 tmpDt=DateUtil.sqlFormatToDate1(tmpDtStr);
    				 }
    				 
    				 int dtVal2=50;
    				 if(tmpDt!=null){
    					 dtVal2=reqdt1.compareTo(tmpDt);
    				 }
    				
    				   					
	    				List<TblBookedCarInfo> bookedCarInfos =consumerInstrumentServiceImpl.getBookedCarByspId(spInfo.getId(),reqdtstr);
	       				 if(bookedCarInfos!=null && !bookedCarInfos.isEmpty()){
	       					
	       					int slots=0;
	       					int p=0; 
							int l=0;
							
								
	       					 
	       					 try{
	       						 slots=Integer.parseInt(spInfo.getSlots());
	       					
	       					 }catch(Exception e){
	       						 e.printStackTrace();
	       					 }
	       					
										 
									 
	       					 for(TblBookedCarInfo bc : bookedCarInfos){
	       						 
	       						       						 
	       						 String str=DateUtil.changeDateFromat(bc.getSlot());
	       						 Date dt=DateUtil.sqlFormatToDate(str);
	       						 
	       						 String s=DateUtil.changeDateFromat(reqdt);
	       						 Date d=DateUtil.sqlFormatToDate(s);
	       						 
	       						 int dtVal=dt.compareTo(d);	
	       						 
	       						 logger.debug("sql dt",dt);
	       						 logger.debug("req dt",d);
	       						
	       						 logger.debug("req dt Date()",new Date((long) obj.get("slotDateTime")));
	       						 
	       						 logger.debug("sql slot",str);
	       						 logger.debug("requested slot",DateUtil.changeDateFromat(reqdt));
	       						 logger.debug("date compare int val",dtVal);
	       						 
	       						 
	       						 
	       						 String str1=DateUtil.changeDateFromat1(bc.getSlot());
	       						 Date dt1=DateUtil.sqlFormatToDate1(str1);
	       						 int dtVal1=dt1.compareTo(reqdt1);
	       						 
	       						 logger.debug("sql slot1:",str1);
	       						 logger.debug("requested slot1:",DateUtil.changeDateFromat1(reqdt1));
	       						 logger.debug("date compare int val 1",dtVal1);
	       						 
	       						 
	       						 if(dtVal==0){
	       							 logger.debug("inside dtVal==0");
	       							 if(spInfo.getOpenStatus().equalsIgnoreCase(AtAppConstants.FREEZE)){
	       								logger.debug("inside FREEZE dtVal==0");
	       								statusDto.setStatusCode(HttpStatus.NOT_ACCEPTABLE.toString());
	    	        					statusDto.setStatusDesc("service provider has been close for today");
	    	        					String response = JsonUtil.objToJson(statusDto);
	    	        					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_ACCEPTABLE);
	    	        					return responseEntity;
	       							 }else if(spInfo.getOpenStatus().equalsIgnoreCase(AtAppConstants.UNFREEZE) && dtVal2==0){
	       								
	       								logger.debug("inside UNFREEZE dtVal=0 and dtVal2==0 ");
   										statusDto.setStatusCode(HttpStatus.LOCKED.toString());
   			        					statusDto.setStatusDesc("slot is temporary freeze occupied");
   			        					String response = JsonUtil.objToJson(statusDto);
   			        					responseEntity = new ResponseEntity<String>(response,HttpStatus.LOCKED);
   			        					return responseEntity;
	       								 
	       								 /* if(tmpDt!=null){
	       									int dtVal2=reqdt1.compareTo(tmpDt);
	       									if(dtVal2==0)
	       									{
	       										
	       									}
	       									 
	       								 }*/
	       								
	       							 }else if(spInfo.getOpenStatus().equalsIgnoreCase(AtAppConstants.UNFREEZE) && dtVal1==0){
		       								logger.debug("inside UNFREEZE dtVal==0 && dtVal1==0");
		       								statusDto.setStatusCode(HttpStatus.LOCKED.toString());
		    	        					statusDto.setStatusDesc("slot is already occupied");
		    	        					String response = JsonUtil.objToJson(statusDto);
		    	        					responseEntity = new ResponseEntity<String>(response,HttpStatus.LOCKED);
		    	        					return responseEntity;
		       						}else if(spInfo.getOpenStatus().equalsIgnoreCase(AtAppConstants.UNFREEZE) && dtVal1!=0){
		       							p++;
		       						}
	       							 
	       						 }
	       						 
	       					 }//loop close here
	       					 
	       					 
	       					logger.debug("p val",p);
	       					logger.debug("l val",l);
	       					
	       					
	       					       					 
	       					if(p>0 && p<slots){
	       						logger.debug("outside loop  p>0 && p<slots");
		       						statusDto.setStatusCode(HttpStatus.OK.toString());
		        					statusDto.setStatusDesc("slot is available");
		        					String response = JsonUtil.objToJson(statusDto);
		        					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
		        					return responseEntity;
		        					
		       				 }else if(p>0 && p>=slots){
		       					logger.debug("outside loop  p>0 && p>slots");
		       					statusDto.setStatusCode(HttpStatus.LOCKED.toString());
	        					statusDto.setStatusDesc("no more slots available");
	        					String response = JsonUtil.objToJson(statusDto);
	        					responseEntity = new ResponseEntity<String>(response,HttpStatus.LOCKED);
	        					return responseEntity;
	        					
		       				 }
	       					 
	       					  
	       					 
	       					 
	       				 }else{
	       					 
	       					 /*No booking found or query result is null*/
	       					 if(spInfo.getOpenStatus().equalsIgnoreCase(AtAppConstants.FREEZE)){
	       						statusDto.setStatusCode(HttpStatus.NOT_ACCEPTABLE.toString());
		    					statusDto.setStatusDesc("booking not found but sp has freeze for today");
		    					String response = JsonUtil.objToJson(statusDto);
		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_ACCEPTABLE);  
	       					 }else if(spInfo.getOpenStatus().equalsIgnoreCase(AtAppConstants.UNFREEZE) ){
	       						 
	       						
   									logger.debug("no booking dtVal2==0 int",dtVal2);
   									
   									
   									if(dtVal2==0)
   									{
   										logger.debug("no booking dtVal2==0");
   										statusDto.setStatusCode(HttpStatus.LOCKED.toString());
   			        					statusDto.setStatusDesc("slot is temporary freeze occupied");
   			        					String response = JsonUtil.objToJson(statusDto);
   			        					responseEntity = new ResponseEntity<String>(response,HttpStatus.LOCKED);
   			        					
   									}else if(dtVal2!=0){
   										logger.debug("no booking UNFREEZE dtVal2!=0");
   										statusDto.setStatusCode(HttpStatus.OK.toString());
   			        					statusDto.setStatusDesc("slot is available");
   			        					String response = JsonUtil.objToJson(statusDto);
   			        					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
   									}
	       						 
	       						
	       					 }
	       				 
	       					return responseEntity;
	       				 }
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
    					statusDto.setStatusDesc("spInfo not found");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("slotDateTime/spId/userId any or all in /getSlot null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /getSlot");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}	
	
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/carBookingService", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> carBookingServiceHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /carBookingService ");
		logger.info(" /carBookingService body ",received);

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
							obj.get("serviceType").toString()!=null && !obj.get("serviceType").toString().isEmpty() &&
								obj.get("spId").toString()!=null && !obj.get("spId").toString().isEmpty() &&
									obj.get("reg_no").toString()!=null && !obj.get("reg_no").toString().isEmpty() &&
										obj.get("service_status").toString()!=null && !obj.get("service_status").toString().isEmpty() &&
											obj.get("booking_service_amount").toString()!=null && !obj.get("booking_service_amount").toString().isEmpty() && 
												obj.get("validity").toString()!=null && !obj.get("validity").toString().isEmpty() &&
													obj.get("valid_count").toString()!=null && !obj.get("valid_count").toString().isEmpty()
								){
					
					
    				logger.debug("userId for /carBookingService :",obj.get("userId").toString());
    				logger.debug("slotDateTime for /carBookingService :",obj.get("slotDateTime").toString());
    				logger.debug("serviceType for /carBookingService :",obj.get("serviceType").toString());
    				logger.debug("spId for /carBookingService :",obj.get("spId").toString());
    				logger.debug("reg_no for /carBookingService :",obj.get("reg_no").toString());
    				logger.debug("service_status for /carBookingService :",obj.get("service_status").toString());
    				logger.debug("booking_service_amount for /carBookingService :",obj.get("booking_service_amount").toString());
    				
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				
    				//TblUserCarInfo uCarInfo=consumerInstrumentServiceImpl.getUserCarByRegNo(obj.get("reg_no").toString());
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("spId").toString());
    				if(userInfo!=null && spInfo!=null){
    					List<TblUserCarInfo> userCarInfo=userInfo.getTblUserCarInfos();
    					if(userCarInfo!=null && !userCarInfo.isEmpty()){
    						List<TblServiceProvider> sp = null;
    						            
    						for(TblUserCarInfo uc:userCarInfo){
    							if(uc.getRegNo().equalsIgnoreCase(obj.get("reg_no").toString()) && uc.getActive().equals(AtAppConstants.ACTIVE)){
    								sp=new ArrayList<TblServiceProvider>();
    								sp.add(spInfo);
    								List<TblBookedCarInfo> bookedCars=uc.getTblBookedCarInfos();
    								if(bookedCars!=null && !bookedCars.isEmpty()){
    									for(TblBookedCarInfo bookedCar:bookedCars){
    										if(!bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.DONE) && !bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.CANCELLED)){
    											statusDto.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED.toString());
    		    		    					statusDto.setStatusDesc("booking already exit or not completed ");
    		    		    					String response = JsonUtil.objToJson(statusDto);
    		    		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.METHOD_NOT_ALLOWED);
    		    		    					return responseEntity;
    										}
    									}
    									
    								}
    								
    									TblBookedCarInfo newBooking=null;
    										newBooking=new TblBookedCarInfo();
    										newBooking.setServiceStatus(obj.get("service_status").toString());
    										newBooking.setServiceType(obj.get("serviceType").toString());
    										newBooking.setBookedServiceAmount(obj.get("booking_service_amount").toString());
    										newBooking.setTblUserCarInfo(uc);
    										newBooking.setSlot(DateUtil.convertLongToDate(Long.parseLong(obj.get("slotDateTime").toString()), "yyyy-MM-dd HH:mm:ss"));
    										newBooking.setBookedAt(new Date(System.currentTimeMillis()));
    										String code = new PasswordGenerator().randomString(6);
    										newBooking.setCode(code);
    										newBooking.setSpId(spInfo.getId());
    										newBooking.setIssue("");
    										newBooking.setComment("");
    										
    								
    								    								
	    							
    								
    								uc.setTblServiceProviders(sp); 
    								uc.setValidity(obj.get("validity").toString());
    								uc.setValidCount(obj.get("valid_count").toString());
    								
    								TblBookedCarInfo bookedCarUpdated=consumerInstrumentServiceImpl.updateBookedCarInfo(newBooking);
    								if(bookedCarUpdated!=null){
    									consumerInstrumentServiceImpl.updateUserCarInfo(uc);
    									
    									logger.debug("/inside booking Mail");
    									
    									String subject = "Booking confirmed !! ("+bookedCarUpdated.getTblUserCarInfo().getRegNo()+")";
    									String message = consumerInstrumentServiceImpl.getCarBookedServiceMessage(userInfo,bookedCarUpdated);
    									
    									SendMail mail=com.team.atapp.notification.SendMailFactory.getMailInstance();
    									try{
    										logger.debug("/inside try block of booking service send Mail");
    										
    										String[] arr={userInfo.getEmailId(),"wheelccare7@gmail.com",spInfo.getEmailId()};
											logger.debug("subject",subject);
											for(String s :arr){
												logger.debug("mailing dest",s);
												mail.send(s, subject, message);
											}
										
    									
    									
    									}catch(Exception ex){
    										logger.error("/Mail Booking Car Service System Error,",ex);
    									}
    									
    									
    									statusDto.setStatusCode(HttpStatus.OK.toString());
        		    					statusDto.setStatusDesc("booking details added successfully");
        		    					statusDto.setCode(bookedCarUpdated.getCode());
        		    					String response = JsonUtil.objToJson(statusDto);
        		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
        		    					return responseEntity;
    								}else{
    									statusDto.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED.toString());
        		    					statusDto.setStatusDesc("booking failed");
        		    					String response = JsonUtil.objToJson(statusDto);
        		    					responseEntity = new ResponseEntity<String>(response,HttpStatus.METHOD_NOT_ALLOWED);
        		    					return responseEntity;
    								}
    								
    																
    								  								
    								
    							}
    						}
    					}
    				}else{
	    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
	    					statusDto.setStatusDesc("userId or spId not found in db");
	    					String response = JsonUtil.objToJson(statusDto);
	    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
	    				}
	    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("userId/spId/slotDateTime/serviceType/reg_no/booking_service_amount/service_status any or all in /serviceStatus null");
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
	@RequestMapping(value = "/spView", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> spViewHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken) throws SQLException{
		logger.info("Inside in /spView ");
		logger.info("/spView body ",received);
		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /spView", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(obj.get("spId").toString()!=null && !obj.get("spId").toString().isEmpty()){
					
    				logger.debug("spId for /serviceStatus :",obj.get("spId").toString());
    				
    				
    				//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("spId").toString());
    				
    				if(spInfo!=null){
    					
    							JSONObject json=null;
    									json=new JSONObject();
    									//json.put("sId",spInfo.getId());
    									json.put("spImage",Base64.getEncoder().encodeToString(spInfo.getImage().getBytes(1, (int) spInfo.getImage().length())));
    									  							   					
    	    					String response = JsonUtil.objToJson(json);
    	    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
						statusDto.setStatusDesc("spId not exist in system");
						String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    					
    				}
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("spId is null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /spView");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	
	@RequestMapping(value = "/setOpenStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> setOpenStatusHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /setOpenStatus ");
		logger.info("/setOpenStatus body ",received);

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
				if(	obj.get("openStatus").toString()!=null && !obj.get("openStatus").toString().isEmpty() &&	
								obj.get("spId").toString()!=null && !obj.get("spId").toString().isEmpty()
								){
					
    				logger.debug("openStatus for /setOpenStatus :",obj.get("openStatus").toString());
    				logger.debug("spId for /setOpenStatus :",obj.get("spId").toString());
    				
    				//validate X-MIGHTY-TOKEN value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("spId").toString());
    				
    				
    				if(spInfo!=null){
    					spInfo.setOpenStatus(obj.get("openStatus").toString());
    					consumerInstrumentServiceImpl.updateSP(spInfo);
    					statusDto.setStatusCode(HttpStatus.OK.toString());
    					statusDto.setStatusDesc("open status updated successfully");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
    					statusDto.setStatusDesc("spInfo not found");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("setOpenStatus/spId any or all in /setOpenStatus null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /setOpenStatus");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getProfileHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /getProfile ");
		logger.info("/getProfile body ",received);

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /getProfile", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(	obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() &&
						obj.get("usertype").toString()!=null && !obj.get("usertype").toString().isEmpty()){
					
    				logger.debug("userId for /userId :",obj.get("userId").toString());
    				logger.debug("usertype for /userId :",obj.get("usertype").toString());
    			    				
    				//validate X-MIGHTY-TOKEN value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userInfo=null;
    				TblServiceProvider spInfo=null;
    				if(obj.get("usertype").toString().equalsIgnoreCase("usr")){
    					userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    					if(userInfo!=null){
        					JSONObject json=null;
        					  		json=new JSONObject();
        					  		json.put("username", userInfo.getUname());
        					  		json.put("emailId", userInfo.getEmailId());
        					  		json.put("contactno",userInfo.getContactnumber());
        					  		String response = JsonUtil.objToJson(json);
        					  		responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
        				}else{
        					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
        					statusDto.setStatusDesc("userId not found");
        					String response = JsonUtil.objToJson(statusDto);
        					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
        				}
    					
    				}else if(obj.get("usertype").toString().equalsIgnoreCase("sp")){
    					spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("userId").toString());
    					
    					if(spInfo!=null){
        					JSONObject json=null;
        					  		json=new JSONObject();
        					  		json.put("username", spInfo.getDisplayName());
        					  		json.put("emailId", spInfo.getEmailId());
        					  		json.put("contactno",spInfo.getPhoneNumber());
        					  		String response = JsonUtil.objToJson(json);
        					  		responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
        				}else{
        					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
        					statusDto.setStatusDesc("spId not found");
        					String response = JsonUtil.objToJson(statusDto);
        					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
        				}
    				}
    				
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("userId in  /getProfile null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /getProfile");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getUserHistory", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getUserHistoryHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /getUserHistory ");
		logger.info("/getUserHistory body ",received);

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /getUserHistory", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(	obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty()){
					
    				logger.debug("userId for /userId :",obj.get("userId").toString());
    			    				
    				//validate X-MIGHTY-TOKEN value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){
    					List<TblUserCarInfo> userCars=userInfo.getTblUserCarInfos();
    					if(userCars!=null && !userCars.isEmpty()){
    						JSONArray arr=new JSONArray();
    						JSONObject result=null;
    							result=new JSONObject();
    						for(TblUserCarInfo uc: userCars){
    						  if(uc.getActive().equalsIgnoreCase(AtAppConstants.ACTIVE)){	
    							JSONObject json=null;
    							List<TblBookedCarInfo> bookedCarInfo=uc.getTblBookedCarInfos();
    							 if(bookedCarInfo!=null && !bookedCarInfo.isEmpty()){
    								 for(TblBookedCarInfo bookedCar : bookedCarInfo){
	    							    if(bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.CANCELLED) || bookedCar.getServiceStatus().equalsIgnoreCase(AtAppConstants.DONE)){
	    					  		    	TblCarModel cm=uc.getTblCarModel();
	    					  		        	if(cm!=null){
	    					  		    		json=new JSONObject();
		    									json.put("service_status", bookedCar.getServiceStatus());
		    									json.put("reg_no", uc.getRegNo());
		    									json.put("code", bookedCar.getCode());
		    									json.put("issue", bookedCar.getIssue());
		    									json.put("booking_service_amount", bookedCar.getBookedServiceAmount());
			    									if(bookedCar.getComment()==null){
			    										json.put("comment","");	
			    									}else{
			    										json.put("comment",bookedCar.getComment());	
			    									}
			    									
			    							    try{
			    										json.put("slot_time", String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			    											.parse(bookedCar.getSlot().toString()).getTime()));
			    								}catch(Exception e){
			    										logger.error(e);
			    								}
			    							    json.put("service_type", bookedCar.getServiceType());
			    							    json.put("model_id", cm.getId());
			    							    json.put("spId", uc.getTblServiceProviders().get(0).getId());
			    							    arr.add(json);
	    					  		    	}	    									
		    									
		    									
    							       }
    						         }   
    					  		    }
    							  }
    						    }	
    						result.put("history", arr);
    						String response = JsonUtil.objToJson(result);
							responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
	    				}
    				}else{
	    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
	    					statusDto.setStatusDesc("userId not found");
	    					String response = JsonUtil.objToJson(statusDto);
	    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
	    			}
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("userId in  /getUserHistory null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /getUserHistory");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	
	
	
	
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> resetPwdHandler(@RequestBody String received){
		logger.info("Inside in /resetPwd ");
		logger.info("/resetPwd body ",received);

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /resetPwd", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(	obj.get("emailId").toString()!=null && !obj.get("emailId").toString().isEmpty() && 
						obj.get("usertype").toString()!=null && !obj.get("usertype").toString().isEmpty()){
					
    				logger.debug("emailId for /resetPwd :",obj.get("emailId").toString());
    	if(obj.get("usertype").toString().equalsIgnoreCase("usr")){		      				
    				String password = new PasswordGenerator().randomString(8);
    				logger.debug("Password generator "+password);
    				String subject = "Your brand new wheelcare password";
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserByEmailId(obj.get("emailId").toString(),obj.get("usertype").toString());
    				
    				if(userInfo!=null){
    					userInfo.setPassword(password);
    					TblUserInfo	user=consumerInstrumentServiceImpl.setGeneratedPwd(userInfo);
    					if(user!=null){
							logger.debug("/inside send Mail");
							String message = consumerInstrumentServiceImpl.getPasswordResetMessage(user);
							
							SendMail mail=com.team.atapp.notification.SendMailFactory.getMailInstance();
							try{
								logger.debug("/inside try/catch send Mail");
							mail.send(user.getEmailId(), subject, message);
							
							}catch(Exception ex){
								logger.error("/Mail System Error,",ex);
							}
    					
    							statusDto.setStatusDesc("password reset successfully");
    							statusDto.setStatusCode(HttpStatus.OK.toString());
    							String response = JsonUtil.objToJson(statusDto);
    					  		responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    					}  		
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
    					statusDto.setStatusDesc("emailId not found");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    	}else{
    		String password = new PasswordGenerator().randomString(8);
			logger.debug("Password generator "+password);
			String subject = "Your brand new wheelcare password";
			
			
			TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSpByEmailId(obj.get("emailId").toString());
			
			if(spInfo!=null){
				spInfo.setPassword(password);
				TblServiceProvider	sp=consumerInstrumentServiceImpl.setGeneratedPwdForSP(spInfo);
				if(sp!=null){
					logger.debug("/inside send Mail");
					String message = consumerInstrumentServiceImpl.getPasswordResetMessageForSp(sp);
					
					SendMail mail=com.team.atapp.notification.SendMailFactory.getMailInstance();
					try{
						logger.debug("/inside try/catch send Mail");
					mail.send(sp.getEmailId(), subject, message);
					
					}catch(Exception ex){
						logger.error("/Mail System Error,",ex);
					}
				
						statusDto.setStatusDesc("password reset successfully");
						statusDto.setStatusCode(HttpStatus.OK.toString());
						String response = JsonUtil.objToJson(statusDto);
				  		responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
				}  		
			}else{
				statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
				statusDto.setStatusDesc("emailId not found");
				String response = JsonUtil.objToJson(statusDto);
				responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
			}
    	}
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("emailId/usertype in  /getProfile null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /getProfile");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	@RequestMapping(value = "/refer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> referHandler(@RequestBody String received){
		logger.info("Inside in /refer ");
		logger.info("/refer body ",received);

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /resetPwd", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(	obj.get("emailId").toString()!=null && !obj.get("emailId").toString().isEmpty() && 
						obj.get("usertype").toString()!=null && !obj.get("usertype").toString().isEmpty() && 
							obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty()){
					
    				logger.debug("emailId for /refer :",obj.get("emailId").toString());
    	if(obj.get("usertype").toString().equalsIgnoreCase("usr")){		      				
    				String referralCode = new PasswordGenerator().randomString(8);
    				logger.debug("referralCode generator "+referralCode);
    				String subject = "Invitation to join wheelcare!!";
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){    					
    					
							logger.debug("/inside send Mail");
							String message = consumerInstrumentServiceImpl.getReferMessage(userInfo,referralCode);
							
							SendMail mail=com.team.atapp.notification.SendMailFactory.getMailInstance();
							try{
								logger.debug("/inside try/catch send Mail");
							mail.send(obj.get("emailId").toString(), subject, message);
							
							}catch(Exception ex){
								logger.error("/Mail System Error,",ex);
							}
    					
    							statusDto.setStatusDesc("password reset successfully");
    							statusDto.setStatusCode(HttpStatus.OK.toString());
    							String response = JsonUtil.objToJson(statusDto);
    					  		responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    					 		
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
    					statusDto.setStatusDesc("emailId not found");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    	}else{
    		String referralCode = new PasswordGenerator().randomString(8);
			logger.debug("referralCode generator "+referralCode);
			String subject = "Invitation to join wheelcare!!";
			
			
			TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(obj.get("userId").toString());
			
			if(spInfo!=null){
								
					logger.debug("/inside send Mail");
					String message = consumerInstrumentServiceImpl.getSpReferMessage(spInfo,referralCode);
					
					SendMail mail=com.team.atapp.notification.SendMailFactory.getMailInstance();
					try{
						logger.debug("/inside try/catch send Mail");
					mail.send(obj.get("emailId").toString(), subject, message);
					
					}catch(Exception ex){
						logger.error("/Mail System Error,",ex);
					}
				
						statusDto.setStatusDesc("referred friend");
						statusDto.setStatusCode(HttpStatus.OK.toString());
						String response = JsonUtil.objToJson(statusDto);
				  		responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
				  		
			}else{
				statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
				statusDto.setStatusDesc("emailId not found");
				String response = JsonUtil.objToJson(statusDto);
				responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
			}
    	}
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("emailId/usertype/userId in  /refer null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /refer");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> changePwdHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /changePwd ");
		logger.info("/changePwd body ",received);

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /changePwd", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(	obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() &&
						obj.get("newpwd").toString()!=null && !obj.get("newpwd").toString().isEmpty() && 
							obj.get("currpwd").toString()!=null && !obj.get("currpwd").toString().isEmpty()){
					
    				logger.debug("userId for /resetPwd :",obj.get("userId").toString());
    				logger.debug("newpwd for /resetPwd :",obj.get("newpwd").toString());
    				logger.debug("currpwd for /resetPwd :",obj.get("currpwd").toString());
    			    				
    				//validate X-MIGHTY-TOKEN value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){
    					
							if(userInfo.getPassword().equalsIgnoreCase(obj.get("currpwd").toString())){
								userInfo.setPassword(obj.get("newpwd").toString());
								userInfo.setUpdateddt(new Date(System.currentTimeMillis()));
								consumerInstrumentServiceImpl.updateUser(userInfo);
								
								statusDto.setStatusDesc("Change password successfully");
    							statusDto.setStatusCode(HttpStatus.OK.toString());
    							String response = JsonUtil.objToJson(statusDto);
    					  		responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
							}else{
								statusDto.setStatusDesc("incorrect password entered");
    							statusDto.setStatusCode(HttpStatus.LOCKED.toString());
    							String response = JsonUtil.objToJson(statusDto);
    					  		responseEntity = new ResponseEntity<String>(response,HttpStatus.LOCKED);
							}
    					
    							
    					  		
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
    					statusDto.setStatusDesc("userId not found");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("emailId/newpwd/currpwd in /changePwd null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /changePwd");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	@RequestMapping(value = "/cancelBookingService", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> cancelBookingServiceHandler(@RequestBody String received,@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken){
		logger.info("Inside in /cancelBookingService ");
		logger.info("/cancelBookingService body ",received);

		JSONObject obj=null;
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
		try{		
				obj=new JSONObject();
				obj=(JSONObject)new JSONParser().parse(received);
		}catch(Exception e){
			return new ResponseEntity<String>("Empty received body /cancelBookingService", HttpStatus.BAD_REQUEST);
		}
		
				
		try {			
				if(	obj.get("userId").toString()!=null && !obj.get("userId").toString().isEmpty() &&
						obj.get("reg_no").toString()!=null && !obj.get("reg_no").toString().isEmpty() && 
							obj.get("service_status").toString()!=null && !obj.get("service_status").toString().isEmpty() && 
								obj.get("validity").toString()!=null && !obj.get("validity").toString().isEmpty() &&
									obj.get("valid_count").toString()!=null && !obj.get("valid_count").toString().isEmpty()) {
					
    				logger.debug("userId for /cancelBookingService :",obj.get("userId").toString());
    				logger.debug("reg_no for /cancelBookingService :",obj.get("reg_no").toString());
    				logger.debug("service_status for /cancelBookingService :",obj.get("service_status").toString());
    			    				
    				//validate X-MIGHTY-TOKEN value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				
    				TblUserInfo userInfo=consumerInstrumentServiceImpl.getUserById(obj.get("userId").toString());
    				
    				if(userInfo!=null){
    					List<TblUserCarInfo> userCars=userInfo.getTblUserCarInfos();
    					if(userCars!=null && !userCars.isEmpty()){
    						
    						for(TblUserCarInfo uc: userCars){
    									
		    							if(uc.getRegNo().equalsIgnoreCase(obj.get("reg_no").toString())){
		    								List<TblBookedCarInfo> bookedCarInfo=uc.getTblBookedCarInfos();
		        							  if(bookedCarInfo!=null && !bookedCarInfo.isEmpty()){
		        								for(TblBookedCarInfo bCarInfo : bookedCarInfo){
		        								  if(bCarInfo.getServiceStatus().equalsIgnoreCase(obj.get("service_status").toString())){
		        									  TblServiceProvider spInfo=consumerInstrumentServiceImpl.getSPById(bCarInfo.getSpId());
		        									  bCarInfo.setServiceStatus(AtAppConstants.CANCELLED);
		        								  	  bCarInfo.setIssue("You have cancelled");
		        								  	  consumerInstrumentServiceImpl.updateBookedCarInfo(bCarInfo);
		        								  	  
		        								  	  uc.setValidity(obj.get("validity").toString());
		        								  	  uc.setValidCount(obj.get("valid_count").toString());
		        								  	  consumerInstrumentServiceImpl.updateUserCarInfo(uc);
		        								  	  
		        								  	logger.debug("/inside Cancel Booking Mail");
		        									
		        									String subject = "Cancellation confirmation !! ("+uc.getRegNo()+")";
		        									String message = consumerInstrumentServiceImpl.getCarCancellationServiceMessage(userInfo,bCarInfo);
		        									
		        									SendMail mail=com.team.atapp.notification.SendMailFactory.getMailInstance();
		        									try{
		        										logger.debug("/inside try block of booking service send Mail");
		        										
		        										String[] arr={userInfo.getEmailId(),"wheelccare7@gmail.com",spInfo.getEmailId()};
		    											logger.debug("subject",subject);
		    											for(String s :arr){
		    												logger.debug("mailing dest",s);
		    												mail.send(s, subject, message);
		    											}
		    										
		        									
		        									
		        									}catch(Exception ex){
		        										logger.error("/Mail Booking Car Service System Error,",ex);
		        									}
		        								  	  responseEntity = new ResponseEntity<String>(HttpStatus.OK);
				    								return responseEntity;
		        								   }	
		        								  }	
		    							        } 
		    						    }	
    							}
    					}
    				   					  		
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
    					statusDto.setStatusDesc("userId not found");
    					String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    				}
    				
				}else{
					statusDto.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
					statusDto.setStatusDesc("emailId/newpwd/currpwd in /changePwd null");
					String response = JsonUtil.objToJson(statusDto);
					responseEntity = new ResponseEntity<String>(response,HttpStatus.EXPECTATION_FAILED);
				}
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /changePwd");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/helpline", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> helplineHanlder(@RequestHeader(value = AtAppConstants.HTTP_HEADER_TOKEN_NAME) String xToken) {
		logger.info("Inside in /helpline");
		ResponseEntity<String> responseEntity = null;
		StatusDto statusDto=null;
			statusDto=new StatusDto();
						
		try {			
									
    	    		//Validate X-MIGHTY-TOKEN Value
    				JWTKeyGenerator.validateXToken(xToken);    				
    				
    				// Validate Expriy Date
    				atAppCommonService.validateXToken(AtAppConstants.KEY_ATAPP_MOBILE, xToken);
    				
    				List<TblHelplineContact> helplines=consumerInstrumentServiceImpl.getHelplineNo();
    				
	    				if(helplines!=null && !helplines.isEmpty()){
	    					JSONArray arr=new JSONArray();
	    						JSONObject result=null;
	    							result=new JSONObject();
				    					for(TblHelplineContact hc : helplines){
				    							logger.debug("In for loop ");
				    								JSONObject json=null;
				    									json=new JSONObject();
				    									json.put("helpline_contact",hc.getContactNo());
				    									json.put("type",hc.getType());
				    									arr.add(json);
				    								
				    		    		}
				    					
		    							logger.debug("/result JSONArray ",arr);
		    							result.put("contacts", arr);		    							
		    							String response = JsonUtil.objToJson(result);
	    	    					    responseEntity = new ResponseEntity<String>(response,HttpStatus.OK);
    						
    				}else{
    					statusDto.setStatusCode(HttpStatus.NOT_FOUND.toString());
						statusDto.setStatusDesc("contact numbers not found");
						String response = JsonUtil.objToJson(statusDto);
    					responseEntity = new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
    					
    				}
				
		}catch(AtAppException ae) {
			logger.debug("IN contoller catch block /helpline");
			statusDto.setStatusDesc(ae.getMessage());
			statusDto.setStatusCode(ae.getHttpStatus().toString());
			String response = JsonUtil.objToJson(statusDto);
			responseEntity = new ResponseEntity<String>(response, ae.getHttpStatus());
		}
		
		
		return responseEntity;
	}
	
}
	