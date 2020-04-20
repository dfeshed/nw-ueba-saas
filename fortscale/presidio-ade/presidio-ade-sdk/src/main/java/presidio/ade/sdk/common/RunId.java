package presidio.ade.sdk.common;

import java.util.UUID;

/**
 * Identifies the processing execution (i.e. 2 historical runs and 1 online run should have 3 different run IDs).
 *
 * @author Barak Schuster
 */
public class RunId {
    private final UUID id;

    public RunId() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }
}
