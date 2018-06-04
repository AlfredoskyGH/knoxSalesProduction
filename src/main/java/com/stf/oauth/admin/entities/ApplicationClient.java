package com.stf.oauth.admin.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *@author team ssoinnova4j
 *
 *Clase entidad que representa una aplicación cliente que necesita autorización
 *a través del protocolo oauth2. 
 */

@Entity
@Table(name = "oauth_client_details")
public class ApplicationClient implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4765428946296185470L;

    @Id
	@GeneratedValue (strategy= GenerationType.AUTO)
	private Long id;

    @Column(name="client_id", unique=true)
    private String clientId;
    
    @Column(name="name")
    private String name;

    @Column (name="resource_ids")
    private String resourceIds;
    
    @Column (name = "client_secret")
    private String secret;

    @Column (name = "scope")
    private String scope;
    
    @Column(name = "enabled")
	private Boolean enabled;
    
    @Column(name = "authorized_grant_types",nullable = false, length=50)
    private String authorizedGrantTypes;

    @Column (name = "web_server_redirect_uri",length=250)
    private String webServerRedirectUri;

    @Column (name="authorities", length=50)
    private String authorities;

    @Column(name="access_token_validity", nullable = false)
    private Integer accessTokenValiditySeconds;

    @Column(name="refresh_token_validity", nullable = false)
    private Integer refreshTokenValiditySeconds;

    @Column (name="autoapprove",length=50)
    private String autoApproveScope;

    @Column (name="additional_information")
    private String additionalInformation;
    
    @Temporal(TemporalType.DATE)
    @Column (name="created")
    private Date created;
    
	//relacion con otras entidades
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserSso userSso;
    
    @OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL,mappedBy="aplicationClient")
    private	Set<RoleApp> roleApps;
    
    //Gestion de la relacion *ToMany
    public void addRoleApp(RoleApp roleApp) {
        roleApps.add(roleApp);
        roleApp.setAplicationClient(this);
    }
 
    public void removeRoleApp(RoleApp roleApp) {
        roleApps.remove(roleApp);
        roleApp.setAplicationClient(null);
    }
    
    //Constructores
    public ApplicationClient() {
    	
    }
    
    public ApplicationClient(Long id) {
		super();
		this.id = id;
	}
    
    public ApplicationClient(UserSso userSso) {
		super();
		this.userSso = userSso;
	}
    
    //Getter y Setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	public String getWebServerRedirectUri() {
		return webServerRedirectUri;
	}

	public void setWebServerRedirectUri(String webServerRedirectUri) {
		this.webServerRedirectUri = webServerRedirectUri;
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

	public String getAutoApproveScope() {
		return autoApproveScope;
	}

	public void setAutoApproveScope(String autoApproveScope) {
		this.autoApproveScope = autoApproveScope;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Set<RoleApp> getRoleApps() {
		return roleApps;
	}

	public void setRoleApps(Set<RoleApp> roleApps) {
		this.roleApps = roleApps;
	}

	public UserSso getUserSso() {
		return userSso;
	}

	public void setUserSso(UserSso userSso) {
		this.userSso = userSso;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationClient [id=");
		builder.append(id);
		builder.append(", client_id=");
		builder.append(clientId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", resourceIds=");
		builder.append(resourceIds);
		builder.append(", client_secret=");
		builder.append(secret);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", authorizedGrantTypes=");
		builder.append(authorizedGrantTypes);
		builder.append(", webServerRedirectUri=");
		builder.append(webServerRedirectUri);
		builder.append(", authorities=");
		builder.append(authorities);
		builder.append(", accessTokenValiditySeconds=");
		builder.append(accessTokenValiditySeconds);
		builder.append(", refreshTokenValiditySeconds=");
		builder.append(refreshTokenValiditySeconds);
		builder.append(", autoApproveScope=");
		builder.append(autoApproveScope);
		builder.append(", additionalInformation=");
		builder.append(additionalInformation);
		builder.append(", created=");
		builder.append(created);
		builder.append(", roleApps=");
		builder.append(roleApps);
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
		result = prime * result + ((accessTokenValiditySeconds == null) ? 0 : accessTokenValiditySeconds.hashCode());
		result = prime * result + ((additionalInformation == null) ? 0 : additionalInformation.hashCode());
		result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
		result = prime * result + ((authorizedGrantTypes == null) ? 0 : authorizedGrantTypes.hashCode());
		result = prime * result + ((autoApproveScope == null) ? 0 : autoApproveScope.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((secret == null) ? 0 : secret.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((refreshTokenValiditySeconds == null) ? 0 : refreshTokenValiditySeconds.hashCode());
		result = prime * result + ((resourceIds == null) ? 0 : resourceIds.hashCode());
		result = prime * result + ((roleApps == null) ? 0 : roleApps.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((webServerRedirectUri == null) ? 0 : webServerRedirectUri.hashCode());
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
		if (!(obj instanceof ApplicationClient))
			return false;
		ApplicationClient other = (ApplicationClient) obj;
		if (accessTokenValiditySeconds == null) {
			if (other.accessTokenValiditySeconds != null)
				return false;
		} else if (!accessTokenValiditySeconds.equals(other.accessTokenValiditySeconds))
			return false;
		if (additionalInformation == null) {
			if (other.additionalInformation != null)
				return false;
		} else if (!additionalInformation.equals(other.additionalInformation))
			return false;
		if (authorities == null) {
			if (other.authorities != null)
				return false;
		} else if (!authorities.equals(other.authorities))
			return false;
		if (authorizedGrantTypes == null) {
			if (other.authorizedGrantTypes != null)
				return false;
		} else if (!authorizedGrantTypes.equals(other.authorizedGrantTypes))
			return false;
		if (autoApproveScope == null) {
			if (other.autoApproveScope != null)
				return false;
		} else if (!autoApproveScope.equals(other.autoApproveScope))
			return false;
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
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (secret == null) {
			if (other.secret != null)
				return false;
		} else if (!secret.equals(other.secret))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (refreshTokenValiditySeconds == null) {
			if (other.refreshTokenValiditySeconds != null)
				return false;
		} else if (!refreshTokenValiditySeconds.equals(other.refreshTokenValiditySeconds))
			return false;
		if (resourceIds == null) {
			if (other.resourceIds != null)
				return false;
		} else if (!resourceIds.equals(other.resourceIds))
			return false;
		if (roleApps == null) {
			if (other.roleApps != null)
				return false;
		} else if (!roleApps.equals(other.roleApps))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (webServerRedirectUri == null) {
			if (other.webServerRedirectUri != null)
				return false;
		} else if (!webServerRedirectUri.equals(other.webServerRedirectUri))
			return false;
		return true;
	}
}
