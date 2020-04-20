package presidio.data.generators.common.md5;

import presidio.data.generators.IBaseGenerator;

public interface Imd5Generator extends IBaseGenerator<String> {
    String getNext();
}
