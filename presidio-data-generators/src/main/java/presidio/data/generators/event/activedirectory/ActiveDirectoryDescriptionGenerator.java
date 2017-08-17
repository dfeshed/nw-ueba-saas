package presidio.data.generators.event.activedirectory;


import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;

public class ActiveDirectoryDescriptionGenerator implements IActiveDirectoryDescriptionGenerator{

    private String buildDescription(ActiveDirectoryEvent activeDirectoryEvent){
        String operationType[] = activeDirectoryEvent.getOperation().getOperationType().split(" ");
        String activeDirectoryDescription = operationType[0] + " " + operationType[1] + " " + activeDirectoryEvent.getObjectDN() + " " + operationType[2];
        return activeDirectoryDescription;
    }

    public void updateDescription(ActiveDirectoryEvent activeDirectoryEvent) {
        activeDirectoryEvent.setActiveDirectoryDescription(buildDescription(activeDirectoryEvent));
    }
}
