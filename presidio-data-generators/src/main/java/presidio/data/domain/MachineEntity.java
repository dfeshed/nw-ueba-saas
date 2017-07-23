package presidio.data.domain;

public class MachineEntity {
    private String name;
    private String normalized_name;
    private String ip_address;
    private boolean remote;

    public MachineEntity(String name, String ip_address) {
        this.name = name;
        this.normalized_name = name;
        this.ip_address = ip_address;
    }

    public MachineEntity(String name, String ip_address, boolean remote) {
        this.name = name;
        this.normalized_name = name;
        this.ip_address = ip_address;
        this.remote = remote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getNormalized_name() { return normalized_name; }

    public void setNormalized_name(String normalized_name) { this.normalized_name = normalized_name; }

    public String getNormalizedMachinename() {
        return normalized_name;
    }

    public boolean isRemote() {
        return remote;
    }
}
