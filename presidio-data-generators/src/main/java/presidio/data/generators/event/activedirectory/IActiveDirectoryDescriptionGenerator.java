package presidio.data.generators.event.activedirectory;

import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;

public interface IActiveDirectoryDescriptionGenerator {
    void updateDescription(ActiveDirectoryEvent activeDirectoryEvent);
}
