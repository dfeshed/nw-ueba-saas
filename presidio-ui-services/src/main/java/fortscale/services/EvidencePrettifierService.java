package fortscale.services;

import fortscale.domain.core.Evidence;

/**
 * Created by avivs on 21/01/16.
 */
public interface EvidencePrettifierService<T> {

    T prettify(Evidence evidence);

}
