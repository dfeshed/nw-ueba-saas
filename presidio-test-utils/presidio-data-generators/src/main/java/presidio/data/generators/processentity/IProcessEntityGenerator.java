package presidio.data.generators.processentity;

import presidio.data.domain.ProcessEntity;

public interface IProcessEntityGenerator {
    ProcessEntity getNext();
}
