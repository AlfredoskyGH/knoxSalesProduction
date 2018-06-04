package com.knox.oauth.admin.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.knox.oauth.admin.commons.RolType;
import com.knox.oauth.admin.commons.bean.AplicacionesUsuario;
import com.knox.oauth.admin.commons.bean.HashGenerator;
import com.knox.oauth.admin.commons.bean.Notification;
import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.ApplicationClient;
import com.knox.oauth.admin.entities.RoleApp;
import com.knox.oauth.admin.entities.UserSso;
import com.knox.oauth.admin.services.ApplicationClientService;
import com.knox.oauth.admin.services.MailSenderService;
import com.knox.oauth.admin.services.RoleAppService;
import com.knox.oauth.admin.services.UserSsoService;

/**
 * @author team ssoinnova4j
 * 
 * Clase controladora del funcionamiento y administración de los
 * usuarios y sus aplicaciones. 18/03/2018
 */
@Controller(value="UserSsoController")
public class UserSsoController {

    
    private UserSso userSsoRef;
	private HashGenerator hashGenerator = new HashGenerator();
    private ApplicationClient applicationClientRef;
	private AplicacionesUsuario appsUserWrapper;
	private RoleApp roleAppRef;
	private Notification notification;
	
	private List<ApplicationClient> listApplicationClientRef;
	private Set<ApplicationClient> setApplicationClientRef;
	private List<UserSso> listUserSsoRef;
	private Set<RoleApp> listRoleAppRef;
	
	//Services
	private UserSsoService userSsoService;
	private ApplicationClientService applicationClientService;
	private RoleAppService roleAppService;
	private MailSenderService mailSenderService;

	@Autowired
	public void setUserSsoService(UserSsoService userSsoService) {
		this.userSsoService = userSsoService;
	}

    
    @Autowired
	public void setApplicationClientService(ApplicationClientService applicationClientService) {
		this.applicationClientService = applicationClientService;
	}
	
	@Autowired
	public void setRoleAppService(RoleAppService roleAppService) {
		this.roleAppService = roleAppService;
	}

	@Autowired
	public void setMailSenderService(MailSenderService mailSenderService) {
		this.mailSenderService = mailSenderService;
	}

    //USER
	/**
	 * Metodo para listar todos los usuarios en BD o en Active Directory.
	 *
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = "/sso/usuarios", method = RequestMethod.GET)
	public String list(Model model) {
		listUserSsoRef = new ArrayList<UserSso>();
		listUserSsoRef = userSsoService.listUsersSso();
		model.addAttribute("usersSso", listUserSsoRef);
		return "usuarios";
	}

	/**
	 * Consultar un usuario especifico por ID *
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/sso/usuario/{id}")
	public String showUserSso(@PathVariable Long id, Model model) {
		userSsoRef = new UserSso();
		userSsoRef = userSsoService.getUserSsoById(id);
		model.addAttribute("userSso", userSsoRef);
		return "usuarioshow";
	}

	/**
	 * Ediatr un usuario especifico por ID
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/sso/usuario/edit/{id}")
	public String editUserSso(@PathVariable Long id, Model model) {
		userSsoRef = new UserSso();
		userSsoRef = userSsoService.getUserSsoById(id);

		model.addAttribute("userSso", userSsoRef);
		return "usuarioform";
	}

	/**
	 * Crear un nuevo Usuario.
	 *
	 * @param model
	 * @return
	 */
	@RequestMapping("/sso/usuario/new")
	public String newUserSso(Model model) {
		model.addAttribute("userSso", new UserSso());
		return "usuarioform";
	}

