package com.stf.oauth.admin.services;

import com.stf.oauth.admin.commons.exception.SSODataException;
import com.stf.oauth.admin.entities.FunctionalityRole;

public interface FunctionalityRoleService {

    Iterable<FunctionalityRole> listFuncionalidades();

    FunctionalityRole getFuncionalidadById(Long id);

    FunctionalityRole getFuncionalidadByName(String name, long idRoleApp);

    FunctionalityRole saveFuncionalidad(FunctionalityRole funcionalidad) throws SSODataException;

    void deleteFuncionalidad(Long id);

    FunctionalityRole getFunctionalityRoleByName(String name, long RoleAppId);

}
