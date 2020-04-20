package presidio.data.generators.ssl_subject;

import presidio.data.generators.IBaseGenerator;

public interface ISslSubjectGenerator extends IBaseGenerator<String> {
    String getNext();
}
