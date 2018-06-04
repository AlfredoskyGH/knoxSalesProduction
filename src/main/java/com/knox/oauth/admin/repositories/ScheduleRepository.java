package com.knox.oauth.admin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knox.oauth.admin.entities.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

}
