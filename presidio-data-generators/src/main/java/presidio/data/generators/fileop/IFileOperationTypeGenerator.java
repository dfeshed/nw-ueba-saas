package presidio.data.generators.fileop;

import presidio.data.domain.event.OperationType;

/**
 * Created by YaronDL on 8/7/2017.
 */
public interface IFileOperationTypeGenerator {

    OperationType getNext();
}
