package presidio.data.domain.event.authentication;

import presidio.data.domain.event.OperationType;

public class AuthenticationOperation {
    private OperationType operationType;

    public AuthenticationOperation(OperationType operationType) {
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public String toString() {
        return "AuthenticationOperation{" +
                "operationType=" + operationType +
                '}';
    }
}
