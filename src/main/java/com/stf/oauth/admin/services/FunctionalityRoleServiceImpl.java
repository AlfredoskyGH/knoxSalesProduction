package com.stf.oauth.admin.services;

import com.stf.oauth.admin.commons.exception.SSODataException;
import com.stf.oauth.admin.entities.FunctionalityRole;
import com.stf.oauth.admin.repositories.FunctionalityRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Service
public class FunctionalityRoleServiceImpl implements FunctionalityRoleService {

    private FunctionalityRoleRepository funcionalidadRepository;
    private FunctionalityRole functionalityRoleSupport;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Autowired
    public void setFuncionalidadRepositoryRepository(FunctionalityRoleRepository funcionalidadRepository) {
		this.funcionalidadRepository = funcionalidadRepository;
	}
    
    @Override
    public Iterable<FunctionalityRole> listFuncionalidades() {
        return funcionalidadRepository.findAll();
    }

	@Override
    public FunctionalityRole getFuncionalidadById(Long id) {
        return funcionalidadRepository.findOne(id);
    }

    @Override
    public FunctionalityRole saveFuncionalidad(FunctionalityRole funcionalidad) throws SSODataException{
    	try {
			if (funcionalidad.getId()==null) {
				funcionalidad.setCreated(new Date(System.currentTimeMillis()));
			}
			functionalityRoleSupport = funcionalidadRepository.save(funcionalidad);
		} catch (Exception e) {
			throw new SSODataException(e.getMessage(),e.getCause(),"DUPKEY");
		}     
        return functionalityRoleSupport;
    }

    @Override
    public void deleteFuncionalidad(Long id) {
        funcionalidadRepository.delete(id);
    }

    @Override
    public FunctionalityRole getFuncionalidadByName(String name, long roleAppId) {
        try {
            TypedQuery<FunctionalityRole> query = em.createQuery("SELECT fr FROM FunctionalityRole fr WHERE fr.name = :functionalityname AND fr.roleApp = :roleappid", FunctionalityRole.class);
            return query.setParameter("functionalityname", name).setParameter("roleappid", roleAppId).getSingleResult();

        } catch (Exception e) {
            //TODO
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public FunctionalityRole getFunctionalityRoleByName(String name, long roleAppId) {
        try {
            FunctionalityRole objFunctionalityRole = new FunctionalityRole();
            TypedQuery<FunctionalityRole> query = em.createQuery("SELECT fr FROM FunctionalityRole fr WHERE fr.name = :functionalityname AND fr.roleApp = :roleappid", FunctionalityRole.class);

            List<FunctionalityRole> list = query.setParameter("functionalityname", name).setParameter("roleappid", roleAppId).getResultList();
            for (FunctionalityRole functionalityRole : list) {
                objFunctionalityRole = functionalityRole;
                break;
            }
            return objFunctionalityRole;

        } catch (Exception e) {
            //TODO
            e.printStackTrace();
            return new FunctionalityRole();
        }

    }

}
