package presidio.output.commons.services.entity;


import fortscale.common.general.Schema;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
public class EntityMappingServiceTest {

    private EntityMappingService entityMappingService = new EntityMappingServiceImpl();

    @Test
    public void testGetSchemasForUser() {
        List<Schema> schemas = entityMappingService.getSchemas("userId");
        Assert.assertEquals(schemas, Arrays.asList(Schema.AUTHENTICATION, Schema.FILE, Schema.PRINT, Schema.ACTIVE_DIRECTORY,
                Schema.PROCESS, Schema.REGISTRY, Schema.IOC));
    }

    @Test
    public void testGetSchemasForNotUser() {
        List<Schema> schemas = entityMappingService.getSchemas("machineId");
        Assert.assertEquals(schemas, Arrays.asList(Schema.values()));
    }

    @Test
    public void testGetEntityNameForUser() {
        String nameField = entityMappingService.getEntityNameField("userId");
        Assert.assertEquals(nameField, "userName");
    }

    @Test
    public void testGetEntityNameForNotUser() {
        String nameField = entityMappingService.getEntityNameField("machineId");
        Assert.assertEquals(nameField, "machineId");
    }

}
