package presidio.data.generators.activedirectory;

import presidio.data.generators.domain.event.activedirectory.ActiveDirectoryOperation;

public interface IActiveDirectoryOperationGenerator {
    ActiveDirectoryOperation getNext();
}
