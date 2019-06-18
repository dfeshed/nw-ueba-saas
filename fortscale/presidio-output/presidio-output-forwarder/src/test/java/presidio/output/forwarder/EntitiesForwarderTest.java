package presidio.output.forwarder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.forwarder.spring.OutputForwarderTestConfigBeans;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
public class EntitiesForwarderTest {

    @Configuration
    @Import(OutputForwarderTestConfigBeans.class)
    static class ContextConfiguration {
        
        @Autowired
        ForwarderConfiguration forwarderConfiguration;

        @Autowired
        ForwarderStrategyFactory forwarderStrategyFactory;

        @Bean
        public EntityPersistencyService entityPersistencyService() {
            Entity entity = new Entity("test", "test1", 90.0d, new ArrayList<>(), new ArrayList<>(), null, EntitySeverity.CRITICAL, 0, "entityType");
            entity.setId("c678bb28-f795-402c-8d64-09f26e82807d");
            EntityPersistencyService entitiesPersistencyService = Mockito.mock(EntityPersistencyService.class);
            Mockito.when(entitiesPersistencyService.findEntitiesByLastUpdateLogicalDateAndEntityType(Mockito.any(Instant.class), Mockito.any(Instant.class), Mockito.any(String.class))).thenReturn(Collections.singletonList(entity).stream());
            return entitiesPersistencyService;
        }

        @Bean
        public EntitiesForwarder entitiesForwarder() {
            return new EntitiesForwarder(entityPersistencyService(), forwarderConfiguration, forwarderStrategyFactory);
        }
    }

    @Autowired
    EntitiesForwarder entitiesForwarder;

    @Autowired
    MemoryStrategy memoryForwarder;


    @Test
    public void testEntitiesForwarding() {
        entitiesForwarder.forwardEntities(Instant.now(), Instant.now(), "entityType");
        Assert.assertEquals(1,memoryForwarder.allMessages.size());
        Assert.assertEquals("{\"id\":\"c678bb28-f795-402c-8d64-09f26e82807d\",\"entitiyId\":\"test\",\"severity\":\"CRITICAL\",\"alertsCount\":0}",memoryForwarder.allMessages.get(0).getPayload());
    }


}
