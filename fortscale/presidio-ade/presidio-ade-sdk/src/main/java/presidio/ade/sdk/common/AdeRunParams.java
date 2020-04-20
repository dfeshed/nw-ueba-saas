package presidio.ade.sdk.common;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Barak Schuster
 */
public abstract class AdeRunParams {
    private final Instant startInstant;
    private final Duration timeDelta;

    public AdeRunParams(Instant startInstant, Duration timeDelta) {
        this.startInstant = startInstant;
        this.timeDelta = timeDelta;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Duration getTimeDelta() {
        return timeDelta;
    }
}
