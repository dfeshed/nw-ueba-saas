package fortscale.collection.jobs.ad;

/**
 * Created by Amir Keren on 17/05/2015.
 */
public class AdConnection {

    private String ip_address;
    private String domain_base_search;
    private String domain_user;
    private String domain_name;
    private String domain_password;

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getDomain_base_search() {
        return domain_base_search;
    }

    public void setDomain_base_search(String domain_base_search) {
        this.domain_base_search = domain_base_search;
    }

    public String getDomain_user() {
        return domain_user;
    }

    public void setDomain_user(String domain_user) {
        this.domain_user = domain_user;
    }

    public String getDomain_password() {
        return domain_password;
    }

    public void setDomain_password(String domain_password) {
        this.domain_password = domain_password;
    }

    public String getDomain_name() {
        return domain_name;
    }

    public void setDomain_name(String domain_name) {
        this.domain_name = domain_name;
    }
}