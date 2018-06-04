
package com.stf.oauth.admin.commons.scheduler.job;

import com.stf.oauth.admin.commons.helper.ActiveDirectoryHelper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * <p>
 * Tarea de consulta a Bd de la frecuiencia de ejecución de la consulta de
 * estados de usuarios en Active dirtectory
 * </p>
 * 
 * @author SsoTeam
 */
public class UpdateStatusUserJob implements Job {

	private static Logger _log = LoggerFactory.getLogger(UpdateStatusUserJob.class);

	@Value("${spring.datasource.url}")
	private String urlDB;

	@Value("${spring.datasource.username}")
	private String userDB;

	@Value("${spring.datasource.password}")
	private String passwordDB;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassDB;

	@Value("${domainAD}")
	private String domainAD;

	@Value("${userAdminLDAP}")
	private String userAdminLDAP;

	@Value("${claveAdminLDAP}")
	private String claveAdminLDAP;

	@Value("${canonicalName}")
	private String canonicalName;

	@Value("${baseAD}")
	private String baseAD;

	public UpdateStatusUserJob() {
	}

	/**
	 * <p>
	 * Metodoq ue es invocado en función de la planificación efectuada por
	 * <code>{@link org.quartz.Scheduler}</code> cuando un
	 * <code>{@link org.quartz.Trigger}</code> es disparado y esta asociado con la
	 * tarea <code> ReadDBJobPlanning Job</code>.
	 * </p>
	 * 
	 * @throws JobExecutionException
	 * 
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// Invocar metodo para actualizar estados
		this.actualizarEstadoUser();
		JobKey jobKey = context.getJobDetail().getKey();
		_log.info("--->  ReadDBJobPlanning consultar BD: " + jobKey + " se ejecuto: " + new Date());
	}

	private Boolean actualizarEstadoUser() {
		Boolean result = Boolean.FALSE;
		ActiveDirectoryHelper activeDirectoryHelper;
		NamingEnumeration<SearchResult> resultUsers;
		String userLdap, attValue, userAccountControl;

		_log.info("Iniciando Autenticacion con Active Directory...");

		String domain = domainAD;
		String userAdminLdap = userAdminLDAP;
		String claveAdminLdap = claveAdminLDAP;
		String servidor = "CN=" + userAdminLdap + "," + canonicalName;
		String baseAd = baseAD;

		// Creando instancia del Active Directory con usuario Administrador
		activeDirectoryHelper = new ActiveDirectoryHelper();
		activeDirectoryHelper.cargarContextoLdap(userAdminLdap, claveAdminLdap, domain, servidor);

		try {
			// Buscando todos los usuarios del Active directory
			resultUsers = activeDirectoryHelper.searchAllUser(baseAd);

			while (resultUsers.hasMore()) {
				SearchResult rs = (SearchResult) resultUsers.next();
				Attributes attrs = rs.getAttributes();

				attValue = attrs.get("samaccountname").toString();
				_log.info("Usuario extraido Ldap: " + attValue.substring(attValue.indexOf(":") + 1).trim());
				userLdap = attValue.substring(attValue.indexOf(":") + 1).trim();

				attValue = attrs.get("userAccountControl").toString();
				_log.info("Control de Cuenta (valor flag activo): " + attValue.substring(attValue.indexOf(":") + 1));
				userAccountControl = attValue.substring(attValue.indexOf(":") + 1).trim();

				if (actualizarBD(userLdap, userAccountControl)) {
					_log.info("Usuario: " + userLdap + " Actualizado en BD...");
				} else {
					_log.info("Usuario: " + userLdap + " Existe en Active Directory y NO en BD...");
				}
			}
			// Closing LDAP Connection
			activeDirectoryHelper.closeLdapConnection();

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private Boolean actualizarBD(String usuario, String estadoUsuarioLdap) {
		Boolean result = Boolean.FALSE;
		Connection conexion = null;
		int valorActivo = -1;
		int actualizado = 0;

		try {
			Class.forName(driverClassDB);
			conexion = DriverManager.getConnection(urlDB, userDB, passwordDB);
			Statement s = conexion.createStatement();
			valorActivo = (this.evaluarEstadoLdap(estadoUsuarioLdap)) ? 1 : 0; // 1 es Verdadero; 0 es falso
			actualizado = s.executeUpdate(
					"UPDATE oauth_users set enabled = " + valorActivo + " where username = '" + usuario.trim() + "'");
			if (actualizado != 0)
				result = Boolean.TRUE;

		} catch (Exception e) {
			// TODO validar mensaje o propagacion de excepcion
			e.printStackTrace();
		} finally {
			if (conexion != null) {
				try {
					conexion.close();
				} catch (SQLException e) {
					// TODO validar mensaje o propagacion de excepcion
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * Permite hacer el mapeo de los valores decimales que retorna la flag
	 * userAccountControl del Active Directory.
	 * 
	 * @param estadoUsuarioLdap
	 * @return
	 */
	private Boolean evaluarEstadoLdap(String estadoUsuarioLdap) {
		Boolean result = Boolean.FALSE;
		Integer valor = new Integer(estadoUsuarioLdap);

		switch (valor) {
		case 66048:
			result = Boolean.TRUE; // Enabled, Password Doesn’t Expire
			break;

		case 544:
			result = Boolean.TRUE; // Enabled,Password Not Required
			break;

		case 512:
			result = Boolean.TRUE; // Normal Account --Enabled--
			break;

		case 514:
			result = Boolean.FALSE;// Disabled Account
			break;

		case 66050:
			result = Boolean.FALSE;// Disabled, Password Doesn’t Expire
			break;

		case 66082:
			result = Boolean.FALSE;// Disabled, Password Doesn’t Expire & Not Required
			break;

		case 546:
			result = Boolean.FALSE;// Disabled, Password Not Required
			break;

		case 4096:
			result = Boolean.FALSE;// WORKSTATION_TRUST_ACCOUNT
			break;

		case 532480:
			result = Boolean.FALSE;// Domain controller
			break;
		}
		return result;
	}

}
