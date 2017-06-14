package presidio.ade.domain.record;

import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.RecordReaderImpl;

/**
 * An ADE record reader factory service that returns a default reader if there is no factory for a certain record.
 *
 * Created by Lior Govrin on 14/06/2017.
 */
public class AdeRecordReaderFactoryService extends FactoryService<RecordReader<AdeRecord>> {
	private static final RecordReader<AdeRecord> defaultAdeRecordReader = new RecordReaderImpl<>();

	@Override
	public RecordReader<AdeRecord> getProduct(FactoryConfig factoryConfig) {
		RecordReader<AdeRecord> adeRecordReader = super.getProduct(factoryConfig);
		return adeRecordReader == null ? defaultAdeRecordReader : adeRecordReader;
	}

	@Override
	public RecordReader<AdeRecord> getDefaultProduct(String factoryName) {
		RecordReader<AdeRecord> adeRecordReader = super.getDefaultProduct(factoryName);
		return adeRecordReader == null ? defaultAdeRecordReader : adeRecordReader;
	}
}
