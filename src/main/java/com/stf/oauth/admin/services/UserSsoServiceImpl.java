package com.stf.oauth.admin.services;

import com.stf.oauth.admin.commons.bean.ObjectsJSON;
import com.stf.oauth.admin.commons.bean.ObjectsJSON.UserJson;
import com.stf.oauth.admin.commons.exception.SSODataException;
import com.stf.oauth.admin.entities.RoleApp;
import com.stf.oauth.admin.entities.UserSso;
import com.stf.oauth.admin.repositories.UserSsoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserSsoServiceImpl implements UserSsoService {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UserSsoServiceImpl.class);

	private UserSsoRepository userSsoRepository;
	private UserSso userSsoSupport;

	@Value("${spring.datasource.url}")
	private String urlDB;

	@Value("${spring.datasource.username}")
	private String userDB;

	@Value("${spring.datasource.password}")
	private String passwordDB;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassDB;

	/*@Bean public PasswordEncoder getPasswordEncoder(){
		return new BCryptPasswordEncoder(); }*/

	@PersistenceContext
	private EntityManager em;

	@Autowired
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Autowired
	public void setUserSsoRepository(UserSsoRepository userSsoRepository) {
		this.userSsoRepository = userSsoRepository;
	}

	@Override
	public List<UserSso> listUsersSso() {
		return userSsoRepository.findAll();
	}

	@Override
	public UserSso getUserSsoById(Long id) {
		return userSsoRepository.findOne(id);
	}

	@Override
	public UserSso saveUserSso(UserSso userSso) throws SSODataException {
		userSsoSupport = new UserSso();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		try {
			if (userSso.getId() == null) {
				userSso.setCreated(new Date(System.currentTimeMillis()));
				if(userSso.getPassword()==null){
					logger.warn("[Carga Masiva] Usuario " + userSso.getUsername() +": registrado con password null.");
				}else{
					userSso.setPassword(encoder.encode(userSso.getPassword()));
				}
			}
			userSsoSupport = userSsoRepository.saveAndFlush(userSso);
		} catch (Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				throw new SSODataException(e.getMessage(), e.getCause(), "DUPKEY");
			} else {
				throw new SSODataException(e.getMessage(), e.getCause(), "NOSE");
			}
		}
		return userSsoSupport;
	}

	@Override
	public void deleteUserSso(Long id) {
		userSsoRepository.delete(id);
	}

	@Override
	public UserSso findUserSsoByUsername(String username) {
		userSsoSupport = new UserSso();
		try {
			TypedQuery<UserSso> query = em.createQuery("SELECT us FROM UserSso us WHERE us.username =  :username  and us.enabled = true",
					UserSso.class);
			userSsoSupport = query.setParameter("username", username.trim()).getSingleResult();
		} catch (Exception e) {
			System.err.println("Usuario no encontrado en FindUserByUserName: " + username);
		}
		return userSsoSupport;
	}

	@Override
	public Boolean isAdminSsoUser(String username) {
		userSsoSupport = null;
		Boolean result = Boolean.FALSE;
		Set<RoleApp> listaRoles = null;
		try {
			//TODO mejorar consulta
			TypedQuery<UserSso> query = em.createQuery("SELECT us FROM UserSso us WHERE us.username =  :username  and us.enabled = true",
					UserSso.class);
			userSsoSupport = query.setParameter("username", username.trim()).getSingleResult();
			listaRoles = userSsoSupport.getRoles();
			for (RoleApp role : listaRoles ) {
				//TODO CLASE CONSTANTE
				if(role.getName().equals("ADMIN_SSO")){
					result = Boolean.TRUE;
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("FALLA: metodo isAdminSsoUser, con usuario: " + username);
		}
		return result;
	}

	@Override
	public UserSso findUserSsoByToken(String token) {
		userSsoSupport = new UserSso();
		try {
			TypedQuery<UserSso> query = em.createQuery("SELECT us FROM UserSso us WHERE us.token =  :token",
					UserSso.class);
			userSsoSupport = query.setParameter("token", token.trim()).getSingleResult();
		} catch (Exception e) {
			System.err.println("Token no encontrado en findUserSsoByToken: " + token);
		}
		return userSsoSupport;
	}

    @Override
    public UserJson getUserJSON(String username) {
        UserJson userJson = null;
        Connection conexion = null;

        try {
            Class.forName(driverClassDB);
            conexion = DriverManager.getConnection(urlDB, userDB, passwordDB);
            Statement s = conexion.createStatement();

            ResultSet rs = s.executeQuery("SELECT ou.username, ou.firstname, ou.lastname, ou.email, ou.enabled, ou.user_active_until " + "FROM oauth_users ou "
					+ "where ou.username = '" + username + "'");
                    //+ "INNER JOIN oauth_access_token oat "+  "where oat.token_id = '" + token + "'");

            while (rs.next()) {
                userJson = new ObjectsJSON().new UserJson();
                userJson.setUsername(rs.getString(1));
                userJson.setFirstname(rs.getString(2));
                userJson.setLastname(rs.getString(3));
                userJson.setEmail(rs.getString(4));
                userJson.setEnabled(rs.getBoolean(5));
                userJson.setUserActiveUntil(rs.getDate(6));
            }
        } catch (Exception e) {
            //TODO validar mensaje o propagacion de excepcion
            e.printStackTrace();
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    //TODO validar mensaje o propagacion de excepcion
                    e.printStackTrace();
                }
            }
        }
        return userJson;
    }

	@Override
	public UserSso findUserSsoByCredentials(String username, String password) {
		userSsoSupport = new UserSso();
		try {
			TypedQuery<UserSso> query = em.createQuery("SELECT us FROM UserSso us WHERE us.username =  :username and us.password = :password and us.enabled = true",
					UserSso.class);
			userSsoSupport = query.setParameter("username", username.trim()).setParameter("password", password).getSingleResult();
		} catch (Exception e) {
			System.err.println("Credenciales invalidas: " + username);
		}
		return userSsoSupport;
	}

}
