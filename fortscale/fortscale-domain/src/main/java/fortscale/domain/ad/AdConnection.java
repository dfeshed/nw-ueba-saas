package fortscale.domain.ad;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Amir Keren on 17/05/2015.
 */
public class AdConnection {

	public static final String ACTIVE_DIRECTORY_KEY = "system.activeDirectory.settings";

    @Size(min = 1)
    protected List<String> dcs;

    @NotBlank
	protected String domainBaseSearch;

    @NotBlank
	protected String domainUser;

    @NotBlank
	protected String domainPassword;

    public AdConnection() {}

    public AdConnection(List<String> dcs, String domainBaseSearch, String domainUser, String domainPassword) {
        this.dcs = dcs;
        this.domainBaseSearch = domainBaseSearch;
        this.domainUser = domainUser;
        this.domainPassword = domainPassword;
    }

    public List<String> getDcs() {
        return dcs;
    }

    public void setDcs(List<String> dcs) {
        this.dcs = dcs;
    }

    public String getDomainBaseSearch() {
        return domainBaseSearch;
    }

    public void setDomainBaseSearch(String domainBaseSearch) {
        this.domainBaseSearch = domainBaseSearch;
    }

    public String getDomainUser() {
        return domainUser;
    }

    public void setDomainUser(String domainUser) {
        this.domainUser = domainUser;
    }

    public String getDomainPassword() {
        return domainPassword;
    }

    public void setDomainPassword(String domainPassword) {
        this.domainPassword = domainPassword;
    }

}