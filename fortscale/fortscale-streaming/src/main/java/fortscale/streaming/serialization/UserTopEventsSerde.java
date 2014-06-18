package fortscale.streaming.serialization;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.samza.serializers.Serde;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fortscale.streaming.service.UserTopEvents;

public class UserTopEventsSerde implements Serde<UserTopEvents>{
	private static final Logger logger = LoggerFactory.getLogger(UserTopEventsSerde.class);
	
	private ObjectMapper mapper;
	
	public UserTopEventsSerde() {
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Override
	public byte[] toBytes(UserTopEvents arg0) {
		try {
			return mapper.writeValueAsString(arg0).getBytes("UTF-8");
		} catch (UnsupportedEncodingException | JsonProcessingException e) {
			logger.error("error converting model to bytes",  e);
			return null;
		}
	}

	@Override
	public UserTopEvents fromBytes(byte[] arg0) {
		try {
			return mapper.readValue(arg0, UserTopEvents.class);
		} catch (IOException e) {
			logger.error("error converting bytes to model ",  e);
			return null;
		}
	}

}
