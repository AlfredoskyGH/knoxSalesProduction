package com.stf.oauth.authorization.config;

import com.stf.oauth.admin.entities.RoleApp;
import com.stf.oauth.admin.entities.UserSso;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Provides a basic implementation of the UserDetails interface
 */
public class CustomUserDetails implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Collection<? extends GrantedAuthority> authorities;
    private String password;
    private String username;

    public CustomUserDetails(UserSso user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = translate(user.getRoles());
    }

    /**
     * Translates the List<Role> to a List<GrantedAuthority>
     * @param roles the input list of roles.
     * @return a list of granted authorities
     */
    private Collection<? extends GrantedAuthority> translate(Set<RoleApp> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (RoleApp role : roles) {
            String name = role.getName().toUpperCase();
            //Make sure that all roles start with "ROLE_"
            if (!name.startsWith("ROLE_"))
                name = "ROLE_" + name;
            authorities.add(new SimpleGrantedAuthority(name));
        }
        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
