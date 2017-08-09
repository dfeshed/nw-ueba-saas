package fortscale.ml.model.store;

import java.time.Instant;

/**
 * Created by barak_schuster on 7/16/17.
 */
public class EmptyModelDao extends ModelDAO{
    public EmptyModelDao(Instant endTime) {
        super(null, null, null, null, endTime);
    }
}
