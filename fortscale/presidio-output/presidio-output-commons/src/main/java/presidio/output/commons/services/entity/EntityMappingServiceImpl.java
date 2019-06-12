package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EntityMappingServiceImpl implements EntityMappingService {

    @Override
    public List<Schema> getSchemas(String entityType) {
        if(Objects.equals(entityType,"userId")){
            return Arrays.asList(Schema.AUTHENTICATION, Schema.FILE, Schema.PRINT, Schema.ACTIVE_DIRECTORY,
                    Schema.PROCESS, Schema.REGISTRY, Schema.IOC);
        }

        return Arrays.asList(Schema.values());
    }

    @Override
    public String getEntityNameField(String entityIdField) {
        if(Objects.equals(entityIdField, "userId")){
            return "userName";
        }

        return  entityIdField;
    }
}
