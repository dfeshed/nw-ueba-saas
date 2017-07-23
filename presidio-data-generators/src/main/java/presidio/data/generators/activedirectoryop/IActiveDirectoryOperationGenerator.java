package presidio.data.generators.activedirectoryop;

import presidio.data.domain.event.activedirectory.ActiveDirectoryOperation;

public interface IActiveDirectoryOperationGenerator {
    ActiveDirectoryOperation getNext();
}
