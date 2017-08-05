package com.team.atapp.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.team.atapp.logger.AtLogger;
import com.team.atapp.service.LoginService;

@Controller
public class LoginController {
	private static final AtLogger logger = AtLogger.getLogger(LoginController.class);
	
	@Autowired
	private LoginService loginService;
		
	
	@RequestMapping(value= {"/"})
	public String defaultURL(){
		return "index";
	}
	
	
	@RequestMapping(value= {"/inValid"})
	public String inValidCredentials(){
		return "index";
	}
	
	@RequestMapping(value= {"/forgotPassword"})
	public String forgetPasswordHandler(){
		return "forgotPassword";
	}
	
		
	@RequestMapping(value= {"/adminHome"}, method=RequestMethod.GET)
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception{
	   	return new ModelAndView("AdminView");
		    	
	}
		 
		 
		 
		
		 
	@RequestMapping(value= {"/logout"})
			public String goToLogout(HttpServletRequest request,HttpServletResponse response,Map<String,Object> map){
				logger.debug("In gotoLogout Page......");
					HttpSession session = request.getSession(true);
						session.invalidate();
							response.setHeader("Cache-Control",	"no-cache, no-store, must-revalidate");
								response.setHeader("Pragma", "co-cache");
									response.setDateHeader("Expires", 0);
										return "redirect:/";
			}
		 	
		 	
		 	
				

			
}
