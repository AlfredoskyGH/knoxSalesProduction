package com.knox.oauth.admin.controllers;

import com.knox.oauth.admin.commons.bean.Days;
import com.knox.oauth.admin.commons.bean.Notification;
import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.Schedule;
import com.knox.oauth.admin.services.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller(value = "ScheduleController")
public class ScheduleController {

	private Schedule scheduleRef;
	private Days daysRef;
	private List<Schedule> listScheduleRef;
	private ScheduleService scheduleService;
	private Notification notification;

	@Autowired
	public void setScheduleService(ScheduleService scheduleService) {
		this.scheduleService = scheduleService;
	}

	@RequestMapping(value = "/sso/planificador", method = RequestMethod.GET)
	public String getSchedule(Model model) {
		scheduleRef = new Schedule();
		daysRef = new Days();
		listScheduleRef = scheduleService.listSchedules();

		model.addAttribute("schedule", scheduleRef);
		model.addAttribute("days", daysRef);
		model.addAttribute("listSchedule", listScheduleRef);
		return "planificador";
	}

	@RequestMapping(value = "/sso/planificador", method = RequestMethod.POST)
	public String saveSchedule(Schedule schedule, Days days, Model model) {
		scheduleRef = new Schedule();
		notification = new Notification();

		if (validate(schedule, days, notification)) {
			schedule.setName("Consultar Estado de Usuarios - Active Directory");
			schedule.setPeriodicity("Diariamente");
			schedule.setDays(getSelectDays(days));
			schedule.setExpression(schedule.constructExpression(schedule));

			try {
				scheduleRef = scheduleService.saveSchedule(schedule);
			} catch (SSODataException e) {
				if (e.getCodigoErr().equals("DUPKEY")) {
					notification.alert("1", "ERROR", "Tarea ya se encuentra registrada.");
				} else {
					notification.alert("1", "ERROR", e.getMensaje());
				}

			}
			notification.alert("1", "SUCCESS", "Tarea registrada con exito. ");
		}

		listScheduleRef = scheduleService.listSchedules();
		model.addAttribute("schedule", schedule);
		model.addAttribute("days", days);
		model.addAttribute("listSchedule", listScheduleRef);
		model.addAttribute("notification", notification);
		return "planificador";

	}

	@RequestMapping("/sso/planificador/edit/{id}")
	public String editUserSso(@PathVariable Long id, Model model) {
		scheduleRef = new Schedule();
		daysRef = new Days();
		listScheduleRef = scheduleService.listSchedules();

		scheduleRef = scheduleService.getScheduleById(id);
		daysRef = daysRef.setDays(scheduleRef.getDays());

		model.addAttribute("schedule", scheduleRef);
		model.addAttribute("days", daysRef);
		model.addAttribute("listSchedule", listScheduleRef);
		return "planificador";
	}

	@RequestMapping("/sso/planificador/delete/{id}")
	public String deleteUserSso(@PathVariable Long id) {
		scheduleService.deleteSchedule(id);
		return "redirect:/sso/planificador";
	}

	public boolean validate(Schedule schedule, Days days, Notification notification) {

		if (getSelectDays(days).trim().isEmpty()) {
			notification.alert("1", "ERROR", "Debe seleccionar al menos un dia.");
			return false;
		}

		if (schedule.getHours().equals("-1")) {
			notification.alert("1", "ERROR", "Seleccione la hora de ejecución de la tarea.");
			return false;
		}

		if (schedule.getMinutes().equals("-1")) {
			notification.alert("1", "ERROR", "Seleccione los minutos de ejecución de la tarea.");
			return false;
		}

		return true;

	}

	public String getSelectDays(Days days) {
		String result = "";

        for (String day :days.getListDays()) {
            if (day!= null){
                result += day + " - ";
            }
        }
        if(result!=""){
            result = result.substring(0, result.length()- 3);
        }

		return result;
	}

}
