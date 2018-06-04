package com.knox.oauth.admin.controllers;

import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.FunctionalityRole;
import com.knox.oauth.admin.services.FunctionalityRoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author team ssoinnova4j
 * 
 * Clase controladora de la gestión de la entidad funcionalidad.
 * 18/03/2018
 */

@Controller
public class FunctionalityRoleController {

    private FunctionalityRoleService funcionalidadService;

    @Autowired
    public void setFuncionalidadServiceService(FunctionalityRoleService funcionalidadService) {
        this.funcionalidadService = funcionalidadService;
    }

    /**
     * Metodo para listar todas las funcionalidades en BD.
     *
     * @param model
     * @return String
     */
    @RequestMapping(value = "/sso/funcionalidades", method = RequestMethod.GET)
    public String list(Model model) {
    	//TODO CONEXION AL ACTIVE DIRECTORY
        model.addAttribute("funcionalidades", funcionalidadService.listFuncionalidades());
        return "funcionalidades";
    }


    /**
     * Consultar una funcionalidad especifia por ID     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/sso/funcionalidad/{id}")
    public String showRol(@PathVariable Long id, Model model) {
        model.addAttribute("funcionalidad", funcionalidadService.getFuncionalidadById(id));
        return "funcionalidadshow";
    }

    /**
     * Editar una funcionalidad especifica por ID
     * 
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/sso/funcionalidad/edit/{id}")
    public String editFuncionalidad(@PathVariable Long id, Model model) {
        model.addAttribute("funcionalidad", funcionalidadService.getFuncionalidadById(id));
        return "funcionalidadform";
    }

    /**
     * Crear una nueva funcionalidad.
     *
     * @param model
     * @return
     */
    @RequestMapping("/sso/funcionalidad/new")
    public String newFuncionalidad(Model model) {
        model.addAttribute("funcionalidad", new FunctionalityRole());
        return "funcionalidadform";
    }

    /**
     * Guardar una funcionalidad en la Fuente de Datos
     *
     * @param funcionalidad
     * @return
     * @throws SSODataException 
     */
    @RequestMapping(value = "/sso/funcionalidad", method = RequestMethod.POST)
    public String saveFuncionalidad(FunctionalityRole funcionalidad) throws SSODataException {
    	funcionalidadService.saveFuncionalidad(funcionalidad);
        return "redirect:/sso/funcionalidad/" + funcionalidad.getId();
    }

    /**
     * Eliminar una funcionalidad por el ID
     * 
     * @param id
     * @return String
     */
    @RequestMapping("/sso/funcionalidad/delete/{id}")
    public String deleteFuncionalidad(@PathVariable Long id) {
    	funcionalidadService.deleteFuncionalidad(id);
        return "redirect:/sso/funcionalidades";
    }

}
