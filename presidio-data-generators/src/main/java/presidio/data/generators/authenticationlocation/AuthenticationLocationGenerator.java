package presidio.data.generators.authenticationlocation;

import presidio.data.domain.event.authentication.AuthenticationLocation;
import presidio.data.generators.common.ILocationGenerator;

public class AuthenticationLocationGenerator implements IAuthenticationLocationGenerator{
    private ILocationGenerator locationGenerator;

    public AuthenticationLocationGenerator(ILocationGenerator locationGenerator) {
        this.locationGenerator = locationGenerator;
    }

    public ILocationGenerator getLocationGenerator() {
        return locationGenerator;
    }

    public void setLocationGenerator(ILocationGenerator locationGenerator) {
        this.locationGenerator = locationGenerator;
    }

    @Override
    public AuthenticationLocation getNext() {
        return new AuthenticationLocation(
                getLocationGenerator().getNext()
        );
    }
}
