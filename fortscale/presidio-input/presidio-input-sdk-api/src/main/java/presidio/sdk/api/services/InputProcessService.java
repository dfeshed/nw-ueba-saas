package presidio.sdk.api.services;


import presidio.sdk.api.domain.AbsEventRecord;

/**
 * Created by shays on 21/05/2017.
 */
public interface InputProcessService {
    boolean store(AbsEventRecord event);

}
