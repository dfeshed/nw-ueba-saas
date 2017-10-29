package presidio.data.generators.event.activedirectory;


import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.utils.StringUtils;

public class ActiveDirectoryDescriptionGenerator implements IActiveDirectoryDescriptionGenerator{

    private String buildDescription(ActiveDirectoryEvent activeDirectoryEvent){
        String operationType = StringUtils.getFriendlyName(activeDirectoryEvent.getOperation().getOperationType().getName());
        String activeDirectoryDescription = operationType + " for user " + activeDirectoryEvent.getUser().getUsername();
        return activeDirectoryDescription;
    }

    public void updateDescription(ActiveDirectoryEvent activeDirectoryEvent) {
        activeDirectoryEvent.setActiveDirectoryDescription(buildDescription(activeDirectoryEvent));
    }

}
