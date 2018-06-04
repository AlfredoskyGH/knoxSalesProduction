package com.stf.oauth.admin.controllers;

import com.stf.oauth.admin.commons.bean.Notification;
import com.stf.oauth.admin.commons.exception.SSODataException;
import com.stf.oauth.admin.entities.RoleApp;
import com.stf.oauth.admin.services.RoleAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author team ssoinnova4j
 * 
 * Clase controladora del funcionamiento y gestión de la entidad rol.
 * 18/03/2018
 */

@Controller
public class RoleAppController {

    private RoleApp roleApp;
    private RoleAppService rolService;
    private Notification notification;

    @Autowired
    public void setRolService(RoleAppService rolService) {
        this.rolService = rolService;
    }

    /**
     * Metodo para listar todos los roles en BD.
     *
     * @param model
     * @return String
     */
    @RequestMapping(value = "/sso/roles", method = RequestMethod.GET)
    public String list(Model model) {
    	//TODO CONEXION AL ACTIVE DIRECTORY
        model.addAttribute("roles", rolService.listRoles());
        return "roles";
    }


    /**
     * Consultar un rol especifico por ID     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/sso/rol/{id}")
    public String showRol(@PathVariable Long id, Model model) {
        model.addAttribute("rol", rolService.getRolById(id));
        return "rolshow";
    }

    
    /**
     * Editar un rol especifico por ID
     * 
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/sso/rol/edit/{id}")
    public String editRol(@PathVariable Long id, Model model) {
        model.addAttribute("rol", rolService.getRolById(id));
        return "rolform";
    }

    /**
     * Crear un nuevo Rol.
     *
     * @param model
     * @return
     */
    @RequestMapping("/sso/rol/new")
    public String newRol(Model model) {
        model.addAttribute("rol", new RoleApp());
        return "rolform";
    }

    /**
     * Guardar un rol en la Fuente de Datos
     *
     * @param rol
     * @return
     */
    @RequestMapping(value = "/sso/rol", method = RequestMethod.POST)
    public String saveRol(RoleApp rol) {
    	try {
			rolService.saveRol(rol);
		} catch (SSODataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "redirect:/sso/rol/" + rol.getId();
    }

    /**
     * Eliminar un rol por el ID
     * 
     * @param id
     * @return String
     */
    @RequestMapping("/sso/rol/delete/{id}")
    public String deleteRol(@PathVariable Long id, Model model) {
        roleApp = rolService.getRolById(id);
        notification = new Notification();
        if(roleApp.isAdmin()){
            notification.alert("1", "ERROR",
                    "No se puede eliminar un rol Administrador.");
        }else{
            rolService.deleteRol(id);

            notification.alert("1", "SUCCESS",
                    "El Rol " + roleApp.getName() + " se ha eliminado correctamente.");
        }

        model.addAttribute("roles", rolService.listRoles());
        model.addAttribute("notification", notification);
        return "roles";

    }

}
