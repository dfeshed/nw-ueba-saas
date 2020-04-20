package presidio.data.generators.registryentry;

import presidio.data.domain.RegistryEntry;

public interface IRegistryEntryGenerator {
    RegistryEntry getNext();
}
