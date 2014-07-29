package fortscale.streaming.model.prevalance;

import static fortscale.utils.ConversionUtils.convertToString;
import static fortscale.utils.TimestampUtils.convertToMilliSeconds;

import java.util.List;

import net.minidev.json.JSONObject;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import fortscale.utils.TimestampUtils;

/**
 * Task state the hold a runtime barrier and event discriminator encountered for each user
 */
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class UserTimeBarrier {

	private static Logger logger = LoggerFactory.getLogger(UserTimeBarrier.class);
	
    private long timestamp;
    private String discriminator;

    public long getTimestamp() {
        return timestamp;
    }

    public String getDiscriminator() {
        return discriminator==null? "" : discriminator;
    }

    public boolean isEventAfterBarrier(UserTimeBarrier other) {
    	return (other==null)? false : isEventAfterBarrier(other.getTimestamp(), other.getDiscriminator());
    }
    
    public boolean isEventAfterBarrier(long timestamp, String discriminator) {
		return ((timestamp > this.getTimestamp()) || (timestamp == this.getTimestamp() && !this.getDiscriminator().equals(discriminator)));
	}
    
    public boolean updateBarrier(long timestamp, String discriminator) {
    	// update barrier in case it is not too much in the future
		if (!TimestampUtils.isFutureTimestamp(timestamp, 24)) {
			this.timestamp = convertToMilliSeconds(timestamp);
			this.discriminator = discriminator;
			return true;
		} else {
			logger.error("encountered event in a future time {} [current time={}], skipping barrier update", timestamp, System.currentTimeMillis());
			return false;
		}
    }
    
    public static String calculateDisriminator(JSONObject message, List<String> discriminatorsFields) {
		HashFunction hf = Hashing.md5();
		Hasher hasher = hf.newHasher();
		for (String field : discriminatorsFields) {
			hasher.putString(convertToString(message.get(field)));
		}
		return Long.toString(hasher.hash().asLong());
	}
}
