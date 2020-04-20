package presidio.data.generators.event.authentication;

import presidio.data.domain.event.authentication.AuthenticationEvent;

public interface IAuthenticationDescriptionGenerator {
    void updateFileDescription(AuthenticationEvent authenticaionEvent);
}
