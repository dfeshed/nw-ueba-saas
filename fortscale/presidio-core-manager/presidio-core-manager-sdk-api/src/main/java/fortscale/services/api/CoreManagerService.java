package fortscale.services.api;

import fortscale.domain.AbsEventRecord;

/**
 * Created by shays on 21/05/2017.
 */
public interface CoreManagerService {
    boolean store(AbsEventRecord event);

}
