package presidio.data.domain;

public class MachineEntity {
    private String machineId;
    private String machineIp;
    private String machineNameRegexCluster;
    private String machineDomain;
    private String machineDomainDN;
    private String domainFQDN;
    private String osVersion;

    public MachineEntity(String machineId, String machineNameRegexCluster, String machineDomain) {
        this.machineId = machineId;
        this.machineNameRegexCluster = machineNameRegexCluster;
        this.machineDomain = machineDomain;
    }

    public MachineEntity(String machineId, String machineIp, String machineNameRegexCluster, String machineDomain) {
        this(machineId, machineNameRegexCluster, machineDomain);
        this.machineIp = machineIp;
    }

    public MachineEntity(String machineId, String machineIP, String machineNameRegexCluster, String machineDomain, String machineDomainDN, String osVersion) {
        this.machineId = machineId;
        this.machineIp = machineIP;
        this.machineNameRegexCluster = machineNameRegexCluster;
        this.machineDomain = machineDomain;
        this.machineDomainDN = machineDomainDN;
        this.osVersion = osVersion;
    }

    public MachineEntity(String machineId, String machineIP, String machineNameRegexCluster, String machineDomain, String machineDomainDN, String domainFQDN, String osVersion) {
        this.machineId = machineId;
        this.machineIp = machineIP;
        this.machineNameRegexCluster = machineNameRegexCluster;
        this.machineDomain = machineDomain;
        this.machineDomainDN = machineDomainDN;
        this.osVersion = osVersion;
        this.domainFQDN = domainFQDN;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getMachineNameRegexCluster() {
        return machineNameRegexCluster;
    }

    public void setMachineNameRegexCluster(String machineNameRegexCluster) {
        this.machineNameRegexCluster = machineNameRegexCluster;
    }

    public String getMachineDomain() {
        return machineDomain;
    }

    public void setMachineDomain(String machineDomain) {
        this.machineDomain = machineDomain;
    }

    public String getMachineDomainDN() {
        return machineDomainDN;
    }

    public void setMachineDomainDN(String machineDomainDN) {
        this.machineDomainDN = machineDomainDN;
    }

    public String getDomainFQDN() {
        return (domainFQDN == null)?"":domainFQDN;
    }

    public void setDomainFQDN(String domainFQDN) {
        this.domainFQDN = domainFQDN;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    @Override
    public String toString() {
        return "MachineEntity{" +
                "machineId='" + machineId + '\'' +
                ", machineIp='" + machineIp + '\'' +
                ", machineNameRegexCluster='" + machineNameRegexCluster + '\'' +
                ", machineDomain='" + machineDomain + '\'' +
                ", machineDomainDN='" + machineDomainDN + '\'' +
                ", domainFQDN='" + domainFQDN + '\'' +
                ", osVersion='" + osVersion + '\'' +
                '}';
    }
}
