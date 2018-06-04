package com.knox.oauth.admin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knox.oauth.admin.entities.UserSso;

@Repository
public interface UserSsoRepository extends JpaRepository<UserSso, Long> {

    UserSso findByUsername(String username);
}
