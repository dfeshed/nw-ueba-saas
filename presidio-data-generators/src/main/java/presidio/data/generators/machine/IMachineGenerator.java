package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.IBaseGenerator;

public interface IMachineGenerator extends IBaseGenerator<MachineEntity> {
    MachineEntity getNext();
}
