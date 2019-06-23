package presidio.data.generators.common.md5;

import presidio.data.generators.IBaseGenerator;

import java.util.UUID;

public class Md5RandomGenerator implements IBaseGenerator<String> {
    @Override
    public String getNext() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
