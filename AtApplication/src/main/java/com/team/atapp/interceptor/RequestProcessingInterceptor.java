package com.team.atapp.interceptor;
/*package com.team.mighty.interceptor;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class RequestProcessingInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = Logger.getLogger(RequestProcessingInterceptor.class);

	public int i;
	public static  String hasAuthentication="undefined";
	public static String autherisation_insertion_mode="undefined";
		
	
	@Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		
				
		logger.debug(">> FILTER <<");
		String indexPage="/";
		String forwardPage="/";
		String queryString="";
		HttpSession session = request.getSession();
	
			String role="";
				String preUrl = request.getServletPath();
					String actualPrePage=getURI(request);			
						String page=preUrl;
							queryString= getQueryString(request)==null?"": getQueryString(request);
								logger.debug("preUrl as "+page);
		page=page==null?"":page;
		if(page.equals("")==false )
		{			
			if(page.equals("/")){
				page=page.substring(0);
			}
			else{
			page=page.substring(1);
			}
			
			if(page.contains("?"))
			{ System.out.println("page in ???"+page);
				request.getSession().invalidate();
				page=page.substring(0,page.indexOf("?"));
				
			}
			
			queryString=queryString==""?"":"?"+queryString;
			forwardPage=page+queryString;
			logger.debug("forword page"+forwardPage);
		}else
		{
			page=indexPage;
			forwardPage=page;
		}

	
		if(page.equals("/") || page.equals("forgotPassword") || page.equals("resetPassword"))
		{	
			return true;
		}
		else if (page.equals("login")) { 
				return true;
			  }
		
		return true;
	
	}

    

private String getURI(HttpServletRequest request ) {
	
	String returnString="";
	try {
		returnString = request.getAttribute("javax.servlet.forward.request_uri").toString();
		if(returnString.trim().equals("")) {
			returnString = request.getServletPath().substring(1);
		} else {
			returnString.substring(returnString.lastIndexOf("/")+1);
		}
	}catch(Exception ex) {
		returnString = request.getServletPath().substring(1); 
	}
	
	return returnString;
}

public String getQueryString( HttpServletRequest servletRequest  )
{
	
	StringBuilder queryString = new StringBuilder();
	  Map<String, String[]> params= servletRequest.getParameterMap();
	  Set<String> pset= params.keySet();
	  boolean isFirst=true;
	  if(pset!=null&&pset.size()>0) {
	  	  for(String name:pset) {
	  		  for(String value:params.get(name) ) {
				 StringBuilder tocken=new StringBuilder();
        		   tocken.append(name).append("=").append(value);
        		   				if(isFirst) {
        		   						queryString.append(tocken);
        		   						isFirst=false;
        		   				} else {
        		   						queryString.append("&").append(tocken);
        		   }
        		    
			  }
      		 
		  }
	  }
	  return queryString.toString();
}
}
*/