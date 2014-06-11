package fortscale.streaming.serialization;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.samza.serializers.Serde;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fortscale.streaming.model.PrevalanceModel;


public class PrevalanceModelSerde implements Serde<PrevalanceModel> {

	private static final Logger logger = LoggerFactory.getLogger(PrevalanceModelSerde.class);
	
	private ObjectMapper mapper;
	
	public PrevalanceModelSerde() {
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	@Override
	public byte[] toBytes(PrevalanceModel model) {
		try {
			return mapper.writeValueAsString(model).getBytes("UTF-8");
		} catch (UnsupportedEncodingException | JsonProcessingException e) {
			logger.error("error converting model to bytes",  e);
			return null;
		}
	}

	@Override
	public PrevalanceModel fromBytes(byte[] bytes) {
		try {
			return mapper.readValue(bytes, PrevalanceModel.class);
		} catch (IOException e) {
			logger.error("error converting bytes to model ",  e);
			return null;
		}
	}
	
	public String toString(PrevalanceModel model) {
		try {
			return mapper.writeValueAsString(model);
		} catch (JsonProcessingException e) {
			logger.error("error converting model to bytes",  e);
			return null;
		}
	}
	
	public PrevalanceModel fromString(String json) {
		try {
			return mapper.readValue(json, PrevalanceModel.class);
		} catch (IOException e) {
			logger.error("error converting bytes to model ",  e);
			return null;
		}
	}

}
