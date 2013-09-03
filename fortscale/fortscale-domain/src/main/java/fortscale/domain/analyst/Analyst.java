package fortscale.domain.analyst;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

import fortscale.domain.core.AbstractDocument;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;





@Document
public class Analyst extends AbstractDocument{
	
	
	@Indexed(unique=true)
	private String userName;
	@Indexed
	private String password;
	
	private final Set<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
	
	private String firstName;
	private String lastName;
	
	@Field("email")
	@Indexed(unique=true)
	private EmailAddress emailAddress;
	
	
	
	
	public Analyst(String userName, String password, EmailAddress emailAddress, String firstName, String lastName , Collection<? extends GrantedAuthority> authorities) {
		this(userName, password, emailAddress, firstName, lastName, true, true, true, true, authorities);
	}
	
	
	/**
	 * Creates a new {@link User} from the given adDn.
	 * 
	 * @param userName must not be {@literal null} or empty.
	 * @param password must not be {@literal null} or empty.
	 * @param emailAddress must not be {@literal null}.
	 * @param firstName must not be {@literal null} or empty.
	 * @param lastName must not be {@literal null} or empty.
	 */
	@PersistenceConstructor
	public Analyst(String userName, String password, EmailAddress emailAddress, String firstName, String lastName,
			boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {

		Assert.hasText(userName);
		Assert.hasText(password);
		Assert.notNull(emailAddress);
		Assert.hasText(firstName);
		Assert.hasText(lastName);

		this.userName = userName;
		this.password = password;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
		
		this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
	}
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public EmailAddress getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(EmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	
	public Set<GrantedAuthority> getAuthorities() {
		return authorities;
	}


	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}


	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}


	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}


	public boolean isEnabled() {
		return enabled;
	}


	private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<GrantedAuthority> sortedAuthorities =
            new TreeSet<GrantedAuthority>(new AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to the set.
            // If the authority is null, it is a custom authority and should precede others.
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }
	
}
