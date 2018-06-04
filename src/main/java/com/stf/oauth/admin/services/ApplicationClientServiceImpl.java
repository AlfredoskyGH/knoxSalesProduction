package com.stf.oauth.admin.services;

import com.stf.oauth.admin.commons.exception.SSODataException;
import com.stf.oauth.admin.entities.ApplicationClient;
import com.stf.oauth.admin.repositories.AplicacionClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;


@Service
public class ApplicationClientServiceImpl implements ApplicationClientService {

	private AplicacionClienteRepository aplicacionClienteRepository;
	private ApplicationClient appicationClientSupport = null;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Autowired
	public void setAplicacionClienteRepository(AplicacionClienteRepository aplicacionClienteRepository) {
		this.aplicacionClienteRepository = aplicacionClienteRepository;
	}

	@Override
	public List<ApplicationClient> listAplicacionesClientes() {
		return aplicacionClienteRepository.findAll();
	}


	@Override
	public ApplicationClient getAplicacionClienteById(Long id) {
		return aplicacionClienteRepository.findOne(id);
	}


	@Override
	public ApplicationClient saveAplicacionCliente(ApplicationClient aplicacion) throws SSODataException {
		appicationClientSupport = new ApplicationClient();
		try {
			if (aplicacion.getId() == null) {
				aplicacion.setCreated(new Date(System.currentTimeMillis()));
			}
			appicationClientSupport = aplicacionClienteRepository.saveAndFlush(aplicacion);
		} catch (Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				throw new SSODataException(e.getMessage(), e.getCause(), "DUPKEY");
			} else {
				throw new SSODataException(e.getMessage(), e.getCause(), "NOSE");
			}
		}
		return appicationClientSupport;
	}

	@Override
	public void deleteAplicacionCliente(Long id) {
		aplicacionClienteRepository.delete(id);
	}

	//TODO LA CONSTRUCCION DE LA CONSULTA
	@Override
	public ApplicationClient getApplicationClientByClientId(String client_id) {
		ApplicationClient applicationClient = null;
		try {
			
			TypedQuery<ApplicationClient> query = em.createQuery("SELECT ac FROM ApplicationClient ac "
					+ "WHERE ac.clientId =  :client_id", ApplicationClient.class);
			applicationClient = new ApplicationClient();
			applicationClient = query.setParameter("client_id", client_id).getSingleResult();

		} catch (Exception e) {
			//TODO
			e.printStackTrace();
		}
		return applicationClient;
	}

}
