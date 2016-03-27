package fortscale.streaming.service.machineNormalization;

public class MachineNormalizationFieldsConfig {

    private String normalizationField;
    private String hostnameField;

    public String getHostnameField() {return hostnameField;}
    public void setHostnameField(String hostnameField) {this.hostnameField = hostnameField;}
    public String getNormalizationField() {return normalizationField;}
    public void setNormalizationField(String normalizationField) {this.normalizationField = normalizationField;}

    public MachineNormalizationFieldsConfig(String hostnameField, String normalizationField) {
        setHostnameField(hostnameField);
        setNormalizationField(normalizationField);
    }




}
