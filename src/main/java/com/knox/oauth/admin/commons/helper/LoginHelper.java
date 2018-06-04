package com.knox.oauth.admin.commons.helper;

import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.UserSso;
import com.knox.oauth.admin.services.UserSsoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Clase que permite apoyar las acciones y opciones que se generan el proceso de
 * autenticación, sea por BD o por Active Dierctory.
 * 
 * @author team ssoinnova4j
 *
 */
@Component
public class LoginHelper {

	private final Logger log = Logger.getLogger(LoginHelper.class.getName());

	@Value("${serverName}")
	private String serverName;

	@Value("${domainAD}")
	private String domainAD;

	@Autowired
	private UserSsoService userSsoService1;
	
	public UserSso findUserSsoOnDb(final UserSsoService userSsoService, String usernameCredential)
			throws UsernameNotFoundException {
		UserSso result = new UserSso();
		log.info("\n Usuario a Consultar: " + usernameCredential);
		result = userSsoService.findUserSsoByUsername(usernameCredential);
		try {
			if (result.getId() == null) {
				result = null;
			}
			// Se valida la fecha activo hasta del usuario
			if (result != null && result.getUserActiveUntil() != null) {
				Date hoy = new Date();
				if (hoy.after(result.getUserActiveUntil())) {
					result.setEnabled(false);
					result = userSsoService.saveUserSso(result);
					//int dias = (int) ((hoy.getTime() - result.getUserActiveUntil().getTime()) / 86400000);
					throw new UsernameNotFoundException("El usuario actualmente no tiene acceso al Sistema.");
				}
			}
		} catch (SSODataException e) {
			throw new UsernameNotFoundException(e.getMensaje());
		} catch (UsernameNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
		return result;
	}

	/**
	 * Conectarse y autenticar un usuario en Active Dierctory
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public Boolean autenticarUserInActiveDirectory(String username, String password) {
		Boolean result = Boolean.FALSE;
		ActiveDirectoryHelper activeDirectoryHelper;

		log.info("Iniciando Autenticacion con Active Directory");

		String domain = domainAD;
		String servidor = serverName;
		// Creating instance of ActiveDirectory
		activeDirectoryHelper = new ActiveDirectoryHelper();
		result = activeDirectoryHelper.isUserValidInLDAP(username, password, domain, servidor);

		log.info("Usuario Autenticado contra el Active Directory");
		// Closing LDAP Connection
		activeDirectoryHelper.closeLdapConnection();
		return result;
	}

	//Getter & Setter
	public UserSsoService getUserSsoService() {
		return userSsoService1;
	}

	public void setUserSsoService(UserSsoService userSsoService) {
		this.userSsoService1 = userSsoService;
	}

	public HttpSession getSession(){
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
		HttpServletRequest request = attributes.getRequest();
		HttpSession session = request.getSession(true);
		session.setAttribute("errorLogin", "");

		return session;
	}
}
