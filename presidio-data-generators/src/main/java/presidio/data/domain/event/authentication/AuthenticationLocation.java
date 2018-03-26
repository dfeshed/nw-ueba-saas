package presidio.data.domain.event.authentication;

import presidio.data.domain.Location;

public class AuthenticationLocation {
    private Location location;

    public AuthenticationLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "AuthenticationLocation{" +
                "location=" + location +
                '}';
    }
}
