package presidio.ade.domain.record;

import fortscale.utils.recordreader.ReflectionRecordReader;

import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * A record reader for {@link AdeRecord}s.
 *
 * Created by Lior Govrin on 19/06/2017.
 */
public class AdeRecordReader extends ReflectionRecordReader {
	private AdeRecord adeRecord;

	/**
	 * C'tor.
	 *
	 * @param adeRecord          the ADE record from which values are extracted
	 * @param fieldPathDelimiter this ADE record reader's field path delimiter (evaluated as a regular expression)
	 */
	public AdeRecordReader(@NotNull AdeRecord adeRecord, @NotNull String fieldPathDelimiter) {
		super(adeRecord, fieldPathDelimiter);
		this.adeRecord = adeRecord;
	}

	/**
	 * Default c'tor (default field path delimiter is used).
	 *
	 * @param adeRecord the ADE record from which values are extracted
	 */
	public AdeRecordReader(@NotNull AdeRecord adeRecord) {
		super(adeRecord);
		this.adeRecord = adeRecord;
	}

	public Instant getDate_time() {
		return adeRecord.getDate_time();
	}
}
