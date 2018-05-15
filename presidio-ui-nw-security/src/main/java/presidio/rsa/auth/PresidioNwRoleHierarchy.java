package presidio.rsa.auth;

import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;

/**
 * A basic implementation for RoleHierarchy which translate the granted authorities (come from the Token)
 * into UEBA app roles. The roles are the corner stone for authentication in spring boot.
 * If we want the ability to map multiple roles to authority in the future, we need to extend this class
 */
public class PresidioNwRoleHierarchy implements RoleHierarchy {


    public static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        Collection<SimpleGrantedAuthority> grantedRoles = new HashSet<>();
        if (!CollectionUtils.isEmpty(grantedAuthorities)){
            grantedAuthorities.forEach((grantedAuthority)->{
                String authorotyName = grantedAuthority.getAuthority();
                SimpleGrantedAuthority role = new SimpleGrantedAuthority(ROLE_PREFIX +authorotyName);
                grantedRoles.add(role);
            });
        }
        return grantedRoles;
    }


}
