package com.rsa.asoc.sa.ui.common.authentication.domain.bean;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Contains information about the user's credentials, name, roles and permissions
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class DetailedUserPasswordAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;
    private String name;
    private String displayName;
    private String description;
    private List<String> permissions;

    public DetailedUserPasswordAuthenticationToken(Object principal, Object credentials,
            Collection<GrantedAuthority> authorities, DetailedUserInfo detailedUserInfo) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;

        this.name = detailedUserInfo.getName();
        this.displayName = detailedUserInfo.getDisplayName();
        this.description = detailedUserInfo.getDescription();
        this.permissions = detailedUserInfo.getPermissions();

        setDetails(detailedUserInfo.getDetails());
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}
