package com.knox.oauth.admin.services;

import com.knox.oauth.admin.commons.exception.SSODataException;
import com.knox.oauth.admin.entities.RoleApp;
import com.knox.oauth.admin.repositories.RoleAppRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Service
public class RoleAppServiceImpl implements RoleAppService {

    private RoleAppRepository rolRepository;
    private RoleApp roleAppSupport;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Autowired
    public void setRolRepository(RoleAppRepository rolRepository) {
		this.rolRepository = rolRepository;
	}
    
    @Override
    public List<RoleApp> listRoles() {
        return rolRepository.findAll();
    }

	@Override
    public RoleApp getRolById(Long id) {
        return rolRepository.findOne(id);
    }

    @Override
    public RoleApp saveRol(RoleApp rol) throws SSODataException {
    	roleAppSupport = new RoleApp();
    	try {
			if (rol.getId()==null) {
				rol.setCreated(new Date(System.currentTimeMillis()));
			}
			roleAppSupport = rolRepository.saveAndFlush(rol);
		} catch (Exception e) {
			//TODO validar exception
			throw new SSODataException(e.getMessage(), e.getCause(), "DUPKEY");
		}
    	return roleAppSupport;
    }

    @Override
    public void deleteRol(Long id) {
    	/*try {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("busquedaRoles");
			EntityManager em = emf.createEntityManager();
			TypedQuery<RoleApp> query = em.createQuery("DELETE  FROM RoleApp ra WHERE ra.id = :rolid", RoleApp.class);
			query.setParameter("rolid", id).getSingleResult();
		} catch (Exception e) {
			//TODO
			e.printStackTrace();
		}*/
    	rolRepository.delete(id);
    	//return;
    }

    @Override
    public RoleApp findRolByNameApp(String name, Long appId) {
    	roleAppSupport = new RoleApp();
    	try {
            TypedQuery<RoleApp> query = em.createQuery("SELECT ra FROM RoleApp ra WHERE ra.name = :rolname AND ra.ApplicationClient = :appid", RoleApp.class);
            roleAppSupport = query.setParameter("rolname", name).setParameter("appid", appId).getSingleResult();

		} catch (Exception e) {
			//TODO
			e.printStackTrace();
		}
    	return roleAppSupport;
    }
    
    @Override
    public List<RoleApp> listRolesByApplicationId(Long idapp){
    	return rolRepository.findAll();
    }

}
