package com.knox.oauth.admin.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *@author team ssoinnova4j
 *
 *Clase entidad que representa una funcionalidad de un rol de una aplicación cliente. 
 *15/03/2018 
 */


@Entity
@Table(name = "oauth_functionality", uniqueConstraints= @UniqueConstraint(columnNames={"role_id", "name"}))
public class FunctionalityRole implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6703889625848068724L;

	@Id
	@GeneratedValue (strategy= GenerationType.AUTO)
    private Long id;

    @Column (name="name", length=50)
    private String name;

    @Column(name = "enabled")
	private Boolean enabled;
	
    @Temporal(TemporalType.DATE)
	@Column (name="created")
    private Date created;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="role_id")
	@JsonManagedReference
    private RoleApp roleApp;
	
	/**
	 */
	public FunctionalityRole() {
	}
	
	/**
	 * @param id
	 * @param name
	 * @param enabled
	 * @param created
	 * @param roleApp
	 */
	public FunctionalityRole(Long id, String name, Boolean enabled, Date created, RoleApp roleApp) {
		this.id = id;
		this.name = name;
		this.enabled = enabled;
		this.created = created;
		this.roleApp = roleApp;
	}
	
	/**
	 * @param roleApp
	 */
	public FunctionalityRole(RoleApp roleApp) {
		this.roleApp = roleApp;
	}
	
	/**
	 * @param id
	 */
	public FunctionalityRole(Long id) {
		this.id = id;
	}

	
	//Getter y Setter
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
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
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/**
	 * @return the roleApp
	 */
	public RoleApp getRoleApp() {
		return roleApp;
	}

	/**
	 * @param roleApp the roleApp to set
	 */
	public void setRoleApp(RoleApp roleApp) {
		this.roleApp = roleApp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FunctionalityRole [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", created=");
		builder.append(created);
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
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof FunctionalityRole))
			return false;
		FunctionalityRole other = (FunctionalityRole) obj;
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
		return true;
	}

}
