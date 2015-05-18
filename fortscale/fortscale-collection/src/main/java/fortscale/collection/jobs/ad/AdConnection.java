package fortscale.collection.jobs.ad;

/**
 * Created by Amir Keren on 17/05/2015.
 */
public class AdConnection {

    private String dcAddress;
    private String baseSearch;
    private String username;
    private String password;

    public AdConnection(String dcAddress, String baseSearch, String username, String password) {
        this.dcAddress = dcAddress;
        this.baseSearch = baseSearch;
        this.username = username;
        this.password = password;
    }

    public String getDcAddress() {
        return dcAddress;
    }

    public String getBaseSearch() {
        return baseSearch;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}