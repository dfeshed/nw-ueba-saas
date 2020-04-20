package presidio.data.generators.iocentity;

import presidio.data.domain.IocEntity;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;

/**
 * Default generator for IOC entity.
 *
 * **/
public class IocEntityGenerator implements IIocEntityGenerator {

    IStringGenerator iocNameGenerator;
    IStringGenerator iocTacticGenerator;
    IStringGenerator iocLevelGenerator;

    public IocEntityGenerator() throws GeneratorException {
        iocNameGenerator    = new RandomStringGenerator();
        iocTacticGenerator  = new StringCyclicValuesGenerator(IocEntity.IOC_TACTIC.getNames(IocEntity.IOC_TACTIC.class));
        iocLevelGenerator   = new StringCyclicValuesGenerator(IocEntity.IOC_LEVEL.getNames(IocEntity.IOC_LEVEL.class));
    }

    public IocEntity getNext(){

        String name     = iocNameGenerator.getNext();
        String tactic   = iocTacticGenerator.getNext();
        String level    = iocLevelGenerator.getNext();

        return new IocEntity(name, tactic, level);
    }

    public IStringGenerator getIocNameGenerator() {
        return iocNameGenerator;
    }

    public void setIocNameGenerator(IStringGenerator iocNameGenerator) {
        this.iocNameGenerator = iocNameGenerator;
    }

    public IStringGenerator getIocTacticGenerator() {
        return iocTacticGenerator;
    }

    public void setIocTacticGenerator(IStringGenerator iocTacticGenerator) {
        this.iocTacticGenerator = iocTacticGenerator;
    }

    public IStringGenerator getIocLevelGenerator() {
        return iocLevelGenerator;
    }

    public void setIocLevelGenerator(IStringGenerator iocLevelGenerator) {
        this.iocLevelGenerator = iocLevelGenerator;
    }


}
