package presidio.data.generators.authenticationop;

import presidio.data.domain.event.authentication.AuthenticationOperation;

public interface IAuthenticationOperationGenerator {
    AuthenticationOperation getNext();
}
