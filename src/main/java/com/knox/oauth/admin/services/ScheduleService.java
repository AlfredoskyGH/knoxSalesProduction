package com.knox.oauth.admin.services;

import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.Schedule;

import java.util.List;


public interface ScheduleService {

    List<Schedule> listSchedules();
    
    List<Schedule> listEnabledSchedule() throws SSODataException;

    Schedule saveSchedule(Schedule schedule) throws SSODataException ;

    void deleteSchedule(Long id);

    Schedule getScheduleById(Long id);
    
    Schedule getScheduleByTime(Integer hour, String min);

}
