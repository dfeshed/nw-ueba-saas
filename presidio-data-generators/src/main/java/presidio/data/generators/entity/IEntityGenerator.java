package presidio.data.generators.entity;

public interface IEntityGenerator extends IBaseGenerator<Entity> {
    Entity getNext();
}
