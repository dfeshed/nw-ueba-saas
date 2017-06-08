package presidio.ade.domain.record.enriched;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderImpl;

/**
 * This factory creates generic enriched record readers.
 * The generic reader can read any type of enriched record.
 *
 * Created by Lior Govrin on 04/06/2017.
 */
public class EnrichedRecordReaderFactory extends AbstractServiceAutowiringFactory<RecordReader<EnrichedRecord>> {
	public static final String FACTORY_NAME = "enriched_record_reader_factory";
	private static final RecordReader<EnrichedRecord> enrichedRecordReader = new RecordReaderImpl<>();

	@Override
	public String getFactoryName() {
		return FACTORY_NAME;
	}

	@Override
	public RecordReader<EnrichedRecord> getProduct(FactoryConfig factoryConfig) {
		return enrichedRecordReader;
	}
}
