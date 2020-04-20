package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntityMappingServiceImpl implements EntityMappingService {

    @Override
    public List<Schema> getSchemas(String entityIdField) {
        if("userId".equals(entityIdField)){
            return Arrays.asList(Schema.AUTHENTICATION, Schema.FILE, Schema.PRINT, Schema.ACTIVE_DIRECTORY,
                    Schema.PROCESS, Schema.REGISTRY, Schema.IOC);
        }

        return Collections.emptyList();
    }

    @Override
    public String getEntityNameField(String entityIdField) {
        if("userId".equals(entityIdField)){
            return "userName";
        }

        return  entityIdField;
    }
}
