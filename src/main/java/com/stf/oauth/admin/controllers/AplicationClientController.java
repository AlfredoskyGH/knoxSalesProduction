package com.stf.oauth.admin.controllers;

import com.stf.oauth.admin.commons.RolType;
import com.stf.oauth.admin.commons.bean.Notification;
import com.stf.oauth.admin.commons.exception.SSODataException;
import com.stf.oauth.admin.entities.ApplicationClient;
import com.stf.oauth.admin.entities.FunctionalityRole;
import com.stf.oauth.admin.entities.RoleApp;
import com.stf.oauth.admin.services.ApplicationClientService;
import com.stf.oauth.admin.services.FunctionalityRoleService;
import com.stf.oauth.admin.services.RoleAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author team ssoinnova4j Clase controladora en Sping MVC de la gestion de las
 *         aplicaciones sus roles y funcionalidades. 17/03/2018
 */

@Controller
public class AplicationClientController {

	/**
	 * atributos de apoyo.
	 */
	private List<ApplicationClient> listApplicationClientRef;

	private ApplicationClient applicationClientRef;
	private RoleApp roleAppRef;
	private FunctionalityRole functionalityRoleRef;
	private Notification notification;

	// Services
	private ApplicationClientService aplicacionClienteService;
	private RoleAppService roleAppService;
	private FunctionalityRoleService functionalityRoleService;

	@Autowired
	public void setAplicacionClienteService(ApplicationClientService aplicacionClienteService) {
		this.aplicacionClienteService = aplicacionClienteService;
	}

	@Autowired
	public void setRoleAppService(RoleAppService roleAppService) {
		this.roleAppService = roleAppService;
	}

	@Autowired
	public void setFunctionalityRoleService(FunctionalityRoleService functionalityRoleService) {
		this.functionalityRoleService = functionalityRoleService;
	}

	// APLICACION
	/**
	 * Metodo para listar todos las aplicaciones en BD.
	 *
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = "/sso/aplicaciones", method = RequestMethod.GET)
	public String listAplicaciones(Model model) {
		listApplicationClientRef = new ArrayList<>();
		listApplicationClientRef = aplicacionClienteService.listAplicacionesClientes();

		model.addAttribute("aplicaciones", listApplicationClientRef);
		if (notification!= null){
			if(notification.getMessage().contains("rol(es)")){
				model.addAttribute("notification", notification);// vincularaplicaciones
			}
			notification = null;
		}
		return "aplicaciones";
	}

	/**
	 * Crear una nueva Aplicacion.
	 *
	 * @param model
	 * @return
	 */
	@RequestMapping("/sso/aplicacion/new")
	public String newAplicacionCliente(Model model) {
		String mensaje = null;
		applicationClientRef = new ApplicationClient();
		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("errormsj", mensaje);
		return "aplicacionform";
	}

	/**
	 * Guardar una Aplicacion en la Fuente de Datos
	 *
	 * @param aplicacion
	 * @return
	 */
	@RequestMapping(value = "/sso/aplicacion", method = RequestMethod.POST)
	public String saveAplicacionCliente(ApplicationClient aplicacion, Model model) {
		applicationClientRef = new ApplicationClient();
		notification = new Notification();
		try {
			//Set de los campos estandar en base de datos
			aplicacion.setResourceIds(null);
			aplicacion.setScope("read,write,trust");
			aplicacion.setAuthorizedGrantTypes("password,authorization_code,refresh_token");
			aplicacion.setAuthorities("ROLE_USER,ROLE_ADMIN");
			aplicacion.setAdditionalInformation(null);
			aplicacion.setAutoApproveScope("true");
			aplicacion.setWebServerRedirectUri(aplicacion.getWebServerRedirectUri());
			applicationClientRef = aplicacionClienteService.saveAplicacionCliente(aplicacion);

		} catch (SSODataException e) {
			if (e.getCodigoErr().equals("DUPKEY")) {
				notification.alert("1", "ERROR",
						"Identificador de Aplicación Ya EXISTE: " + aplicacion.getClientId());
			} else {
				notification.alert("1", "ERROR", e.getMensaje());
			}
			model.addAttribute("aplicacion", aplicacion);
			model.addAttribute("notification", notification);
			return "aplicacionform";
		}
		notification.alert("1", "SUCCESS",
				"Aplicación: ".concat(applicationClientRef.getClientId()).concat(" Actualizada de forma EXITOSA"));
		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("notification", notification);
		return "aplicacionshow";
	}

