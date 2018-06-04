package com.knox.oauth.admin.controllers;

import com.knox.oauth.admin.commons.bean.BulkLoadCsv;
import com.knox.oauth.admin.commons.bean.Notification;
import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.commons.helper.CargaMasivaHelper;
import com.knox.oauth.admin.entities.ApplicationClient;
import com.knox.oauth.admin.services.ApplicationClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@MultipartConfig(fileSizeThreshold=1024*1024, maxFileSize=1024*1024*5*5, maxRequestSize=1024*1024*5*5)
public class BulkLoadCsvController {

    private Notification notification;
    private CargaMasivaHelper cargaMasivaHelper;
    private ApplicationClientService applicationClientService;
    
	/**
	 * @return the cargaMasivaHelper
	 */
	public CargaMasivaHelper getCargaMasivaHelper() {
		return cargaMasivaHelper;
	}

	/**
	 * @param cargaMasivaHelper the cargaMasivaHelper to set
	 */
	@Autowired
	public void setCargaMasivaHelper(CargaMasivaHelper cargaMasivaHelper) {
		this.cargaMasivaHelper = cargaMasivaHelper;
	}
	
	@Autowired
	public void setApplicationClientService(ApplicationClientService applicationClientService) {
		this.applicationClientService = applicationClientService;
	}

	/* REQUEST MAPPING */
	@RequestMapping(value = "/sso/cargar/usuarios", method = RequestMethod.GET)
	public String loadFile(Model model) {
		model.addAttribute("file", null);
		model.addAttribute("notification", null);
		return "cargarusuarios";
	}

	@RequestMapping(value = "/sso/procesar/archivo", method = RequestMethod.GET)
	public String refreshFile() {
		return "redirect:/sso/cargar/usuarios";
	}

	@RequestMapping(value = "/sso/procesar/archivo", method = RequestMethod.POST)
	public String importFile(@RequestParam("file") MultipartFile archivo, Model model)
			throws IOException, SSODataException {
		BulkLoadCsv file;
		List<ApplicationClient> listApplicationClient = new ArrayList<>();
		notification = new Notification();
		
		file = new BulkLoadCsv();
		file.setNameFile(archivo.getOriginalFilename());

		if (file.getNameFile().contains(".csv")) {
			listApplicationClient = applicationClientService.listAplicacionesClientes();
			
			//Invocar a clase helper que se encrga de procesar el archivo de carga masiva
			file = cargaMasivaHelper.procesarCargaMasiva(archivo, listApplicationClient, file);

			if (file.getUsersLoad() != null) {
				if (file.getTotal() == file.getProcessed()) {
					notification.alert("1", "SUCCESS", "Se procesaron los registros correctamente.");
				} else if (file.getTotal() == file.getNoProcessed()) {
					notification.alert("1", "ERROR", "No se proceso ningun registro del archivo.");
				} else {
					notification.alert("1", "WARNING", "Algunos registros no se procesaron.");
				}
			}
		} else {
			notification.alert("1", "ERROR", "Solo se permiten archivos .csv.");
		}
		model.addAttribute("file", file);
		model.addAttribute("notification", notification);

		return "cargarusuarios";
	}
}