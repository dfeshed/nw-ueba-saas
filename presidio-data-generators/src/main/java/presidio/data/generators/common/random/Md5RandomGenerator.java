package presidio.data.generators.common.random;

import presidio.data.generators.IBaseGenerator;

import java.util.UUID;

public class Md5RandomGenerator implements IBaseGenerator<String> {
    @Override
    public String getNext() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
