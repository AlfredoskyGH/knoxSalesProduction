package com.knox.oauth.admin.services;

import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.ApplicationClient;

import java.util.List;

public interface ApplicationClientService {

    List<ApplicationClient> listAplicacionesClientes();

    ApplicationClient getAplicacionClienteById(Long id);

    ApplicationClient getApplicationClientByClientId(String client_id);

    ApplicationClient saveAplicacionCliente(ApplicationClient aplicacion) throws SSODataException;

    void deleteAplicacionCliente(Long id);

}
