package fortscale.streaming.task;

import fortscale.domain.core.EntitySupportingInformation;
import fortscale.domain.core.Evidence;

/**
 * Created by Amir Keren on 02/09/15.
 */
public interface EntitySupportingInformationPopulator {

    EntitySupportingInformation populate(Evidence evidence, String data, boolean isBDPRunning);

}