package com.rsa.asoc.sa.ui.common.authentication.domain.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Represents a basic user object authenticated from Security Analytics.
 *
 * @author Jay Garala
 * @since 10.6.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailedUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Object principal;
    private String displayName;
    private String description;
    private List<String> authorities;

    /**
     * {@link org.springframework.security.core.userdetails.UserDetails}
     */
    private Map<String, Object> details;

    /**
     * Aggregated permissions on the roles
     */
    private List<String> permissions;

    public DetailedUserInfo() {
    }

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

    public Object getPrincipal() {
        return principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DetailedUserInfo{");
        sb.append("name='").append(name).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", authorities=").append(authorities);
        sb.append(", details=").append(details);
        sb.append(", permissions=").append(permissions);
        sb.append('}');
        return sb.toString();
    }
}
