package com.stf.oauth.admin.services;

import com.stf.oauth.admin.commons.exception.SSODataException;
import com.stf.oauth.admin.entities.ApplicationClient;

import java.util.List;

public interface ApplicationClientService {

    List<ApplicationClient> listAplicacionesClientes();

    ApplicationClient getAplicacionClienteById(Long id);

    ApplicationClient getApplicationClientByClientId(String client_id);

    ApplicationClient saveAplicacionCliente(ApplicationClient aplicacion) throws SSODataException;

    void deleteAplicacionCliente(Long id);

}
