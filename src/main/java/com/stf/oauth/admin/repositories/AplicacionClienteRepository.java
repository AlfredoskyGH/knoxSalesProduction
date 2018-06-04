package com.stf.oauth.admin.repositories;

import com.stf.oauth.admin.entities.ApplicationClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AplicacionClienteRepository extends JpaRepository<ApplicationClient, Long> {

}
