package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityMappingServiceImpl implements EntityMappingService {

    @Override
    public List<Schema> getSchemas(String entityIdField) {
        if("userId".equals(entityIdField)){
            return Arrays.asList(Schema.AUTHENTICATION, Schema.FILE, Schema.PRINT, Schema.ACTIVE_DIRECTORY,
                    Schema.PROCESS, Schema.REGISTRY, Schema.IOC);
        }

        return new ArrayList<>();
    }

    @Override
    public String getEntityNameField(String entityIdField) {
        if("userId".equals(entityIdField)){
            return "userName";
        }

        return  entityIdField;
    }
}
