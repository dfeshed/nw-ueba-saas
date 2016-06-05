package fortscale.domain.ad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir Keren on 17/05/2015.
 */
public class AdConnection {

    private List<String> dcs;
    private String domainBaseSearch;
    private String domainUser;
    private String domainPassword;

    public AdConnection() {}

    public AdConnection(String dc, String domainBaseSearch, String domainUser, String domainPassword) {
        this.dcs = new ArrayList();
        dcs.add(dc);
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