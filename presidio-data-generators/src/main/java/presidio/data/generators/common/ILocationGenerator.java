package presidio.data.generators.common;

import presidio.data.domain.Location;
import presidio.data.generators.entity.IBaseGenerator;

public interface ILocationGenerator extends IBaseGenerator<Location> {

    Location getNext();
}
