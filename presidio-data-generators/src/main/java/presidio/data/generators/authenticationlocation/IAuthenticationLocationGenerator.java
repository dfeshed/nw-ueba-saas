package presidio.data.generators.authenticationlocation;

import presidio.data.domain.event.authentication.AuthenticationLocation;

public interface IAuthenticationLocationGenerator {
    AuthenticationLocation getNext();
}
