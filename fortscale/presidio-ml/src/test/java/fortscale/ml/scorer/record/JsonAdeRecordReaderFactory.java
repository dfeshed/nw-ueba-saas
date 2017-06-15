package fortscale.ml.scorer.record;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.recordreader.RecordReader;
import presidio.ade.domain.record.AdeRecord;

/**
 * A factory that creates {@link JsonAdeRecordReader}s.
 *
 * Created by Lior Govrin on 14/06/2017.
 */
public class JsonAdeRecordReaderFactory extends AbstractServiceAutowiringFactory<RecordReader<AdeRecord>> {
	private static final RecordReader<AdeRecord> defaultProduct = new JsonAdeRecordReader();

	@Override
	public RecordReader<AdeRecord> getProduct(FactoryConfig factoryConfig) {
		return defaultProduct;
	}

	@Override
	public String getFactoryName() {
		return JsonAdeRecord.class.getSimpleName();
	}

	@Override
	public RecordReader<AdeRecord> getDefaultProduct() {
		return defaultProduct;
	}
}
