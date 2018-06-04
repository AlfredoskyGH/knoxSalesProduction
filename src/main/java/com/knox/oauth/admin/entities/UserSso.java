package com.knox.oauth.admin.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author team ssoinnova4j
 * Clase entidad usuario que se requiere autenticar o validar
 * con el modulo sso. Representa el usuario que recibira acceso a aplicaciones.
 * 15/03/2018
 */

@Entity
@Table(name = "oauth_users")
public class UserSso implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3181104013651339465L;

	@Id
	@GeneratedValue (strategy= GenerationType.AUTO)
	private Long id;

	@Column(name = "username", nullable = false, unique = true)
	private String username;
	
	@Column(name="password")
	private String password;
	
	@Column(name = "firstname" )
	private String firstname;
	
	@Column(name = "lastname")
	private String lastname;
	
	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "enabled")
	private Boolean enabled;

	@Temporal(TemporalType.DATE)
	@Column (name="user_active_until")
	@DateTimeFormat(pattern="dd/MM/yyyy")
    private Date userActiveUntil;
	
	@Temporal(TemporalType.DATE)
	@Column (name="created")
	private Date created;

	@Column (name="token")
	private String token;

	@Column (name="token_date_validate")
	private Date tokenDateValidate;
	

	//Relaciones de entidades
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name = "oauth_users_roles",  joinColumns = @JoinColumn(name = "user_id"),
    	    inverseJoinColumns = @JoinColumn(name = "role_id"))
	@JsonManagedReference
    private Set<RoleApp> roles = new HashSet<>();

	//Gestion de la relacion *ToMany
	public void addRoleApp(RoleApp roleApp) {
		roles.add(roleApp);
		roleApp.getUserSsos().add(this);
	}

    public void removeRoleApp(RoleApp roleApp) {
        roles.remove(roleApp);
        roleApp.getUserSsos().remove(this);
    }

    public Boolean isAdmin(){
		for (RoleApp role: roles) {
			if(role.getName().equals("ADMIN_SSO")){
				return true;
			}
		}
		return false;
	}

	//CONSTRUCTORES
	public UserSso() {
	}
	
	public UserSso(Long id) {
		this.id = id;
	}
	
	public UserSso(UserSso userSso) {
		this.id = userSso.id;
		this.username = userSso.username;
		this.password = userSso.password;
		this.firstname = userSso.firstname;
		this.lastname = userSso.lastname;
		this.email = userSso.email;
		this.enabled = userSso.enabled;
		this.userActiveUntil = userSso.userActiveUntil;
		this.created = userSso.created;
		this.roles = userSso.roles;
	}
	
	/**
	 * @param id
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param enabled
	 * @param userActiveUntil
	 * @param created
	 * @param token
	 * @param tokenDateValidate
	 * @param roles
	 */
	public UserSso(Long id, String username, String password, String firstname, String lastname, String email,
			Boolean enabled, Date userActiveUntil, Date created, String token, Date tokenDateValidate,
			Set<RoleApp> roles) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.enabled = enabled;
		this.userActiveUntil = userActiveUntil;
		this.created = created;
		this.token = token;
		this.tokenDateValidate = tokenDateValidate;
		this.roles = roles;
	}

/*	public UserSso getObject(){
		UserSso obj = new UserSso();
		obj.id = this.id;
		obj.username = this.username;
		obj.firstname = this.firstname;
		obj.lastname = this.lastname;
		obj.email = this.email;
		obj.enabled = this.enabled;
		obj.userActiveUntil = this.userActiveUntil;
		obj.created = this.created;
		obj.roles = this.roles;
		return obj;
	}*/

	//Getter y Setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getUserActiveUntil() {
		return userActiveUntil;
	}

	public void setUserActiveUntil(Date userActiveUntil) {
		this.userActiveUntil = userActiveUntil;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getTokenDateValidate() {
		return tokenDateValidate;
	}

	public void setTokenDateValidate(Date tokenDateValidate) {
		this.tokenDateValidate = tokenDateValidate;
	}

	public Set<RoleApp> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleApp> roles) {
		this.roles = roles;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserSso [id=");
		builder.append(id);
		builder.append(", username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append(", firstname=");
		builder.append(firstname);
		builder.append(", lastname=");
		builder.append(lastname);
		builder.append(", email=");
		builder.append(email);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", userActiveUntil=");
		builder.append(userActiveUntil);
		builder.append(", created=");
		builder.append(created);
		builder.append(", roles=");
		builder.append(roles);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result + ((userActiveUntil == null) ? 0 : userActiveUntil.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserSso))
			return false;
		UserSso other = (UserSso) obj;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (userActiveUntil == null) {
			if (other.userActiveUntil != null)
				return false;
		} else if (!userActiveUntil.equals(other.userActiveUntil))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}


	/*
	//TODO crear entidad completa de auditoria (logs)
	@Column(name = "ultima_ip_conexion", length = 50)
	private String ultimaIpConexion;
	@Column(name = "ultima_app_conexion", length = 50)
	private String nombreUltimaAppConectado;
	//TODO revision acceso por rango de ip o grupo de direcciones
	@Column(name = "rango_ip_externo", length=50)
	private Boolean external;*/
}

