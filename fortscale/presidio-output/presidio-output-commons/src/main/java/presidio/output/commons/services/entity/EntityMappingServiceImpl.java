package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;
import fortscale.utils.recordreader.RecordReader;
import fortscale.utils.recordreader.ReflectionRecordReader;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EntityMappingServiceImpl implements EntityMappingService {

    @Override
    public List<Schema> getSchemas(String entityType) {
        if(entityType.equals("userId")){
            return Arrays.asList(Schema.AUTHENTICATION, Schema.FILE, Schema.PRINT, Schema.ACTIVE_DIRECTORY,
                    Schema.PROCESS, Schema.REGISTRY, Schema.IOC);
        }

        return Arrays.asList(Schema.values());
    }

    @Override
    public String getEntityName(EnrichedEvent event, String entityType) {
        RecordReader recordReader = new ReflectionRecordReader(event);
        if(Objects.equals(entityType, "userId")){
            return recordReader.get("userName", String.class);
        }

        return recordReader.get(entityType, String.class);
    }
}
