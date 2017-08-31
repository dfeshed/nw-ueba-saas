package presidio.data.generators.event.activedirectory;


import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;

public class ActiveDirectoryDescriptionGenerator implements IActiveDirectoryDescriptionGenerator{

    private String buildDescription(ActiveDirectoryEvent activeDirectoryEvent){
        String operationType = activeDirectoryEvent.getOperation().getOperationType();
        String activeDirectoryDescription = operationType + " for user " + activeDirectoryEvent.getObjectDN();
        return activeDirectoryDescription;
    }

    public void updateDescription(ActiveDirectoryEvent activeDirectoryEvent) {
        activeDirectoryEvent.setActiveDirectoryDescription(buildDescription(activeDirectoryEvent));
    }
}
