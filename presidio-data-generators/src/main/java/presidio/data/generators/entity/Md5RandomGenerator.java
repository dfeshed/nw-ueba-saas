package presidio.data.generators.entity;

import java.util.UUID;

public class Md5RandomGenerator implements Imd5Generator {
    @Override
    public String getNext() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
