package presidio.data.domain.event;

import java.time.Instant;

public abstract class Event {
    public abstract String toString();
    public abstract Instant getDateTime();

}
