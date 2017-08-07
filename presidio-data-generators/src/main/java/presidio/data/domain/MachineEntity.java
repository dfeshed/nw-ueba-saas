package presidio.data.domain;

public class MachineEntity {
    private String machineId;
    private String machineIp;
    private String machineNameRegexCluster;
    private String machineDomain;

    public MachineEntity(String machineId, String machineNameRegexCluster, String machineDomain) {
        this.machineId = machineId;
        this.machineIp = "0.0.0.1";
        this.machineNameRegexCluster = machineNameRegexCluster;
        this.machineDomain = machineDomain;
    }

    public MachineEntity(String machineId, String machineIp, String machineNameRegexCluster, String machineDomain) {
        this.machineId = machineId;
        this.machineIp = machineIp;
        this.machineNameRegexCluster = machineNameRegexCluster;
        this.machineDomain = machineDomain;
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

    @Override
    public String toString() {
        return "MachineEntity{" +
                "machineId='" + machineId + '\'' +
                ", machineIp='" + machineIp + '\'' +
                ", machineNameRegexCluster='" + machineNameRegexCluster + '\'' +
                ", machineDomain='" + machineDomain + '\'' +
                '}';
    }
}
