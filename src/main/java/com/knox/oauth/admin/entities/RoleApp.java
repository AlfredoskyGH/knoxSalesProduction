package com.knox.oauth.admin.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author team ssoinnova4j
 *
 *         Clase entidad que representa un rol de una aplicación, además de una
 *         entidad que se relaciona con los usuarios sso. 15/03/2018
 */

@Entity
@Table(name = "oauth_roles", uniqueConstraints = @UniqueConstraint(columnNames = { "aplication_client_id", "name" }))
public class RoleApp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5423416466129355042L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "type")
	private String type;

	@Column(name = "enabled")
	private Boolean enabled;

	@Temporal(TemporalType.DATE)
	@Column(name = "created")
	private Date created;

	// Relaciones entre entidades
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aplication_client_id")
	private ApplicationClient aplicationClient;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "roleApp")
	private List<FunctionalityRole> functionalityRoles = new ArrayList<>();

	@ManyToMany(mappedBy = "roles")
	private List<UserSso> userSsos = new ArrayList<>();

	// Gestion de la relacion *ToMany
	public void addFunctionalityRole(FunctionalityRole functionalityRole) {
		functionalityRoles.add(functionalityRole);
		functionalityRole.setRoleApp(this);
	}

	public void removeFunctionalityRole(FunctionalityRole functionalityRole) {
		functionalityRoles.remove(functionalityRole);
		functionalityRole.setRoleApp(null);
	}

	public void addUserSso(UserSso userSso) {
		userSsos.add(userSso);
		userSso.getRoles().add(this);
	}

	public void removeUserSso(UserSso userSso) {
		userSsos.remove(userSso);
		userSso.getRoles().remove(this);
	}

	public Boolean isAdmin() {
		if(name.equals("ADMIN_SSO")){
			return true;
		}

		return false;
	}

	// Constructores
	/**
	 */
	public RoleApp() {
	}

	/**
	 * @param aplicationClient
	 */
	public RoleApp(ApplicationClient aplicationClient) {
		this.aplicationClient = aplicationClient;
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param enabled
	 * @param created
	 * @param aplicationClient
	 */
	public RoleApp(Long id, String name, String type, Boolean enabled, Date created,
			ApplicationClient aplicationClient) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.enabled = enabled;
		this.created = created;
		this.aplicationClient = aplicationClient;
	}

	/**
	 * @param id
	 */
	public RoleApp(Long id) {
		this.id = id;
	}

	// Getter y Setter
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the aplicationClient
	 */
	public ApplicationClient getAplicationClient() {
		return aplicationClient;
	}

	/**
	 * @param aplicationClient
	 *            the aplicationClient to set
	 */
	public void setAplicationClient(ApplicationClient aplicationClient) {
		this.aplicationClient = aplicationClient;
	}

	/**
	 * @return the functionalityRoles
	 */
	public List<FunctionalityRole> getFunctionalityRoles() {
		return functionalityRoles;
	}

	/**
	 * @param functionalityRoles
	 *            the functionalityRoles to set
	 */
	public void setFunctionalityRoles(List<FunctionalityRole> functionalityRoles) {
		this.functionalityRoles = functionalityRoles;
	}

	/**
	 * @return the userSsos
	 */
	public List<UserSso> getUserSsos() {
		return userSsos;
	}

	/**
	 * @param userSsos
	 *            the userSsos to set
	 */
	public void setUserSsos(List<UserSso> userSsos) {
		this.userSsos = userSsos;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleApp [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", tipo=");
		builder.append(type);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", created=");
		builder.append(created);
		builder.append(", functionalities=");
		builder.append(functionalityRoles);
		builder.append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RoleApp))
			return false;
		RoleApp other = (RoleApp) obj;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
