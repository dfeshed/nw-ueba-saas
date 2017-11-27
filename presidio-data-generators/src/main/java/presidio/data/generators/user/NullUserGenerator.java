package presidio.data.generators.user;

import presidio.data.domain.User;
import presidio.data.generators.common.GeneratorException;

public class NullUserGenerator implements IUserGenerator {

    public NullUserGenerator() throws GeneratorException {
    }

    public User getNext(){
        return null;
    }
}
