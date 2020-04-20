package presidio.data.generators.common.random;

import presidio.data.generators.IBaseGenerator;

import java.util.UUID;

public class UUID_RandomGenerator implements IBaseGenerator<String> {
    @Override
    public String getNext() {
        return UUID.randomUUID().toString();
    }
}
