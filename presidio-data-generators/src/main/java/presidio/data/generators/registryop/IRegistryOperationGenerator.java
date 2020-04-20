package presidio.data.generators.registryop;

import presidio.data.domain.event.registry.RegistryOperation;

public interface IRegistryOperationGenerator {
    RegistryOperation getNext();
}
