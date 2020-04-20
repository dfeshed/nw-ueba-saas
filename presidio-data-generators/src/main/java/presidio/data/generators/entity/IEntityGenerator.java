package presidio.data.generators.entity;

import presidio.data.domain.event.Entity;
import presidio.data.generators.IBaseGenerator;

public interface IEntityGenerator extends IBaseGenerator<Entity> {
    Entity getNext();
}
