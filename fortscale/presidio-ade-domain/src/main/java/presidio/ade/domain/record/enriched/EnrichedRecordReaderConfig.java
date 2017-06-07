package presidio.ade.domain.record.enriched;

import fortscale.utils.factory.FactoryConfig;

/**
 * A configuration for an enriched record reader.
 * There are no specific parameters in this configuration,
 * because the enriched record reader is generic.
 *
 * Created by Lior Govrin on 07/06/2017.
 */
public class EnrichedRecordReaderConfig implements FactoryConfig {
	@Override
	public String getFactoryName() {
		return EnrichedRecordReaderFactory.FACTORY_NAME;
	}
}
