package fortscale.domain.ad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir Keren on 17/05/2015.
 */
public class AdConnection {

    private List<String> ipAddresses;
    private String domainBaseSearch;
    private String domainUser;
    private String domainPassword;

    public AdConnection() {}

    public AdConnection(String ipAddress, String domainBaseSearch, String domainUser, String domainPassword) {
        this.ipAddresses = new ArrayList();
        ipAddresses.add(ipAddress);
        this.domainBaseSearch = domainBaseSearch;
        this.domainUser = domainUser;
        this.domainPassword = domainPassword;
    }

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