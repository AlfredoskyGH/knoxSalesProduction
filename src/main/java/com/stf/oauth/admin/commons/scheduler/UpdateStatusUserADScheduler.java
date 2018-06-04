package com.stf.oauth.admin.commons.scheduler;

import com.stf.oauth.admin.commons.scheduler.job.UpdateStatusUserJob;
import com.stf.oauth.admin.entities.Schedule;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Clase que permite ejecutar la consulta de los estados de los usuarios 
 * en Active Directory, para luego actualiza los mismo en BD.
 * 
 *  @author team ssoinnova4j
 */
public class UpdateStatusUserADScheduler {

	@Value("${spring.datasource.url}")
	private String urlDB;

	@Value("${spring.datasource.username}")
	private String userDB;

	@Value("${spring.datasource.password}")
	private String passwordDB;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassDB;

	public void run() throws Exception {
		Logger log = LoggerFactory.getLogger(UpdateStatusUserADScheduler.class);
		
		//Recupera de BD el Job Planificado
		Schedule schPlanificadoBD = this.recuperarScheduleToExecute();

		log.info("--- Inicializando Job Recuerrente de consulta en BD de Tarea Programada ---");

		// creamos referencia a scheduler (planificador)
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();

		JobDetail jobUpdateStatusUser = newJob(UpdateStatusUserJob.class).withIdentity(schPlanificadoBD.getName(), "group2")
				.build();

		log.info(schPlanificadoBD.getExpression());
		
		// Tarea planificada a ser ejecutada cada dos (2) minutos.
		CronTrigger trigger = newTrigger().withIdentity("trigger2", "group2")
				.withSchedule(cronSchedule(schPlanificadoBD.getExpression())).build();
		Date ft = sched.scheduleJob(jobUpdateStatusUser, trigger);

		// Iniciar el planificador
		sched.start();
		log.info("------- Planificador Consulta BD Iniciado --".concat(jobUpdateStatusUser.getKey().toString())
					.concat("- fecha: ").concat(ft.toString())
					.concat(" Planificado: ").concat(trigger.getCronExpression()));

		try {
			//Esperando la ejecución del Hilo de actualización de estatus por 20 min
			Thread.sleep(2400L * 1000L); //
		} catch (Exception e) {
			
		}
		
		log.info("---Terminando Proceso de Actualización de Estatus de Usuarios ---");
		sched.shutdown(Boolean.TRUE);
		log.info("------- Fin de Proceso -----------------");
		SchedulerMetaData metaData = sched.getMetaData();
		log.info("Ejecución en " + metaData.getNumberOfJobsExecuted() + " tareas.");
	}
	
	private Schedule recuperarScheduleToExecute() {
		Schedule sch = null;
		Connection conexion = null;
		Calendar cal = null;
		int hora, minutos;

		try {
			Class.forName(driverClassDB);
			conexion = DriverManager.getConnection(urlDB,
					userDB, passwordDB);
			Statement s = conexion.createStatement();
			cal = Calendar.getInstance();
			hora = cal.get(Calendar.HOUR_OF_DAY);
			minutos = cal.get(Calendar.MINUTE);

			ResultSet rs = s.executeQuery("SELECT id, name, hours, minutes, enabled, expression " + "FROM schedule where hours = "
					+ hora + " and minutes between " + minutos + " and " + (minutos + 29) + " and enabled = true");

			while (rs.next()) {
				sch = new Schedule();
				sch.setId(rs.getLong(1));
				sch.setName(rs.getString(2).trim());
				sch.setHours(rs.getInt(3));
				sch.setMinutes(rs.getInt(4));
				sch.setEnabled(rs.getBoolean(5));
				sch.setExpression(rs.getString(6));
			}
		}catch (Exception e) {
			//TODO validar mensaje o propagacion de excepcion
			e.printStackTrace();
		}finally {
			if (conexion != null) {
				try {
					conexion.close();
				} catch (SQLException e) {
					//TODO validar mensaje o propagacion de excepcion
					e.printStackTrace();
				}
			}
		}
		return sch;
	}
}
