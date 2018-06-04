package com.knox.oauth.admin.controllers;

import com.knox.oauth.admin.commons.bean.HashGenerator;
import com.knox.oauth.admin.commons.bean.ObjectsJSON;
import com.knox.oauth.admin.commons.bean.ObjectsJSON.RoleAppJSON;
import com.knox.oauth.admin.commons.bean.ObjectsJSON.UserJson;
import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.ApplicationClient;
import com.knox.oauth.admin.entities.RoleApp;
import com.knox.oauth.admin.entities.UserSso;
import com.knox.oauth.admin.services.ApplicationClientService;
import com.knox.oauth.admin.services.MailSenderService;
import com.knox.oauth.admin.services.UserSsoService;
import com.knox.oauth.authorization.config.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

//TODO obtener la ruta post de todos los metodos de un archivo de propiedades.
@RestController
public class EndPointsRestController {

	@Autowired
	UserSsoService userSsoService;

	@Autowired
	ApplicationClientService applicationClientService;

	@Autowired
	private MailSenderService mailSenderService;

	@Autowired
	@Resource(name="tokenServices")
	ConsumerTokenServices tokenServices;

	@Autowired
	@Resource(name="tokenStore")
	TokenStore tokenStore;

	@Autowired
	public void setUserSsoService(UserSsoService userSsoService) {
		this.userSsoService = userSsoService;
	}

	@Autowired
	public void setMailSenderService(MailSenderService mailSenderService) {
		this.mailSenderService= mailSenderService;
	}

	/**
	 *
	 * @param request
	 * @return EndPoint Informacion de Usuario
	 */
	@RequestMapping(value = "/user-info", method = RequestMethod.GET)
	public UserJson getUserInfo(HttpServletRequest request) {
		String access_token = request.getHeader("access_token");
		OAuth2Authentication oauth2 = tokenStore.readAuthentication(access_token);
		Authentication auth = oauth2.getUserAuthentication();
		CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
		UserJson userJson = new ObjectsJSON().new UserJson();
		userJson = userSsoService.getUserJSON(user.getUsername());
		return userJson;
	}

	/**
	 *
	 * @param request
	 * @return EndPoint Permisos de Usuario
	 */
	@RequestMapping(value = "/permissions", method = RequestMethod.GET)
	public List<RoleAppJSON> getPermissions(HttpServletRequest request) {
		List<RoleAppJSON> resultLis = new ArrayList<>();

		String username = request.getHeader("username");
		String client_id = request.getHeader("client_id");
		UserSso user = new UserSso();
		user = userSsoService.findUserSsoByUsername(username);
		ApplicationClient app = applicationClientService.getApplicationClientByClientId(client_id);

		//Obtengo todos los roles que el usuario tiene asignado a la aplicacion
		for (RoleApp roleApp: app.getRoleApps()){
			for (RoleApp roleUser: user.getRoles()) {
				if (roleApp.equals(roleUser)){
					RoleAppJSON roleAppJSON = new ObjectsJSON().new RoleAppJSON();
					roleAppJSON = roleAppJSON.getRoleAppJSON(roleApp);
					resultLis.add(roleAppJSON);
					break;
				}
			}
		}

		return resultLis;
	}

	/**
	 *
	 * @param tokenId
	 * @return EndPoint de Logout
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/logout/{tokenId:.*}")
	public String revokeToken(HttpServletRequest request, HttpServletResponse response, @PathVariable String tokenId) {
		if(tokenServices.revokeToken(tokenId)){
			return "Cierre de session satisfactorio.";
		}else{
			return "No se pudo cerrar la session.";
		}
	}

	/**
	 *
	 * @param request
	 * @return EndPoint Cambio de Contraseña
	 */
	@RequestMapping(value = "/change-password", method = RequestMethod.POST)
	public Map<String, String> changePassword(HttpServletRequest request) {

		String access_token = request.getHeader("access_token");
		OAuth2Authentication oauth2 = tokenStore.readAuthentication(access_token);
		Authentication auth = oauth2.getUserAuthentication();
		CustomUserDetails userCustomer = (CustomUserDetails) auth.getPrincipal();

		Map<String, String> result = new HashMap<String, String>();
		HashGenerator hashGenerator = new HashGenerator();
		UserSso user = new UserSso();
		user = userSsoService.findUserSsoByUsername(userCustomer.getUsername());

		if(user.getEmail() == null){
			result.put("Error:", "Usuario no posee correo electronico");
		}
		else{
			//genero token
			String token = hashGenerator.getToken();
			try {
				//envio correo
				if(sendEmailToken(request, token, user.getEmail())){
					user.setToken(token);
					user.setTokenDateValidate(new Date(System.currentTimeMillis() + 3600000));
					userSsoService.saveUserSso(user);
					result.put("Mensaje:", "Se envio el correo electronico correctamente");

				}else{
					result.put("Mensaje:", "Ocurrieron inconvenientes al momento de enviar el correo electrónico.");
				}
			} catch (SSODataException e) {
				e.printStackTrace();
				result.put("Mensaje:", e.getMensaje());
			}
		}
		return result;
	}

	public boolean sendEmailToken(HttpServletRequest request, String token, String destinatario) {
		JavaMailSender emailSender = mailSenderService.getJavaMailSender();
		try {
			String uri = request.getScheme() + "://" +
					request.getServerName() +
					":" +
					request.getServerPort() + "/password/recuperar/" + token;
			mailSenderService.sendEmailToken(destinatario, uri, emailSender);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void invalidateSession(HttpServletRequest request, HttpServletResponse response) {

		response.setContentType("text/html");

		Cookie[] cookies = request.getCookies();

		// Delete all the cookies
		if (cookies != null) {

			for (int i = 0; i < cookies.length; i++) {

				Cookie cookie = cookies[i];
				cookies[i].setValue(null);
				cookies[i].setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

}
