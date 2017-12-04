package presidio.data.generators.authenticationop;

import presidio.data.domain.event.authentication.AuthenticationOperation;
import presidio.data.generators.common.IOperationTypeGenerator;

public class AuthenticationOperationGenerator implements IAuthenticationOperationGenerator{
    private IOperationTypeGenerator operationTypeGenerator;

    public AuthenticationOperationGenerator() {
        this.operationTypeGenerator = new AuthenticationOperationTypeCyclicGenerator();
    }

    public IOperationTypeGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(IOperationTypeGenerator operationTypeGenerator) {
        this.operationTypeGenerator = operationTypeGenerator;
    }

    @Override
    public AuthenticationOperation getNext() {
        return new AuthenticationOperation(
                getOperationTypeGenerator().getNext()
        );
    }
}
