package fortscale.domain.ad;

import java.util.List;

/**
 * Created by Amir Keren on 17/05/2015.
 */
public class AdConnection {

    private List<String> ipAddresses;
    private String domainBaseSearch;
    private String domainUser;
    private String domainPassword;

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
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