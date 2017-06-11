package presidio.ade.sdk.executions.historical;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by barak_schuster on 6/8/17.
 */
public abstract class ADERunParams {
    private final Instant startInstant;
    private final Duration timeDelta;

    public ADERunParams(Instant startInstant, Duration timeDelta) {
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
