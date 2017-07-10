package presidio.ade.domain.record.enriched;


public class AdeEnrichedActiveDirectoryContext {

    private String normalizedUsername;
    private String result;
    private Boolean isSecuritySensitiveOperation;
    private Boolean isUserAdministrator;
    private String operationType;


    public AdeEnrichedActiveDirectoryContext(EnrichedActiveDirectoryRecord enrichedActiveDirectoryRecord) {
        this.normalizedUsername = enrichedActiveDirectoryRecord.getNormalizedUsername();
        this.result = enrichedActiveDirectoryRecord.getResult();
        this.isSecuritySensitiveOperation = enrichedActiveDirectoryRecord.getSecuritySensitiveOperation();
        this.isUserAdministrator = enrichedActiveDirectoryRecord.getUserAdministrator();
        this.operationType = enrichedActiveDirectoryRecord.getOperationType();
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Boolean getSecuritySensitiveOperation() {
        return isSecuritySensitiveOperation;
    }

    public void setSecuritySensitiveOperation(Boolean securitySensitiveOperation) {
        isSecuritySensitiveOperation = securitySensitiveOperation;
    }

    public Boolean getUserAdministrator() {
        return isUserAdministrator;
    }

    public void setUserAdministrator(Boolean userAdministrator) {
        isUserAdministrator = userAdministrator;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
