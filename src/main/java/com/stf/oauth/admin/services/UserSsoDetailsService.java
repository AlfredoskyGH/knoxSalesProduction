package com.stf.oauth.admin.services;

import com.stf.SsoModuleWebApplication;
import com.stf.oauth.authorization.config.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.logging.Logger;

public class UserSsoDetailsService implements UserDetailsService {
	
	final Logger log = Logger.getLogger(SsoModuleWebApplication.class.getName());
	
	private UserSsoService userSsoService;
	
	public UserSsoDetailsService(UserSsoService userSsoService) {
		this.userSsoService = userSsoService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("\n User: "+username);
		return new CustomUserDetails(userSsoService.findUserSsoByUsername(username));
	}

}
