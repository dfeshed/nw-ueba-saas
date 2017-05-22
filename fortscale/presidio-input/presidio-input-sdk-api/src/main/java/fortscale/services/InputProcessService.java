package fortscale.services;

import fortscale.domain.AbsEventRecord;

/**
 * Created by shays on 21/05/2017.
 */
public interface InputProcessService {
    boolean store(AbsEventRecord event);

}
