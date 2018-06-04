package com.knox.oauth.admin.services;

import com.knox.SsoModuleWebApplication;
import com.knox.oauth.authorization.config.CustomUserDetails;

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