	/**
	 * Logout Usuario.
	 *
	 * @param
	 *
	 * @return
	 */
	@RequestMapping("/sso/logout-sso")
	public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("userSso", new UserSso());
		invalidateSession(request, response);
		return "redirect:/login";
	}

	/**
	 * Guardar un usuario en la Fuente de Datos
	 *
	 * @param userSso
	 * @return
	 */
	@RequestMapping(value = "/sso/usuario", method = RequestMethod.POST)
	public String saveUserSso(UserSso userSso, Model model) {
		userSsoRef = new UserSso();
		notification = new Notification();
		try {
			//Si el usuario tiene Id quiere decir que se trata de una edicion
			if(userSso.getId() != null){
				userSsoRef = userSsoService.findUserSsoByUsername(userSso.getUsername());
				userSso.setRoles(userSsoRef.getRoles());
				userSsoRef = userSsoService.saveUserSso(userSso);
			}else{
				String passwordTemp = hashGenerator.getPasswordTemp();
				userSso.setPassword(passwordTemp);
				if(sendEmailNewUser(userSso, passwordTemp)){
					userSsoRef = userSsoService.saveUserSso(userSso);
				}else{
					notification.alert("1", "ERROR", "Ocurrieron inconvenientes al momento de enviar el correo. Verifíque que la dirección indicada sea válida.");
					model.addAttribute("userSso", userSso);
					model.addAttribute("notification", notification);
					return "usuarioform";
				}
			}

		} catch (SSODataException e) {
			if (e.getCodigoErr().equals("DUPKEY")) {
				notification.alert("1", "ERROR", "Nombre de Usuario Ya EXISTE: " + userSso.getUsername());
			} else {
				notification.alert("1", "ERROR", e.getMensaje());
			}
			model.addAttribute("userSso", userSso);
			model.addAttribute("notification", notification);
			return "usuarioform";
		}
		notification.alert("1", "SUCCESS",
				"Usuario: ".concat(userSsoRef.getUsername()).concat(" Actualizado de forma EXITOSA"));
		model.addAttribute("userSso", userSso);
		model.addAttribute("notification", notification);
		return "usuarioshow";
	}

	/**
	 * Eliminar un usuario por el ID
	 * 
	 * @param id
	 * @return String
	 */
	@RequestMapping("/sso/usuario/delete/{id}")
	public String deleteUserSso(@PathVariable Long id, Model model) {
		userSsoRef = userSsoService.getUserSsoById(id);

		notification = new Notification();
		if(userSsoRef.isAdmin()){
			notification.alert("1", "ERROR",
					"No se puede eliminar un usuario Administrador.");
		}else{
			userSsoService.deleteUserSso(id);

			notification.alert("1", "SUCCESS",
					"El Usuario " + userSsoRef.getUsername() + " se ha eliminado correctamente.");
		}

		listUserSsoRef = new ArrayList<UserSso>();
		listUserSsoRef = userSsoService.listUsersSso();
		model.addAttribute("notification", notification);
		model.addAttribute("usersSso", listUserSsoRef);
		return "usuarios";
	}

	// USUARIO-APLICACION
	/**
	 * Metodo que permite consultar un usuario y la gestion de sus aplicaciones.
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/sso/usuario/{id}/ver")
	public String verUsuariosAplicaciones(@PathVariable Long id, Model model) {
		userSsoRef = new UserSso();
		// Wrapper para lista de aplicaciones vinculadas
		appsUserWrapper = new AplicacionesUsuario();
		listApplicationClientRef = new ArrayList<>();

		userSsoRef = userSsoService.getUserSsoById(id);
		listApplicationClientRef = this.llenarListaAppVinculadas(userSsoRef, appsUserWrapper.getListaId());

		Iterable<ApplicationClient> iter1 = listApplicationClientRef;
		for (ApplicationClient app : iter1) {
			appsUserWrapper.setListaId(appsUserWrapper.getListaId().concat(app.getId().toString()).concat(","));
		}
		appsUserWrapper.setListApplicationsUsers(listApplicationClientRef);
		listApplicationClientRef = applicationClientService.listAplicacionesClientes();
		appsUserWrapper.setUser(userSsoRef);
		appsUserWrapper.setApp(new ApplicationClient(userSsoRef));

		//La notificación no se limpiara en caso de que se haya hecho una desvinculacion
		if (notification!= null){
			if (notification.getMessage().contains("desvinculada")){
				model.addAttribute("notification", notification);// vincularaplicaciones
			}
		}

		model.addAttribute("userSso", appsUserWrapper.getUser());// usuarioshow
		model.addAttribute("aplicacion", appsUserWrapper.getApp());// vincularaplicaciones
		model.addAttribute("aplicaciones", listApplicationClientRef);// vincularaplicaciones
		model.addAttribute("wrapper", appsUserWrapper);// vincularaplicaciones
		notification = new Notification();

		return "usuarioaplicaciones";
	}

	/**
	 * Vincular una aplicacion a un usuario
	 * 
	 * @param wrapper
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/sso/usuario/aplicacion/vincular", method = RequestMethod.POST)
	public String saveAplicacionClienteUsuario(AplicacionesUsuario wrapper, Model model) {
		userSsoRef = new UserSso();
		listApplicationClientRef = new ArrayList<>();
		applicationClientRef = new ApplicationClient();
		appsUserWrapper = new AplicacionesUsuario();
		notification = null;

		userSsoRef = userSsoService.getUserSsoById(wrapper.getUser().getId());
		listApplicationClientRef = this.llenarListaAppVinculadas(userSsoRef, wrapper.getListaId());

		Iterable<ApplicationClient> iter = listApplicationClientRef;
		for (ApplicationClient app : iter) {
			if (app.getId().equals(wrapper.getApp().getId())) {
				notification = new Notification();
				notification.alert("1", "ERROR",
						"La Aplicación: " + app.getClientId() + ", ya esta Viculada al Usuario.");
			}
		}

		if (notification == null) {
			applicationClientRef = applicationClientService
					.getAplicacionClienteById(wrapper.getApp().getId());
			applicationClientRef.setUserSso(userSsoRef);
			
			notification = new Notification();
			notification.alert("1", "SUCCESS",
					"Aplicación: " + applicationClientRef.getName() + ",Vinculada EXITOSAMENTE.");
			listApplicationClientRef.add(applicationClientRef);
			appsUserWrapper.setListaId(
					wrapper.getListaId().concat(applicationClientRef.getId().toString()).concat(","));
		}

		appsUserWrapper.setListApplicationsUsers(listApplicationClientRef);
		listApplicationClientRef = applicationClientService.listAplicacionesClientes();
		appsUserWrapper.setUser(userSsoRef);
		appsUserWrapper.setApp(applicationClientRef);
		
		model.addAttribute("userSso", userSsoRef);// usuario show
		model.addAttribute("aplicacion", appsUserWrapper.getApp());
		model.addAttribute("aplicaciones", listApplicationClientRef);// aplicaciones vinculadas
		model.addAttribute("wrapper", appsUserWrapper);// aplicaciones vinculadas
		model.addAttribute("notification", notification);

		return "usuarioaplicaciones";
	}

	/**
	 * Desvincular una aplicacion de un usuario
	 * 
	 * @param id
	 * @param userId
	 * @return Long
	 */
	@RequestMapping("/sso/usuario/{userId}/aplicacion/delete/{id}")
	public ModelAndView deleteRolAplicacionCliente(@PathVariable Long userId, @PathVariable Long id) {
		String mensaje = null;
		applicationClientRef = new ApplicationClient();
		userSsoRef = new UserSso();
		listApplicationClientRef = new ArrayList<>();
		ModelAndView modelAndView = new ModelAndView();
		notification = new Notification();

		try {
			applicationClientRef = applicationClientService.getAplicacionClienteById(id);
			userSsoRef = userSsoService.getUserSsoById(userId);
			listRoleAppRef = userSsoRef.getRoles();

			for (RoleApp rol: listRoleAppRef ) {
				if(rol.getAplicationClient().equals(applicationClientRef)){
					userSsoRef.removeRoleApp(rol);
				}
			}

			// userSsoRef.removeApplicationClient(applicationClientRef);
			userSsoRef = userSsoService.saveUserSso(userSsoRef);
			listApplicationClientRef = applicationClientService.listAplicacionesClientes();
			notification.alert("1", "SUCCESS",
					"Aplicación: " + applicationClientRef.getName() + ",desvinculada EXITOSAMENTE.");
		} catch (SSODataException e) {
			if (e.getCodigoErr().equals("DUPKEY")) {
				mensaje = new String("Falla al momento de Eliminar Aplicacion dentro de Usuario: " + id);
			}
		}

		modelAndView.setViewName("redirect:/sso/usuario/" + userId + "/ver");
		modelAndView.addObject("notification", notification);
		return modelAndView;
	}

	// USUARIO-APLICACION-ROLE
	/**
	 * Metodo que permite consultar los roles de una aplicacion para vincular al
	 * usuario.
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/sso/usuario/{id}/aplicacion/{appId}/ver")
	public String verRolesAplicacionesUsuario(@PathVariable Long id, @PathVariable Long appId, Model model) {
		// Usuario
		userSsoRef = new UserSso();
		applicationClientRef = new ApplicationClient();
		listRoleAppRef = new HashSet<RoleApp>();
		roleAppRef = new RoleApp();
		Set<RoleApp> listVinculadas = new HashSet<>();

		userSsoRef = userSsoService.getUserSsoById(id);
		applicationClientRef = applicationClientService.getAplicacionClienteById(appId);
		applicationClientRef.setUserSso(userSsoRef);
		listRoleAppRef = applicationClientRef.getRoleApps();
		roleAppRef.setAplicationClient(applicationClientRef);
		
		Iterable<RoleApp> iter = userSsoRef.getRoles();
		for (RoleApp roleApp : iter) {
			roleApp.setAplicationClient(applicationClientRef);
			listVinculadas.add(roleApp);
		}

		model.addAttribute("userSso", userSsoRef);// usuarioshow
		model.addAttribute("aplicacion", applicationClientRef);// aplicacionshowresume
		model.addAttribute("roles", listRoleAppRef);// vincularrolesusuario
		model.addAttribute("rolesvinc", listVinculadas);// vincularrolesusuario
		model.addAttribute("rol", roleAppRef);// vincularrolesusuario

		return "usuarioaplicacionroles";
	}
	
	/**
	 * Vincular una aplicacion a un usuario
	 * 
	 * @param rol
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/sso/usuario/aplicacion/rol/vincular", method = RequestMethod.POST)
	public String saveRolUsuario(RoleApp rol, Model model) {
		userSsoRef = new UserSso();
		listApplicationClientRef = new ArrayList<>();
		listRoleAppRef = new HashSet<>();
		Set<RoleApp> listVinculadas = new HashSet<>();
		roleAppRef = new RoleApp();
		notification = null;
		Boolean existeRol = Boolean.FALSE;

		userSsoRef = userSsoService.getUserSsoById(rol.getAplicationClient().getUserSso().getId());
		
		Iterable<RoleApp> iter1 = userSsoRef.getRoles();
		for (RoleApp roleApp : iter1) {
			if (roleApp.getId().equals(rol.getId())) {
				existeRol = Boolean.TRUE;
				notification = new Notification();
				notification.alert("1", "ERROR", "Rol " + roleApp.getName()+", ya esta Vinculado al Usuario");
			}
		}
		
		if (!existeRol) {
			roleAppRef = roleAppService.getRolById(rol.getId());
			userSsoRef.addRoleApp(roleAppRef);
			roleAppRef.addUserSso(userSsoRef);
			try {
				userSsoRef = userSsoService.saveUserSso(userSsoRef);
			} catch (SSODataException e) {
				notification = new Notification();
				notification.alert("1", "ERROR", e.getMensaje());
			}		
		}
		
		applicationClientRef = applicationClientService.getAplicacionClienteById(rol.getAplicationClient().getId());
		listRoleAppRef = applicationClientRef.getRoleApps();
		applicationClientRef.setUserSso(userSsoRef);
		roleAppRef.setAplicationClient(applicationClientRef);
		
		Iterable<RoleApp> iter = userSsoRef.getRoles();
		for (RoleApp roleApp : iter) {
			roleApp.setAplicationClient(applicationClientRef);
			listVinculadas.add(roleApp);
		}
		if (notification == null) {
			notification = new Notification();
			notification.alert("1", "SUCCESS","Rol " + roleAppRef.getName()+", Vinculado EXITOSAMENTE.");
		}
		model.addAttribute("userSso", userSsoRef);// usuarioshow
		model.addAttribute("aplicacion", applicationClientRef);// aplicacionshowresume
		model.addAttribute("roles", listRoleAppRef);// vincularrolesusuario
		model.addAttribute("rolesvinc", listVinculadas);// vincularrolesusuario
		model.addAttribute("rol", roleAppRef);// vincularrolesusuario
		model.addAttribute("notification", notification);
		notification = new Notification();

		return "usuarioaplicacionroles";
	}
	
	/**
	 * Desvincular un rol de un usuario
	 * 
	 * @param id
	 * @param idUser
	 * @param idApp
	 * @return Long
	 */
	@RequestMapping("/sso/usuario/{idUser}/aplicacion/{idApp}/rol/delete/{id}")
	public String deleteRolUsuario(@PathVariable Long idUser, @PathVariable Long idApp, @PathVariable Long id, Model model) {
		userSsoRef = new UserSso();
		listApplicationClientRef = new ArrayList<>();
		roleAppRef = new RoleApp();
		listRoleAppRef = new HashSet<>();
		Set<RoleApp> listVinculadas = new HashSet<>();
		
		try {
			userSsoRef = userSsoService.getUserSsoById(idUser);
			roleAppRef = roleAppService.getRolById(id);

			if(userSsoRef.isAdmin() && roleAppRef.isAdmin()){
				notification = new Notification();
				notification.alert("1", "ERROR", new String("No se puede desvincular el rol al usuario administrador"));
			}else{
				userSsoRef.removeRoleApp(roleAppRef);
				roleAppRef.removeUserSso(userSsoRef);

				userSsoService.saveUserSso(userSsoRef);
			}

			applicationClientRef = applicationClientService.getAplicacionClienteById(idApp);
			listRoleAppRef = applicationClientRef.getRoleApps();
			applicationClientRef.setUserSso(userSsoRef);
			roleAppRef.setAplicationClient(applicationClientRef);
			
			Iterable<RoleApp> iter = userSsoRef.getRoles();
			for (RoleApp roleApp : iter) {
				roleApp.setAplicationClient(applicationClientRef);
				listVinculadas.add(roleApp);
			}
			
		} catch (SSODataException e) {
			if (e.getCodigoErr().equals("DUPKEY")) {
				notification = new Notification();
				notification.alert("1", "ERROR", new String("Falla Desvinculando Rol: " + roleAppRef.getName()));
			}
		}
		if (notification == null) {
			notification = new Notification();
			notification.alert("1", "SUCCESS","Rol " + roleAppRef.getName()+", se ha desvinculado EXITOSAMENTE.");
		}
		model.addAttribute("userSso", userSsoRef);// usuarioshow
		model.addAttribute("aplicacion", applicationClientRef);// aplicacionshowresume
		model.addAttribute("roles", listRoleAppRef);// vincularrolesusuario
		model.addAttribute("rolesvinc", listVinculadas);// vincularrolesusuario
		model.addAttribute("rol", roleAppRef);// vincularrolesusuario
		model.addAttribute("notification", notification);
		
		return "usuarioaplicacionroles";
	}
	
	/**
	 * Modificar un rol de un usuario
	 * @param rol
	 * @param model
	 *
	 * @return
	 */
	@RequestMapping(value = "/sso/usuario/aplicacion/rol/guardar", method = RequestMethod.POST)
	public String saveRolEditAplicacion(RoleApp rol, Model model) {
		userSsoRef = new UserSso();
		listApplicationClientRef = new ArrayList<>();
		listRoleAppRef = new HashSet<>();
		Set<RoleApp> listVinculadas = new HashSet<>();
		roleAppRef = new RoleApp();
		notification = null;
		Boolean existeRol = Boolean.FALSE;
		
		userSsoRef = userSsoService.getUserSsoById(rol.getAplicationClient().getUserSso().getId());
		applicationClientRef = applicationClientService.getAplicacionClienteById(rol.getAplicationClient().getId());
		
		Iterable<RoleApp> iter1 = applicationClientRef.getRoleApps();
		for (RoleApp roleApp : iter1) {
			if (roleApp.getName().equals(rol.getName())) {
				existeRol = Boolean.TRUE;
				notification = new Notification();
				notification.alert("1", "ERROR", "Rol " + roleApp.getName()+", ya existe con ese Nombre en la Aplicación: "+applicationClientRef.getName());
			}
		}
		
		if (existeRol){
			roleAppRef = roleAppService.getRolById(rol.getId());
			applicationClientRef.setUserSso(userSsoRef);
			roleAppRef.setAplicationClient(applicationClientRef);
			
			model.addAttribute("tiporoles", this.obtenerTipoRoles());
			model.addAttribute("rol", roleAppRef);
			model.addAttribute("notification", notification);
			return "roledituser";
			
		}else{
			roleAppRef = roleAppService.getRolById(rol.getId());
			userSsoRef.addRoleApp(roleAppRef);
			roleAppRef.addUserSso(userSsoRef);
			try {
				userSsoRef = userSsoService.saveUserSso(userSsoRef);
			} catch (SSODataException e) {
				notification = new Notification();
				notification.alert("1", "ERROR", e.getMensaje());
			}
			
			applicationClientRef = applicationClientService.getAplicacionClienteById(rol.getAplicationClient().getId());
			listRoleAppRef = applicationClientRef.getRoleApps();
			applicationClientRef.setUserSso(userSsoRef);
			roleAppRef.setAplicationClient(applicationClientRef);
			
			Iterable<RoleApp> iter = userSsoRef.getRoles();
			for (RoleApp roleApp : iter) {
				roleApp.setAplicationClient(applicationClientRef);
				listVinculadas.add(roleApp);
			}
			
			if (notification == null) {
				notification = new Notification();
				notification.alert("1", "SUCCESS",
						"Rol: ".concat(roleAppRef.getName()).concat(" Actualizado de forma EXITOSA"));
			}
			model.addAttribute("userSso", userSsoRef);// usuarioshow
			model.addAttribute("aplicacion", applicationClientRef);// aplicacionshowresume
			model.addAttribute("roles", listRoleAppRef);// vincularrolesusuario
			model.addAttribute("rolesvinc", listVinculadas);// vincularrolesusuario
			model.addAttribute("rol", roleAppRef);// vincularrolesusuario
			model.addAttribute("notification", notification);
			return "aplicacionrolform";
		}
	}
	
	/**
	 * Editar un rol de un usuario por el ID
	 * 
	 * @param id
	 * @param userId
	 * @param appId
	 * @return Long
	 */
	@RequestMapping("/sso/usuario/{userId}/aplicacion/{appId}/rol/edit/{id}")
	public String editarRolAplicacionCliente(@PathVariable Long userId, @PathVariable Long appId, @PathVariable Long id, Model model) {

		applicationClientRef = new ApplicationClient();
		roleAppRef = new RoleApp();
		userSsoRef = new UserSso();

		roleAppRef = roleAppService.getRolById(id);
		userSsoRef = userSsoService.getUserSsoById(userId);
		applicationClientRef = applicationClientService.getAplicacionClienteById(appId);
		applicationClientRef.setUserSso(userSsoRef);
		roleAppRef.setAplicationClient(applicationClientRef);
		
		model.addAttribute("tiporoles", this.obtenerTipoRoles());
		model.addAttribute("rol", roleAppRef);

		return "roledituser";
	}
	
	
	@RequestMapping("/login")
	public String login(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String errorLogin = (String) session.getAttribute("errorLogin");
		Boolean autenticado = (Boolean) session.getAttribute("authenticated");

		if(autenticado!= null && autenticado){
			return "redirect:/sso";
		}else{
			if(errorLogin != null){
				model.addAttribute("errorLogin", errorLogin);
			}
			model.addAttribute("userSso", new UserSso());
			return "login";
		}

	}

	@RequestMapping(value = "/password/olvido", method = RequestMethod.GET)
	public String forgetPassword(Model model) {
		model.addAttribute("accion", "olvido");
		model.addAttribute("userSso", new UserSso());
		return "password";
	}

	@RequestMapping(value = "/password/recuperar", method = RequestMethod.POST)
	public String resendPassword(HttpServletRequest request, UserSso userSso, Model model) {
		notification = new Notification();
		userSsoRef = userSsoService.findUserSsoByUsername(userSso.getUsername());
		if(userSsoRef.getId() == null){
			notification.alert("1", "ERROR", "El correo electrónico ingresado no se encuentra registrado.");
			model.addAttribute("accion", "olvido");
		}
		else{
			//genero token
			String token = hashGenerator.getToken();
			try {
				//envio correo
				if(sendEmailToken(request, token, userSsoRef.getEmail())){
					//almaceno token
					userSsoRef.setToken(token);
					userSsoRef.setTokenDateValidate(new Date(System.currentTimeMillis() + 3600000));
					userSsoService.saveUserSso(userSsoRef);
					notification.alert("1", "SUCCES", "Se ha enviado un correo electrónico a la dirección especificada.");
					model.addAttribute("accion", "envioToken");
				}else{
					notification.alert("1", "ERROR", "Ocurrieron inconvenientes al momento de enviar el correo electrónico.");
					model.addAttribute("accion", "olvido");
				}
			} catch (SSODataException e) {
				e.printStackTrace();
				notification.alert("1", "ERROR", "Ocurrieron inconvenientes al momento de asignar el codigo de validación al usuario.");
				model.addAttribute("accion", "olvido");
			}
		}

		model.addAttribute("notification", notification);
		model.addAttribute("userSso", userSsoRef);
		return "password";
	}

	//
	public String actualizarPassword(UserSso userSso) {
		userSsoRef = new UserSso();
		// genero token
		String token = hashGenerator.getToken();
		try {
			// envio correo
			if (sendEmailToken(token, userSso.getEmail())) {
				// almaceno token
				userSsoRef = userSsoService.findUserSsoByUsername(userSso.getUsername());
				userSsoRef.setToken(token);
				userSsoRef.setTokenDateValidate(new Date(System.currentTimeMillis() + 3600000));
				userSsoService.saveUserSso(userSsoRef);
			}
		} catch (SSODataException e) {
			e.printStackTrace();
		}
		return "actualizarpass";
	}
	

	@RequestMapping("/password/recuperar/{token}")
	public String validateToken(HttpServletRequest request, @PathVariable String token, Model model) {
		notification = new Notification();
		userSsoRef = userSsoService.findUserSsoByToken(token);
		Date now = new Date(System.currentTimeMillis());
		if(userSsoRef.getId() == null){
			notification.alert("1", "ERROR", "El código de recuperación de contraseña no es valido.");
			model.addAttribute("notification", notification);
			model.addAttribute("userSso", new UserSso());
			return "login";
		}else{
			if(now.after(userSsoRef.getTokenDateValidate())){
				notification.alert("1", "ERROR", "El código de recuperación de contraseña ya expiro, adquiera uno nuevo.");
				model.addAttribute("accion", "olvido");
				model.addAttribute("notification", notification);
			}else{
				model.addAttribute("accion", "recuperar");
			}
			model.addAttribute("userSso", userSsoRef);
			return "password";
		}
	}

	@RequestMapping("/password/nueva")
	public String changePassword(UserSso user, Model model) {
		notification = new Notification();
		UserSso userUpdate = userSsoService.getUserSsoById(user.getId());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if(userUpdate.getId() != null){
			userUpdate.setPassword(encoder.encode(user.getPassword()));
			try {
				userSsoService.saveUserSso(userUpdate);
				model.addAttribute("accion", "cambioPassword");
				notification.alert("1", "SUCCES", "Cambio de contraseña.");
			} catch (SSODataException e) {
				e.printStackTrace();
				model.addAttribute("accion", "recuperar");
				notification.alert("1", "ERROR", "Ocurrieron inconvenientes al momento de actualizar la contraseña.");
			}
		}

		model.addAttribute("notification", notification);
		return "password";
	}

	// Metodos de Apoyo
	private List<ApplicationClient> llenarListaAppVinculadas(UserSso us, String lista){
		List<ApplicationClient> result = new ArrayList<>();
		String[] items = null;
		applicationClientRef = null;
		setApplicationClientRef = new HashSet<>();

		Iterable<RoleApp> iter = us.getRoles();
		for (RoleApp roleApp : iter) {
			if(roleApp.getAplicationClient()!=null){
				roleApp.getAplicationClient().setUserSso(userSsoRef);
				result.add(roleApp.getAplicationClient());
			}else{
				us.removeRoleApp(roleApp);
				try {
					userSsoService.saveUserSso(us);
				} catch (SSODataException e) {
					e.printStackTrace();
				}
			}
		}

		items = lista.split(",");
		for (int i = 0; i < items.length; i++) {
			String string = items[i];
			if (!string.equals("")) {
				applicationClientRef = new ApplicationClient();
				applicationClientRef = applicationClientService.getAplicacionClienteById(new Long(string.trim()));
				applicationClientRef.setUserSso(us);
				result.add(applicationClientRef);
			}
		}
		// Eliminar aplicaciones repetidas de la lista
		setApplicationClientRef.addAll(result);
		result.clear();
		result.addAll(setApplicationClientRef);
		return result;
	}

	private List<String> obtenerTipoRoles() {
		List<String> tiporoles = new ArrayList<>();
		// Lista de tipos de roles
		tiporoles.add(RolType.ADMINISTRADOR_SEGURIDAD.toString());
		tiporoles.add(RolType.APLICACION.toString());

		return tiporoles;
	}

	public boolean sendEmailToken(HttpServletRequest request, String token, String email) {
		JavaMailSender emailSender = mailSenderService.getJavaMailSender();
		try {
			String uri = request.getRequestURL() + "/" + token;

			mailSenderService.sendEmailToken(email, uri, emailSender);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailNewUser(UserSso userSso, String passwordTemp) {
		JavaMailSender emailSender = mailSenderService.getJavaMailSender();
		try {
			mailSenderService.sendEmailNewUser(userSso, passwordTemp, emailSender);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailToken(String token, String destino) {
		JavaMailSender emailSender = mailSenderService.getJavaMailSender();
		try {
			String uri = "http://localhost:8080/password/recuperar/" + token;
			mailSenderService.sendEmailToken(destino, uri, emailSender);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
		// Se invalida la session
		HttpSession session = request.getSession();
		session.invalidate();

		response.setContentType("text/html");
		Cookie[] cookies = request.getCookies();

		// Se eliminan todas las cookies
		if (cookies != null) {

			for (int i = 0; i < cookies.length; i++) {

				Cookie cookie = cookies[i];
				cookies[i].setValue(null);
				cookies[i].setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

	public Boolean validateApp(ApplicationClient app){
		if (app.getId() == 0) {
			return false;
		}

		return true;
	}

}
