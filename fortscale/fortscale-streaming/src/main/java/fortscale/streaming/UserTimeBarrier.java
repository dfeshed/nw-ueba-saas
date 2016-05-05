package fortscale.streaming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static fortscale.utils.ConversionUtils.convertToString;
import static fortscale.utils.time.TimestampUtils.convertToMilliSeconds;

/**
 * Task state the hold a runtime barrier and event discriminator encountered for each user
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class UserTimeBarrier {
	private static Logger logger = LoggerFactory.getLogger(UserTimeBarrier.class);

	private long timestamp;
	private String discriminator;

	public long getTimestamp() {
		return timestamp;
	}

	public String getDiscriminator() {
		return discriminator == null ? "" : discriminator;
	}

	public boolean isEventAfterBarrier(long timestamp, String discriminator) {
		long convertedTimestamp = convertTimestamp(timestamp);
		return ((convertedTimestamp > this.getTimestamp()) || (convertedTimestamp == this.getTimestamp() && !this.getDiscriminator().equals(discriminator)));
	}

	private long convertTimestamp(long timestamp) {
		return convertToMilliSeconds(timestamp);
	}

	public boolean updateBarrier(long timestamp, String discriminator) {
		// Update barrier in case it is not too much in the future
		if (!TimestampUtils.isFutureTimestamp(timestamp, 24)) {
			this.timestamp = convertTimestamp(timestamp);
			this.discriminator = discriminator;
			return true;
		} else {
			logger.error("encountered event in a future time {} [current time={}], skipping barrier update", timestamp, System.currentTimeMillis());
			return false;
		}
	}

	public static String calculateDiscriminator(JSONObject message, List<String> discriminatorsFields) {
		HashFunction hf = Hashing.md5();
		Hasher hasher = hf.newHasher();
		for (String field : discriminatorsFields) {
			String fieldValue = convertToString(message.get(field));
			if (fieldValue != null)
				hasher.putString(fieldValue);
		}
		return Long.toString(hasher.hash().asLong());
	}
}
