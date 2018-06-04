package com.knox.oauth.admin.commons.helper;

import com.knox.oauth.admin.commons.bean.BulkLoadCsv;
import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.ApplicationClient;
import com.knox.oauth.admin.entities.FunctionalityRole;
import com.knox.oauth.admin.entities.RoleApp;
import com.knox.oauth.admin.entities.UserSso;
import com.knox.oauth.admin.services.ApplicationClientService;
import com.knox.oauth.admin.services.FunctionalityRoleService;
import com.knox.oauth.admin.services.RoleAppService;
import com.knox.oauth.admin.services.UserSsoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Clase que permite el apoyo en el procesamiento del archivo de carga masivca
 * de usuarios - roles.
 * 
 * @author team ssoinnova4j
 *
 */
@Component
public class CargaMasivaHelper {

	// Services
	private UserSsoService userSsoService;
	private ApplicationClientService applicationClientService;
	private RoleAppService roleAppService;
	private FunctionalityRoleService functionalityRoleService;
	
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
	public void setFunctionalityRoleService(FunctionalityRoleService functionalityRoleService) {
		this.functionalityRoleService = functionalityRoleService;
	}

	public BulkLoadCsv procesarCargaMasiva(MultipartFile archivo, List<ApplicationClient> listApplications, BulkLoadCsv archivoBase) {
		BufferedReader br;
		try {
			String line;
			InputStream is = archivo.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			int numLinea = 0;
			while ((line = br.readLine()) != null) {
				numLinea = numLinea + 1;

				line.replace(";", "");
				String[] campos = line.split(",");

				if (campos.length == 9) {
					if (validateData(campos, numLinea, archivoBase, listApplications)) {
						archivoBase.processed();
						archivoBase.totalAcumulator();
						continue;
					} else {
						archivoBase.noProcessed();
						archivoBase.totalAcumulator();
						continue;
					}
				} else {
					archivoBase.setValidateFormat(Boolean.FALSE);
					archivoBase.setErrorFormat("Linea " + numLinea + ": no posee la cantidad de columnas correctas. ");
					continue;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return archivoBase;
	}

	public boolean validateData(String[] campos, int numLinea, BulkLoadCsv file,
			List<ApplicationClient> listApplications) {
		// Definicion de campos
		String usuario = campos[0].trim();
		String nombre = campos[1].trim();
		String apellido = campos[2].trim();
		String email = campos[3].trim();
		String estatus = campos[4].trim();
		String activoHasta = campos[5].trim();
		Date activoHastaDate = null;
		String aplicacion = campos[6].trim();
		String rol = campos[7].trim();
		String funcionalidad = campos[8].trim().replace(";", "");

		// Definicion de objetos
		UserSso user = new UserSso();
		ApplicationClient aplication = new ApplicationClient();
		RoleApp role = new RoleApp();
		FunctionalityRole functionality = new FunctionalityRole();

		// Validacion de datos
		// Usuario
		if (StringUtils.isEmpty(usuario)) {
			file.addErrorField("linea " + numLinea + " [Campo 1] : Usuario no puede estar vacio.");
			return Boolean.FALSE;
		} else {
			user = userSsoService.findUserSsoByUsername(usuario);
			if (user.getId() == null) {
				user = new UserSso();
				user.setUsername(usuario);
				user.setCreated(new Date(System.currentTimeMillis()));
			}
		}

		// Nombre
		if (StringUtils.isEmpty(nombre)) {
			file.addErrorField("linea " + numLinea + " [Campo 2] : Nombre no puede estar vacio.");
			return Boolean.FALSE;
		}

		// Apellido
		if (StringUtils.isEmpty(apellido)) {
			file.addErrorField("linea " + numLinea + " [Campo 3] : Apellido no puede estar vacio.");
			return Boolean.FALSE;
		}

		// Correo
		if (StringUtils.isEmpty(email)) {
			file.addErrorField("linea " + numLinea + " [Campo 4] : Correo no puede estar vacio.");
			return Boolean.FALSE;
		}

		// Estatus
		if (StringUtils.isEmpty(estatus)) {
			file.addErrorField("linea " + numLinea + " [Campo 5] : Estatus no puede estar vacio.");
			return Boolean.FALSE;
		} else {
			// formato estatus true or Boolean.FALSE
			if (!Boolean.parseBoolean(estatus)) {
				file.addErrorField("linea " + numLinea
						+ " [Campo 5] : Estatus debe venir en formato boolean [true o Boolean.FALSE].");
				return Boolean.FALSE;
			}
		}

		// Activo Hasta
		if (!StringUtils.isEmpty(activoHasta)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			// formato activoHasta DATE
			if (isValidFormat(activoHasta, sdf)) {
				try {
					activoHastaDate = sdf.parse(activoHasta);
				} catch (ParseException e) {
					file.addErrorField("linea " + numLinea + " Falla Inesperada...");
					return Boolean.FALSE;
				}
			} else {
				file.addErrorField("linea " + numLinea + " [Campo 6] : Fecha debe venir en formato [yyyy-mm-dd].");
				return Boolean.FALSE;
			}
		}

		// Aplicacion
		if (!StringUtils.isEmpty(aplicacion)) {
			aplication = getApplication(listApplications, aplicacion);
			if (aplication == null) {
				file.addErrorField("linea " + numLinea + " [Campo 7] : La aplicación con el identificador " + aplicacion
						+ " no se encuentra Registrada");
				return Boolean.FALSE;
			}
		}

		// Rol
		if (!StringUtils.isEmpty(rol)) {
			try {
				role = this.procesarRol(rol, aplication);
				// Luego de procesado el rol se relaciona con la aplicacion para actualizar
				aplication.addRoleApp(role);
				aplication = applicationClientService.saveAplicacionCliente(aplication);
			} catch (SSODataException e) {
				if (e.getCodigoErr().equals("DUPKEY")) {

				}
			} catch (Exception e) {

			}
		}

		// Funcionalidad
		if (!StringUtils.isEmpty(funcionalidad)) {
			try {
				functionality = this.procesarFuncionalidad(funcionalidad, role);
				// Luego de procesada la funcionalidad se relaciona con el rol para actualizar

				if(functionality.getId()==null){
					role.addFunctionalityRole(functionality);
					role = roleAppService.saveRol(role);
				}

			} catch (SSODataException e) {
				if (e.getCodigoErr().equals("DUPKEY")) {

				}
			} catch (Exception e) {

			}
		}


		// usuario
		try {
			user.setFirstname(nombre);
			user.setLastname(apellido);
			user.setEmail(email);
			user.setEnabled(Boolean.parseBoolean(estatus));
			user.setUserActiveUntil(activoHastaDate);

			if(user.getId()==null){
				user = userSsoService.saveUserSso(user);
			}

			user.addRoleApp(role);
			user = userSsoService.saveUserSso(user);

			file.addUsersLoad(user);
		} catch (SSODataException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
		return true;
	}

	/**
	 * Metodo que permite hacer el procesamiento de las funcionalidades
	 * 
	 * @param nombreFuncionalidad
	 * @param roleApp
	 * @return
	 */
	private FunctionalityRole procesarFuncionalidad(String nombreFuncionalidad, RoleApp roleApp) throws Exception {
		FunctionalityRole result = new FunctionalityRole();
		// Obtengo todos los roles asignados a la aplicacion
		Iterable<FunctionalityRole> iterFuncionality = roleApp.getFunctionalityRoles();
		for (FunctionalityRole functionalityRoleTemp : iterFuncionality) {
			if (functionalityRoleTemp.getName().trim().toUpperCase().equals(nombreFuncionalidad.trim().toUpperCase())) {
				result = functionalityRoleTemp;
				break;
			}
		}
		try {
			if (result.getId() == null) {
				result.setName(nombreFuncionalidad);
				result.setRoleApp(roleApp);
				result.setEnabled(true);
				result.setCreated(new Date(System.currentTimeMillis()));
				result = functionalityRoleService.saveFuncionalidad(result);
			}
		} catch (SSODataException e) {
			if (e.getCodigoErr().equals("DUPKEY")) {
				throw new Exception("Funcionalidad duplicada");
			}
		}
		return result;
	}

	/**
	 * Metodo que permite hacer el procesamiento de las roles
	 * 
	 * @param nombreRol
	 * @param applicationClient
	 * @return
	 */
	private RoleApp procesarRol(String nombreRol, ApplicationClient applicationClient) throws Exception {
		RoleApp result = new RoleApp();
		// Obtengo todos los roles asignados a la aplicacion
		Iterable<RoleApp> iterRoles = applicationClient.getRoleApps();
		for (RoleApp roleTemp : iterRoles) {
			if (roleTemp.getName().trim().toUpperCase().equals(nombreRol.trim().toUpperCase())) {
				result = roleTemp;
				break;
			}
		}
		try {
			if (result.getId() == null) {
				result.setName(nombreRol);
				result.setEnabled(true);
				result.setType("APLICACION");
				result.setCreated(new Date(System.currentTimeMillis()));
				result.setAplicationClient(applicationClient);
				result = roleAppService.saveRol(result);
			}
		} catch (SSODataException e) {
			if (e.getCodigoErr().equals("DUPKEY")) {
				throw new Exception("Funcionalidad duplicada");
			}
		}
		return result;
	}

	/**
	 * Validar el formato de la fecha que viene en el archivo
	 * 
	 * @param activoHasta
	 * @param sdf
	 * @return
	 */
	private Boolean isValidFormat(String activoHasta, SimpleDateFormat sdf) {
		Boolean result = Boolean.TRUE;
		Date date = null;
		try {
			date = sdf.parse(activoHasta);
			if (!activoHasta.equals(sdf.format(date))) {
				result = Boolean.FALSE;
			}
		} catch (ParseException ex) {
			result = Boolean.FALSE;
		}
		return result;
	}

	/**
	 * Ubicar una aplicación en una lista.
	 * @param listaApp
	 * @param client_id
	 * @return
	 */
	public ApplicationClient getApplication(List<ApplicationClient> listaApp, String client_id) {
		ApplicationClient result = null;
		for (ApplicationClient app : listaApp) {
			if (app.getClientId().equals(client_id)) {
				result = app;
			}
		}
		return result;
	}
}