package org.apache.flume.interceptor.presidio.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class EpochTimeToNanoRepresentationTransformer extends AbstractJsonObjectTransformer{
    private static final Logger logger = LoggerFactory.getLogger(EpochTimeToNanoRepresentationTransformer.class);
    public static final String TYPE = "epoch";

    private String sourceKey;
    private String destinationKey;


    @JsonCreator
    public EpochTimeToNanoRepresentationTransformer(@JsonProperty("name") String name,
                                                    @JsonProperty("sourceKey") String sourceKey,
                                                    @JsonProperty("destinationKey") String destinationKey){
        super(name);

        this.sourceKey = Validate.notBlank(sourceKey, "sourceKey cannot be blank, empty or null.");
        this.destinationKey = Validate.notBlank(destinationKey, "destinationKey cannot be blank, empty or null.");

    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {

        if (!jsonObject.has(sourceKey)) {
            logger.trace("Field does not exist: {}", sourceKey);
            return jsonObject;
        }

        Object epochTimeObj = jsonObject.get(sourceKey);
        Double epochtime = null;
        if (epochTimeObj instanceof String) {
            try {
                epochtime = Double.valueOf((String) epochTimeObj);
            } catch (NumberFormatException ex) {
                logger.debug("The value in field {} is of type string and can not be converted to Double. The value expected to be epoch time. event: {}",
                        sourceKey, jsonObject);
            }
        } else if (epochTimeObj instanceof Number) {
            epochtime = ((Number)epochTimeObj).doubleValue();
        } else {
            logger.debug("The value in field {} is of the wrong type {}. The value expected to be epoch time. event: {}",
                    sourceKey, epochTimeObj.getClass(), jsonObject);
        }

        if (epochtime != null) {
            jsonObject.put(destinationKey, convertEpochToNanoRepresentation(epochtime));
        }

        return jsonObject;
    }

    public static Double convertEpochToNanoRepresentation(Double epochtime) {
        if (epochtime.longValue() == epochtime.doubleValue()) {// if the epochtime is not a long value then we assume nano representation.
            if (!TimestampUtils.isTimestampInSeconds(epochtime.longValue())) {
                epochtime = epochtime * 0.001;
            }
        }
        return epochtime;
    }
}
