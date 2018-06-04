package com.knox.oauth.admin.services;

import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.Schedule;
import com.knox.oauth.admin.repositories.ScheduleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

	private ScheduleRepository scheduleRepository;
	private Schedule scheduleSupport;

	/*@Bean public PasswordEncoder getPasswordEncoder(){
		return new BCryptPasswordEncoder(); }*/

	@PersistenceContext
	private EntityManager em;

	@Autowired
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Autowired
	public void setScheduleRepository(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
	}

	@Override
	public List<Schedule> listSchedules() {
		return scheduleRepository.findAll();
	}

	@Override
	public Schedule saveSchedule(Schedule schedule) throws SSODataException {
		scheduleSupport = new Schedule();
		try {
			if (schedule.getId() == null) {
				schedule.setCreated(new Date(System.currentTimeMillis()));
			}
			scheduleSupport = scheduleRepository.save(schedule);
		} catch (Exception e) {
			throw new SSODataException(e.getMessage(), e.getCause(), "Error");
		}
		return scheduleSupport;
	}

	@Override
	public Schedule getScheduleById(Long id) {
		return scheduleRepository.findOne(id);
	}

	@Override
	public void deleteSchedule(Long id) {
		scheduleRepository.delete(id);
	}

	@Override
	public Schedule getScheduleByTime(Integer hour, String min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Schedule> listEnabledSchedule() throws SSODataException {
		List<Schedule> reSchedules = new ArrayList<>();
		try {
            TypedQuery<Schedule> query = em.createQuery("SELECT sh FROM Schedule sh WHERE sh.enabled = :enabled", Schedule.class);
            reSchedules = query.setParameter("enabled", Boolean.TRUE).getResultList();
		} catch (Exception e) {
			System.err.println(e.getCause());
			throw new SSODataException();
		}
    	return reSchedules;
	}

}
