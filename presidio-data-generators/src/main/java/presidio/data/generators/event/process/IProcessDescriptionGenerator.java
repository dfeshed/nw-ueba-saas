package presidio.data.generators.event.process;

import presidio.data.domain.event.process.ProcessEvent;

public interface IProcessDescriptionGenerator {
    void updateProcessDescription(ProcessEvent processEvent);
}
