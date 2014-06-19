package fortscale.streaming.serialization;

import org.apache.samza.serializers.Serde;
import java.nio.ByteBuffer;

/**
 * A serializer for long
 */
public class LongSerde implements Serde<Long> {

	@Override
	public byte[] toBytes(Long object) {
		try {
			if (object==null)
				return null;
			else
				return ByteBuffer.allocate(8).putLong(object).array();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Long fromBytes(byte[] bytes) {
		try {
			if (bytes==null)
				return null;
			else
				return ByteBuffer.wrap(bytes).getLong();
		} catch (Exception e) {
			return null;
		}
	}
	
}
