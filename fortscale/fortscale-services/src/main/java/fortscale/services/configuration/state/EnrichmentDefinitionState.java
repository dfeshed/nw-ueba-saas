package fortscale.services.configuration.state;

/**
 * @author gils
 * 31/12/2015
 */
public class EnrichmentDefinitionState implements GDSConfigurationState{

    private UserNormalizationState userNormalizationState = new UserNormalizationState();
    private IPResolvingState ipResolvingState = new IPResolvingState();

    public UserNormalizationState getUserNormalizationState() {
        return userNormalizationState;
    }

    public IPResolvingState getIpResolvingState() {
        return ipResolvingState;
    }

    @Override
    public void reset() {
    }

    public static class UserNormalizationState {
        private String userNameField;
        private String domainField;
        private String domainValue;
        private String normalizedUserNameField;
        private String normalizeServiceName;
        private String updateOnly;

        public String getUserNameField() {
            return userNameField;
        }

        public void setUserNameField(String userNameField) {
            this.userNameField = userNameField;
        }

        public String getDomainField() {
            return domainField;
        }

        public void setDomainField(String domainField) {
            this.domainField = domainField;
        }

        public String getDomainValue() {
            return domainValue;
        }

        public void setDomainValue(String domainValue) {
            this.domainValue = domainValue;
        }

        public String getNormalizedUserNameField() {
            return normalizedUserNameField;
        }

        public void setNormalizedUserNameField(String normalizedUserNameField) {
            this.normalizedUserNameField = normalizedUserNameField;
        }

        public String getNormalizeServiceName() {
            return normalizeServiceName;
        }

        public void setNormalizeServiceName(String normalizeServiceName) {
            this.normalizeServiceName = normalizeServiceName;
        }

        public String getUpdateOnly() {
            return updateOnly;
        }

        public void setUpdateOnly(String updateOnly) {
            this.updateOnly = updateOnly;
        }
    }

    public static class IPResolvingState {

        private boolean restrictToAD;
        private boolean shortNameUsage;
        private boolean dropOnFailUsage;
        private boolean overrideIpWithHostNameUsage;
        private String ipField;
        private String hostField;

        public boolean isRestrictToAD() {
            return restrictToAD;
        }

        public void setRestrictToAD(boolean restrictToAD) {
            this.restrictToAD = restrictToAD;
        }

        public boolean isShortNameUsage() {
            return shortNameUsage;
        }

        public void setShortNameUsage(boolean shortNameUsage) {
            this.shortNameUsage = shortNameUsage;
        }

        public boolean isDropOnFailUsage() {
            return dropOnFailUsage;
        }

        public void setDropOnFailUsage(boolean dropOnFailUsage) {
            this.dropOnFailUsage = dropOnFailUsage;
        }

        public boolean isOverrideIpWithHostNameUsage() {
            return overrideIpWithHostNameUsage;
        }

        public void setOverrideIpWithHostNameUsage(boolean overrideIpWithHostNameUsage) {
            this.overrideIpWithHostNameUsage = overrideIpWithHostNameUsage;
        }

        public String getIpField() {
            return ipField;
        }

        public void setIpField(String ipField) {
            this.ipField = ipField;
        }

        public String getHostField() {
            return hostField;
        }

        public void setHostField(String hostField) {
            this.hostField = hostField;
        }
    }
}