	/**
	 * Editar una aplicacion especifica
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/sso/aplicacion/edit/{id}")
	public String editAplicacionCliente(final @PathVariable Long id, Model model) {
		applicationClientRef = new ApplicationClient();
		applicationClientRef = aplicacionClienteService.getAplicacionClienteById(id);

		model.addAttribute("aplicacion", applicationClientRef);
		return "aplicacionform";
	}

	/**
	 * Eliminar una aplicacion por el ID
	 * 
	 * @param id
	 * @return Long
	 */
	@RequestMapping("/sso/aplicacion/delete/{id}")
	public ModelAndView deleteAplicacionCliente(final @PathVariable Long id) {
		applicationClientRef = new ApplicationClient();
		applicationClientRef = aplicacionClienteService.getAplicacionClienteById(id);
		ModelAndView modelAndView = new ModelAndView();
		notification = new Notification();

		if(applicationClientRef.getRoleApps().size()>0){
			notification.alert("1","ERROR" ,"La aplicación se encuentra actualmente vinculada a " + applicationClientRef.getRoleApps().size() + " rol(es).");
		}else{
			aplicacionClienteService.deleteAplicacionCliente(id);
		}

		modelAndView.setViewName("redirect:/sso/aplicaciones");
		modelAndView.addObject("notification", notification);
		return modelAndView;
	}

	// APLICACION-ROLES
	/**
	 * Metodo que permite consultar una aplicacion y la gestion de sus roles.
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/sso/aplicacion/{id}/ver")
	public String verAplicacionRoles(final @PathVariable Long id, Model model) {
		
		applicationClientRef = new ApplicationClient();

		applicationClientRef = aplicacionClienteService.getAplicacionClienteById(id);
		
		model.addAttribute("tiporoles", this.obtenerTipoRoles());
		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("rol", new RoleApp(new ApplicationClient(id)));
		model.addAttribute("roles", applicationClientRef.getRoleApps());

		return "aplicacionrolform";
	}

	/**
	 * Modificar un rol de una aplicacion
	 * @param rol
	 * @param model
	 *
	 * @return
	 */
	@RequestMapping(value = "/sso/aplicacion/rol/guardar", method = RequestMethod.POST)
	public String saveRolEditAplicacion(final RoleApp rol, Model model) {
		roleAppRef = new RoleApp();
		applicationClientRef = null;
		notification = null;
		try {
			roleAppRef = roleAppService.saveRol(rol);
			applicationClientRef = new ApplicationClient();
			applicationClientRef = aplicacionClienteService.getAplicacionClienteById(rol.getAplicationClient().getId());
		} catch (SSODataException e) {
			notification = new Notification();
			if (e.getCodigoErr().equals("DUPKEY")) {
				notification.alert("1", "ERROR","Nombre de Rol:"
						.concat(rol.getName()).concat(", Ya esta REGISTRADo para la Aplicación"));
			}else {
				notification.alert("1","ERROR" ,e.getMensaje());
			}
			applicationClientRef = new ApplicationClient();
			applicationClientRef = aplicacionClienteService
					.getAplicacionClienteById(rol.getAplicationClient().getId());
		}
				
		model.addAttribute("tiporoles", this.obtenerTipoRoles());
		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("roles", applicationClientRef.getRoleApps());
		
		if (notification != null) {
			model.addAttribute("notification", notification);
			model.addAttribute("rol", rol);
			return "roledit";
		}else {
			notification = new Notification();
			notification.alert("1", "SUCCESS",
					"Rol: ".concat(roleAppRef.getName()).concat(" Actualizado de forma EXITOSA"));
			model.addAttribute("rol", new RoleApp(applicationClientRef));
			model.addAttribute("notification", notification);
			return "aplicacionrolform";
		}
	}
	
