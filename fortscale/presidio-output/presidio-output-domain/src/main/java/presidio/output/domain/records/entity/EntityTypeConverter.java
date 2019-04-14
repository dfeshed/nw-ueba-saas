package presidio.output.domain.records.entity;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class EntityTypeConverter {
    private static final Map<String, String> CONTEXT_FIELD_TO_TYPE = ImmutableMap.of("userId", "user","machineId", "machine");

    public static String getEntityType(String contextField){
        return CONTEXT_FIELD_TO_TYPE.get(contextField);

    }
}
