package com.stf.oauth.admin.services;

import com.stf.oauth.admin.commons.bean.ObjectsJSON.UserJson;
import com.stf.oauth.admin.commons.exception.SSODataException;
import com.stf.oauth.admin.entities.UserSso;

import java.util.List;

public interface UserSsoService {

    List<UserSso> listUsersSso();

    UserSso getUserSsoById(Long id);

    UserSso saveUserSso(UserSso userSso) throws SSODataException ;

    void deleteUserSso(Long id);

    UserSso findUserSsoByUsername(String username);

    UserSso findUserSsoByToken(String token);

    Boolean isAdminSsoUser(String username);

    UserJson getUserJSON(String username);

    UserSso findUserSsoByCredentials(String username, String password);

}
