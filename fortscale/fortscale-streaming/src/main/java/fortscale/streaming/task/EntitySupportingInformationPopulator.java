package fortscale.streaming.task;

import fortscale.domain.core.EntitySupportingInformation;

/**
 * Created by Amir Keren on 02/09/15.
 */
public interface EntitySupportingInformationPopulator {

    EntitySupportingInformation populate(String data);

}