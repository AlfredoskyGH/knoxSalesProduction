package com.knox.oauth.admin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knox.oauth.admin.entities.ApplicationClient;

public interface AplicacionClienteRepository extends JpaRepository<ApplicationClient, Long> {

}
