package presidio.data.generators.common;

import presidio.data.domain.event.OperationType;

/**
 * Created by YaronDL on 8/7/2017.
 */
public interface IOperationTypeGenerator {

    OperationType getNext();
}
