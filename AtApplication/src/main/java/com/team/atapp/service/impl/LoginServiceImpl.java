package com.team.atapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.atapp.dao.AdminDao;
import com.team.atapp.dao.LoginDao;
import com.team.atapp.domain.AdminUser;
import com.team.atapp.service.LoginService;
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private LoginDao loginDao;
	
	@Autowired
	private AdminDao adminDao;
	
	public AdminUser getLoginUser(String username, String password)	throws Exception {
		
		return loginDao.getLoginUser(username,password);
	}

		public AdminUser getUserByEmail(String email) throws Exception {
			return loginDao.getUserByEmail(email);
	}

			
		public String getPasswordResetMessage(AdminUser admin) throws Exception {
			return "Hi "
					+admin.getDisplayname()				
					+",<br/><br/>Your Password has been reset. To access Mighty use the below information.<br/><br/> "
					+ ".<br/><br/>"
					+"Link - <a href='http://mighty2.cloudaccess.host/MightyCloud/'>Mighty</a>"
					+"<br/><br/>Login Id - "+admin.getLoginId()
					+"<br/><br/>Password - "+admin.getPassword()
					+"<br/><br/>Regards,<br/>" 
					+ "<a href='http://mighty2.cloudaccess.host/MightyCloud/'>http://mighty2.cloudaccess.host/MightyCloud/</a>\n"
							+" Mighty Team."
							+"</a>"+"<br/>---------------<br/> <i><u>Note:</u> This is a system generated email. Please do not reply.</i>";
		
			
		}

	
		public AdminUser setGeneratedPwd(AdminUser adminUser) throws Exception {
			return (AdminUser)adminDao.save(adminUser);
		}

	
		public AdminUser getUserById(String id) throws Exception {
			return loginDao.getUserById(id);
		}

}