	/**
	 * Crear un rol de una aplicacion
	 * 
	 * @param rol
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/sso/aplicacion/rol/crear", method = RequestMethod.POST)
	public String saveRolAplicacionCliente(final RoleApp rol, Model model) {
		roleAppRef = new RoleApp();
		applicationClientRef = null;
		notification = null;
		try {
			roleAppRef = roleAppService.saveRol(rol);
			applicationClientRef = new ApplicationClient();
			applicationClientRef = aplicacionClienteService.getAplicacionClienteById(rol.getAplicationClient().getId());
		} catch (SSODataException e) {
			notification = new Notification();
			if (e.getCodigoErr().equals("DUPKEY")) {
				notification.alert("1", "ERROR","Nombre de Rol:"
						.concat(rol.getName()).concat(", Ya esta REGISTRADO para la Aplicación"));
			}else {
				notification.alert("1","ERROR" ,e.getMensaje());
			}
			applicationClientRef = new ApplicationClient();
			applicationClientRef = aplicacionClienteService
					.getAplicacionClienteById(rol.getAplicationClient().getId());
		}

		if (notification!=null){
			model.addAttribute("rol", rol);
		}else{
			model.addAttribute("rol", new RoleApp(applicationClientRef));
			notification = new Notification();
			notification.alert("1", "SUCCESS",
					"Rol: ".concat(roleAppRef.getName()).concat(" Creado de forma EXITOSA"));

		}

		model.addAttribute("tiporoles", this.obtenerTipoRoles());
		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("roles", applicationClientRef.getRoleApps());
		model.addAttribute("notification", notification);
		return "aplicacionrolform";
	}

	/**
	 * Eliminar un rol de una aplicación por el ID
	 * 
	 * @param id
	 * @return Long
	 */
	@RequestMapping("/sso/aplicacion/{appId}/rol/delete/{id}")
	public String deleteRolAplicacionCliente(final @PathVariable Long appId, @PathVariable Long id, Model model) {
		String mensaje = null;
		applicationClientRef = new ApplicationClient();
		roleAppRef = new RoleApp();
		notification = new Notification();

		applicationClientRef = aplicacionClienteService.getAplicacionClienteById(appId);

		try {
			roleAppRef = roleAppService.getRolById(id);

			if(roleAppRef.getUserSsos().size()>0){
				notification.alert("1","ERROR" ,"El rol se encuentra actualmente asignado a " + roleAppRef.getUserSsos().size() + " usuario(s).");
			}else{
				applicationClientRef.removeRoleApp(roleAppRef);
				roleAppService.deleteRol(roleAppRef.getId());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		model.addAttribute("tiporoles", this.obtenerTipoRoles());
		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("rol", new RoleApp(applicationClientRef));
		model.addAttribute("notification", notification);
		model.addAttribute("roles", applicationClientRef.getRoleApps());

		return "aplicacionrolform";
	}

	/**
	 * Editar un rol de una aplicación por el ID
	 * 
	 * @param id
	 * @return Long
	 */
	@RequestMapping("/sso/aplicacion/{appId}/rol/edit/{id}")
	public String editarRolAplicacionCliente(@PathVariable Long appId, @PathVariable Long id, Model model) {

		applicationClientRef = new ApplicationClient();
		roleAppRef = new RoleApp();

		roleAppRef = roleAppService.getRolById(id);
		applicationClientRef = aplicacionClienteService.getAplicacionClienteById(appId);
		roleAppRef.setAplicationClient(applicationClientRef);
		
		model.addAttribute("tiporoles", this.obtenerTipoRoles());
		model.addAttribute("rol", roleAppRef);

		return "roledit";
	}

	// APLICACION-ROLES-FUNCIONALIDADES
	
	/**
	 * Metodo que permite consultar un rol de una aplicación 
	 * y la gestion de sus roles.
	 * 
	 * @param id
	 * @param rolId
	 * @param model
	 * @return
	 */
	@RequestMapping("/sso/aplicacion/{id}/rol/{rolId}/ver")
	public String verFuncionalidadesRolAplicacion(@PathVariable Long id, @PathVariable Long rolId, Model model) {

		applicationClientRef = new ApplicationClient();
		roleAppRef = new RoleApp();

		applicationClientRef = aplicacionClienteService.getAplicacionClienteById(id);
		roleAppRef = roleAppService.getRolById(rolId);

		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("rol", roleAppRef);
		model.addAttribute("funcionalidad", new FunctionalityRole(roleAppRef));
		model.addAttribute("funcionalidades", roleAppRef.getFunctionalityRoles());

		return "aplicacionrolfuncionalidadform";
	}

	/**
	 * Guardar la funcionalidad de un rol de una aplicacion
	 * 
	 * @param funcionalidad
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/sso/aplicacion/rol/funcionalidad/guardar", method = RequestMethod.POST)
	public String saveFuncionalidadRolesApp(final FunctionalityRole funcionalidad, Model model) {
		functionalityRoleRef = new FunctionalityRole();
		roleAppRef = null;
		applicationClientRef = null;
		notification = null;
		try {
			functionalityRoleRef = functionalityRoleService.saveFuncionalidad(funcionalidad);
			roleAppRef = new RoleApp();
			roleAppRef = roleAppService.getRolById(funcionalidad.getRoleApp().getId());
			applicationClientRef = new ApplicationClient();
			applicationClientRef = aplicacionClienteService
					.getAplicacionClienteById(funcionalidad.getRoleApp().getAplicationClient().getId());
		} catch (SSODataException e) {
			notification = new Notification();
			if (e.getCodigoErr().equals("DUPKEY")) {
				notification.alert("1", "ERROR","Nombre de Funcionalidad:"
						.concat(funcionalidad.getName()).concat(", Ya esta REGISTRADA para el Rol"));
			}else {
				notification.alert("1","ERROR" ,e.getMensaje());
			}
			roleAppRef = new RoleApp();
			roleAppRef = roleAppService.getRolById(funcionalidad.getRoleApp().getId());
			applicationClientRef = new ApplicationClient();
			applicationClientRef = aplicacionClienteService
					.getAplicacionClienteById(funcionalidad.getRoleApp().getAplicationClient().getId());
		}
		model.addAttribute("rol", roleAppRef);
		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("funcionalidades", roleAppRef.getFunctionalityRoles());
		
		if (notification != null) {
			model.addAttribute("notification", notification);
			model.addAttribute("funcionalidad", funcionalidad);
			return "funcionalidadedit";
		}else {
			notification = new Notification();
			notification.alert("1", "SUCCESS",
					"Rol: ".concat(roleAppRef.getName()).concat(" Actualizado de forma EXITOSA"));
			model.addAttribute("funcionalidad", new FunctionalityRole(roleAppRef));
			model.addAttribute("notification", notification);
			return "aplicacionrolfuncionalidadform";
		}
	}
	
	/**
	 * Registrar una funcionalidad de un rol de una aplicacion
	 * 
	 * @param funcionalidad
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/sso/aplicacion/rol/funcionalidad/crear", method = RequestMethod.POST)
	public String saveFuncionalidadRol(final FunctionalityRole funcionalidad, Model model) {
		functionalityRoleRef = new FunctionalityRole();
		roleAppRef = null;
		applicationClientRef = null;
		notification = null;
		try {
			functionalityRoleRef = functionalityRoleService.saveFuncionalidad(funcionalidad);
			roleAppRef = new RoleApp();
			roleAppRef = roleAppService.getRolById(funcionalidad.getRoleApp().getId());
			applicationClientRef = new ApplicationClient();
			applicationClientRef = aplicacionClienteService
					.getAplicacionClienteById(funcionalidad.getRoleApp().getAplicationClient().getId());
		} catch (SSODataException e) {
			notification = new Notification();
			if (e.getCodigoErr().equals("DUPKEY")) {
				notification.alert("1", "ERROR","Nombre de Funcionalidad:"
						.concat(funcionalidad.getName()).concat(", Ya esta REGISTRADA para el Rol"));
			}else {
				notification.alert("1","ERROR" ,e.getMensaje());
			}
			roleAppRef = new RoleApp();
			roleAppRef = roleAppService.getRolById(funcionalidad.getRoleApp().getId());
			applicationClientRef = new ApplicationClient();
			applicationClientRef = aplicacionClienteService
					.getAplicacionClienteById(funcionalidad.getRoleApp().getAplicationClient().getId());
			model.addAttribute("rol", roleAppRef);
			model.addAttribute("funcionalidad", funcionalidad);
		}
		if (notification == null) {
			notification = new Notification();
			notification.alert("1", "SUCCESS",
					"Funcionalidad: ".concat(functionalityRoleRef.getName()).concat(" Creada de forma EXITOSA"));
			model.addAttribute("rol", roleAppRef);
			model.addAttribute("funcionalidad", new FunctionalityRole(roleAppRef));
		} 
		
		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("funcionalidades", roleAppRef.getFunctionalityRoles());
		model.addAttribute("notification", notification);
		return "aplicacionrolfuncionalidadform";
	}

	/**
	 * Eliminar una funcionalidad de un rol de una aplicación
	 * 
	 * @param id
	 * @return Long
	 */
	@RequestMapping("/sso/aplicacion/{appId}/rol/{rolId}/funcionalidad/delete/{id}")
	public String deleteRolAplicacionCliente(@PathVariable Long appId, @PathVariable Long rolId, @PathVariable Long id,
                                             Model model) {
		String mensaje = null;
		applicationClientRef = new ApplicationClient();
		functionalityRoleRef = new FunctionalityRole();
		roleAppRef = new RoleApp();

		try {
			functionalityRoleRef = functionalityRoleService.getFuncionalidadById(id);
			roleAppRef = roleAppService.getRolById(rolId);
			roleAppRef.removeFunctionalityRole(functionalityRoleRef);
			applicationClientRef = aplicacionClienteService.getAplicacionClienteById(appId);
			roleAppRef.setAplicationClient(applicationClientRef);
			roleAppRef = roleAppService.saveRol(roleAppRef);

		} catch (SSODataException e) {
			if (e.getCodigoErr().equals("DUPKEY")) {
				mensaje = new String("Falla al momento de Eliminar la funcionalidad: " + id);
			}
		}
		model.addAttribute("aplicacion", applicationClientRef);
		model.addAttribute("rol", roleAppRef);
		model.addAttribute("errormsj", mensaje);
		model.addAttribute("funcionalidad", new FunctionalityRole(roleAppRef));
		model.addAttribute("funcionalidades", roleAppRef.getFunctionalityRoles());

		return "aplicacionrolfuncionalidadform";
	}

	/**
	 * Editar una funcionalidad de una aplicación por el ID
	 * 
	 * @param id
	 * @return Long
	 */
	@RequestMapping("/sso/aplicacion/{appId}/rol/{rolId}/funcionalidad/edit/{id}")
	public String editarFuncionalidadRolApp(@PathVariable Long appId, @PathVariable Long rolId, @PathVariable Long id,
                                            Model model) {
		functionalityRoleRef = new FunctionalityRole();
		roleAppRef = new RoleApp();
		applicationClientRef = new ApplicationClient();

		functionalityRoleRef = functionalityRoleService.getFuncionalidadById(id);
		roleAppRef = roleAppService.getRolById(rolId);
		applicationClientRef = aplicacionClienteService.getAplicacionClienteById(appId);
		roleAppRef.setAplicationClient(applicationClientRef);
		functionalityRoleRef.setRoleApp(roleAppRef);
		model.addAttribute("funcionalidad", functionalityRoleRef);

		return "funcionalidadedit";
	}
	
	//Metodo de apoyo
	private List<String> obtenerTipoRoles(){
		List<String> tiporoles = new ArrayList<>();
		//Lista de tipos de roles
		tiporoles.add(RolType.ADMINISTRADOR_SEGURIDAD.toString());
		tiporoles.add(RolType.APLICACION.toString());
		
		return tiporoles;
	}

}