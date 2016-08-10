package fortscale.domain.ad;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Amir Keren on 17/05/2015.
 */
public class AdConnection {

    @Size(min = 1)
    protected List<String> dcs;

    @NotNull
    @NotEmpty
	protected String domainBaseSearch;

    @NotNull
    @NotEmpty
	protected String domainUser;

    @NotNull
    @NotEmpty
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