package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;

public class TransformerTest {

    protected ObjectMapper mapper;

    @Before
    public void init(){
        mapper = new ObjectMapper();
    }

    protected JSONObject transform(IJsonObjectTransformer transformer, JSONObject jsonObject) throws JsonProcessingException {
        return TransformerUtil.transform(mapper, transformer, jsonObject);
    }

    protected JSONObject transform(IJsonObjectTransformer transformer, JSONObject jsonObject, boolean isFilteredOut) throws JsonProcessingException {
        return TransformerUtil.transform(mapper, transformer, jsonObject, isFilteredOut);
    }
}
