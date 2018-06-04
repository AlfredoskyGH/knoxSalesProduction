package com.stf.oauth.authorization.config;

import com.stf.oauth.admin.commons.helper.LoginHelper;
import com.stf.oauth.admin.entities.UserSso;
import com.stf.oauth.admin.services.UserSsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Implementacion personalizada del proceso de autenticaci+on
 * dentro del SpringSecurity para el modulo SSO.
 * 
 * @author team ssoinnova4j
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private LoginHelper loginHelper;

	@Autowired
	private UserSsoService userSsoService;

	// Atributos Globales
	public Boolean isAuthInLDAP = Boolean.FALSE;
	public Boolean isUserAdminSSO = Boolean.FALSE;
	public String passwordDecode = "";

	@Override
	public boolean supports(Class<?> auth) {
		return auth.equals(UsernamePasswordAuthenticationToken.class);
	}
	
	/**
	 * Implementación partícular del proceso de autenticación
	 */
	@Override
	public UsernamePasswordAuthenticationToken authenticate(Authentication authentication) throws AuthenticationException {
		Authentication auth = null;

		UserSso user = new UserSso();
		isUserAdminSSO = Boolean.FALSE;
		isAuthInLDAP = Boolean.FALSE;
		passwordDecode = "";
		String username = authentication.getName().trim();
		String password = authentication.getCredentials().toString().trim();
		passwordDecode = password;

		// Se consulta si el usuario es administrador del modulo SSO
		if (userSsoService.isAdminSsoUser(username)) {
			isUserAdminSSO = Boolean.TRUE;
		} else {
			//Se procede a autenticar el usuario contra el Active Directory
			if (loginHelper.autenticarUserInActiveDirectory(username, password)) {
				isAuthInLDAP = Boolean.TRUE;
			} else {
				isAuthInLDAP = Boolean.FALSE;
			}
		}

		return null;
	}


	// Getter y Setter
	public LoginHelper getLoginHelper() {
		return loginHelper;
	}

	public void setLoginHelper(LoginHelper loginHelper) {
		this.loginHelper = loginHelper;
	}

	public UserSsoService getUserSsoService() {
		return userSsoService;
	}

	public void setUserSsoService(UserSsoService userSsoService) {
		this.userSsoService = userSsoService;
	}
}
