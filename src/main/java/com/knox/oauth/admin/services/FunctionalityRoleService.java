package com.knox.oauth.admin.services;

import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.FunctionalityRole;

public interface FunctionalityRoleService {

    Iterable<FunctionalityRole> listFuncionalidades();

    FunctionalityRole getFuncionalidadById(Long id);

    FunctionalityRole getFuncionalidadByName(String name, long idRoleApp);

    FunctionalityRole saveFuncionalidad(FunctionalityRole funcionalidad) throws SSODataException;

    void deleteFuncionalidad(Long id);

    FunctionalityRole getFunctionalityRoleByName(String name, long RoleAppId);

}
