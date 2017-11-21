package com.team.atapp.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.team.atapp.domain.TblServiceProvider;
import com.team.atapp.domain.TblUserInfo;
import com.team.atapp.logger.AtLogger;
import com.team.atapp.service.ConsumerInstrumentService;

@Controller
public class UserInfoController {
	
	private static final AtLogger logger = AtLogger.getLogger(LoginController.class);
	
	@Autowired
	private ConsumerInstrumentService consumerInstrumentServiceImpl;
	
	@RequestMapping(value= {"/userInfoHistory"}, method=RequestMethod.GET)
    public String userInfoHistoryHandler(Map<String,Object> map) {
			List<TblUserInfo> userInfos=consumerInstrumentServiceImpl.getUserInfos();
				map.put("userInfos", userInfos);
					return "userInfo";
		 
	 }
	
	
	@RequestMapping(value= {"/spSubscription"}, method=RequestMethod.GET)
    public String spSubscriptionHandler(Map<String,Object> map){
			return "spInfo";
		 
	 }
	
	
	@RequestMapping(value= {"/geoCode"}, method=RequestMethod.GET)
    public void geoCodeHandler(Map<String,Object> map){
		logger.debug("Geocoding  geocoding");
		try{
			String url="https://maps.googleapis.com/maps/api/geocode/json?&address=thulpcafe,Bangalore";
			logger.debug("URLConn",url);
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());

		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
			
		 
	 }
	
	
	@RequestMapping(value= {"/spInfosSubmit"}, method=RequestMethod.POST)
    public String onSubmitHandler(HttpServletRequest request,Map<String,Object> map,@RequestParam("file") MultipartFile file){
		logger.debug("In submitting /spInfosSubmit ");
		
	
		String comp=request.getParameter("comp");
		String spname=request.getParameter("spname");	
		String gst=request.getParameter("gst");	
		String regNo=request.getParameter("regNo");	
		String website=request.getParameter("website");	
		String threeD=request.getParameter("threeD");	
		String manual=request.getParameter("manual");	
		String openTime=request.getParameter("openTime");	
		String closeTime=request.getParameter("closeTime");	
		String email=request.getParameter("email");	
		String contact=request.getParameter("contact");	
		String password=request.getParameter("password");	
		String address=request.getParameter("address");	
		String lati=request.getParameter("lati");	
		String longi=request.getParameter("longi");	
		String slot=request.getParameter("slot");
		
		//String file=request.getParameter("file");	
		
		logger.debug("/comp",comp);
		logger.debug("/spname",spname);
		logger.debug("/gst",gst);
		logger.debug("/regNo",regNo);
		logger.debug("/website",website);
		logger.debug("/threeD",threeD);
		logger.debug("/manual",manual);
		logger.debug("/openTime",openTime);
		logger.debug("/closeTime",closeTime);
		logger.debug("/email",email);
		logger.debug("/contact",contact);
		logger.debug("/password",password);
		logger.debug("/address",address);
		logger.debug("/lati",lati);
		logger.debug("/longi",longi);
		logger.debug("/slot",slot);
		
		TblServiceProvider sp=null;
				sp=new TblServiceProvider();
				sp.setCompany(comp);
				sp.setDisplayName(spname);
				sp.setGstNum(gst);
				sp.setRegistrationNum(regNo);
				sp.setWebsite(website);
				sp.setThreeD(threeD);
				sp.setManual(manual);
				sp.setOpenTime(openTime);
				sp.setCloseTime(closeTime);
				sp.setEmailId(email);
				sp.setPhoneNumber(contact);
				sp.setPassword(password);
				sp.setAddress(address);
				sp.setLatitude(lati);
				sp.setLongitude(longi);
				sp.setSlots(slot);
				sp.setPersonIncharge(spname);
				sp.setOpenStatus("unfreeze");
				sp.setCreatedAt(new Date(System.currentTimeMillis()));
				sp.setUpdatedAt(new Date(System.currentTimeMillis()));
				
				
				consumerInstrumentServiceImpl.updateSP(sp);
	
		
		
		return "redirect:/spInfoReport";
		 
	 }
	
	@RequestMapping(value= {"/spInfoReport"}, method=RequestMethod.GET)
    public String spInfoReportHandler(Map<String,Object> map){
		logger.debug("In submitting /spInfoReport ");
			return "spInfoReport";
		 
	 }

}
