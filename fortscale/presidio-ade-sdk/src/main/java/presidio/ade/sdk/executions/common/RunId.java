package presidio.ade.sdk.executions.common;

import java.util.UUID;

/**
 * identifies the processing execution
 * i.e. 2 historical runs and one online run should have 3 different run id's
 * Created by barak_schuster on 5/18/17.
 */
public class RunId {
    private UUID id;
    public RunId() {
        id = UUID.randomUUID();
    }
}
