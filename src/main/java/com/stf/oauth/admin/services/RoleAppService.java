package com.stf.oauth.admin.services;

import com.stf.oauth.admin.commons.exception.SSODataException;
import com.stf.oauth.admin.entities.RoleApp;

import java.util.List;

public interface RoleAppService {

    List<RoleApp> listRoles();
    
    List<RoleApp> listRolesByApplicationId(Long idApp);

    RoleApp getRolById(Long id);

    RoleApp findRolByNameApp(String name, Long idApp);

    RoleApp saveRol(RoleApp rol) throws SSODataException;

    void deleteRol(Long id);

}
