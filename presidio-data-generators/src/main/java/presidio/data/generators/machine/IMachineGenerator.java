package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;

public interface IMachineGenerator {
    MachineEntity getNext();
}
