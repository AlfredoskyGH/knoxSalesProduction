package com.knox.oauth.admin.commons.bean;

import java.io.Serializable;
import java.util.Date;

import com.knox.oauth.admin.entities.FunctionalityRole;
import com.knox.oauth.admin.entities.RoleApp;
import com.knox.oauth.admin.entities.UserSso;

/**
 * Clase wrapper que contiene las entidades Json mapeadas en los endpoints para
 * los controllers (rest-controllers)
 * 
 * @author team ssoinnova4j
 *
 */
public class ObjectsJSON {

	public class UserJson implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String username;
		private String firstname;
		private String lastname;
		private String email;
		private Boolean enabled;
		private Date userActiveUntil;

		public UserJson() {

		}

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @param username
		 *            the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		/**
		 * @return the firstname
		 */
		public String getFirstname() {
			return firstname;
		}

		/**
		 * @param firstname
		 *            the firstname to set
		 */
		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

		/**
		 * @return the lastname
		 */
		public String getLastname() {
			return lastname;
		}

		/**
		 * @param lastname
		 *            the lastname to set
		 */
		public void setLastname(String lastname) {
			this.lastname = lastname;
		}

		/**
		 * @return the email
		 */
		public String getEmail() {
			return email;
		}

		/**
		 * @param email
		 *            the email to set
		 */
		public void setEmail(String email) {
			this.email = email;
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
		 * @return the userActiveUntil
		 */
		public Date getUserActiveUntil() {
			return userActiveUntil;
		}

		/**
		 * @param userActiveUntil
		 *            the userActiveUntil to set
		 */
		public void setUserActiveUntil(Date userActiveUntil) {
			this.userActiveUntil = userActiveUntil;
		}

		public UserJson getUserSso(UserSso user) {
			UserJson obj = new UserJson();
			obj.username = user.getUsername();
			obj.firstname = user.getFirstname();
			obj.lastname = user.getLastname();
			obj.email = user.getEmail();
			obj.enabled = user.getEnabled();
			obj.userActiveUntil = user.getUserActiveUntil();
			return obj;
		}
	}

	public class RoleAppJSON implements Serializable {

		private static final long serialVersionUID = 1L;
		private Long id;
		private String name;
		private String type;
		private Boolean enabled;
		private Date created;
		private String funcionalidades = "";

		public RoleAppJSON() {

		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
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

		public String getFuncionalidades() {
			return funcionalidades;
		}

		public void setFuncionalidades(String funcionalidades) {
			this.funcionalidades = funcionalidades;
		}

		/**
		 * 
		 */

		public RoleAppJSON getRoleAppJSON(RoleApp roleApp) {
			RoleAppJSON obj = new RoleAppJSON();
			obj.id = roleApp.getId();
			obj.name = roleApp.getName();
			obj.type = roleApp.getType();
			obj.enabled = roleApp.getEnabled();
			obj.created = roleApp.getCreated();
			for (FunctionalityRole functionalityRole : roleApp.getFunctionalityRoles()) {
				obj.funcionalidades += functionalityRole.getName() + ", ";
			}

			if (obj.funcionalidades.length() > 0) {
				obj.funcionalidades = obj.funcionalidades.substring(0, obj.funcionalidades.length() - 2);
			} else {
				obj.funcionalidades = "";
			}

			return obj;
		}

	}

	// public class FunctionalityJSON implements Serializable{
	//
	// private static final long serialVersionUID = 1L;
	// private Long id;
	// private String name;
	// private Boolean enabled;
	// private Date created;
	//
	// public FunctionalityJSON(){
	//
	// }
	//
	// public Long getId() {
	// return id;
	// }
	//
	// public void setId(Long id) {
	// this.id = id;
	// }
	//
	// public String getName() {
	// return name;
	// }
	//
	// public void setName(String name) {
	// this.name = name;
	// }
	//
	// public Boolean getEnabled() {
	// return enabled;
	// }
	//
	// public void setEnabled(Boolean enabled) {
	// this.enabled = enabled;
	// }
	//
	// public Date getCreated() {
	// return created;
	// }
	//
	// public void setCreated(Date created) {
	// this.created = created;
	// }
	//
	// public FunctionalityJSON getFunctionalyJSON(FunctionalityRole
	// functionalityRole){
	// FunctionalityJSON obj = new FunctionalityJSON();
	// obj.id = functionalityRole.getId();
	// obj.name = functionalityRole.getName();
	// obj.enabled = functionalityRole.getEnabled();
	// obj.created = functionalityRole.getCreated();
	// return obj;
	// }
	//
	// }
}
