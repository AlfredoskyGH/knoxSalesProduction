package com.stf.oauth.admin.repositories;

import com.stf.oauth.admin.entities.UserSso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSsoRepository extends JpaRepository<UserSso, Long> {

    UserSso findByUsername(String username);
}
