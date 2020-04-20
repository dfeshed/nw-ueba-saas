package fortscale.presidio.output.client.mock.creators;

import presidio.output.client.model.EntitiesWrapper;
import presidio.output.client.model.Entity;

public class FakeEntitiesCreator {

    private FakeCreatorUtils fakeCreatorUtils;
    private FakeAlertsCreator fakeAlertsCreator;

    public FakeEntitiesCreator(FakeCreatorUtils fakeCreatorUtils, FakeAlertsCreator fakeAlertsCreator) {
        this.fakeCreatorUtils = fakeCreatorUtils;
        this.fakeAlertsCreator = fakeAlertsCreator;
    }

    public Entity getEntity(String id, String name){
        Entity entity = new Entity();
        entity.setId(id);
        entity.setEntityName(name);
        entity.setScore(100);
        entity.setSeverity(Entity.SeverityEnum.CRITICAL);
        entity.setEntityId(id);

        fakeAlertsCreator.getAlerts(10).getAlerts().forEach(alert -> {
            entity.addAlertClassificationsItem(alert.getClassifiation().get(0));
            entity.addAlertsItem(alert);

        });
        entity.setAlertsCount(10);

        return entity;
    }
    public EntitiesWrapper getEntities(int amount){
        EntitiesWrapper entitiesWrapper = new EntitiesWrapper();
        for (int i=0;i<amount;i++) {
            entitiesWrapper.addEntitiesItem(getEntity("1", "Cool User"));

        }
        entitiesWrapper.setTotal(amount);

        return entitiesWrapper;

    }
}
