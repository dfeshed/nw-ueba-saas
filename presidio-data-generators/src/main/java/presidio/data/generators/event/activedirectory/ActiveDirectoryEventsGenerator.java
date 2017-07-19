package presidio.data.generators.event.activedirectory;

import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.activedirectory.ActiveDirOperationTypeCyclicGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.domain.User;
import presidio.data.generators.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ActiveDirectoryEventsGenerator implements IEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private TimeGenerator timeGenerator;

    private EntityEventIDFixedPrefixGenerator eventIDGen;   // Need this? Can't see in Schemas

    private IUserGenerator userGenerator;
    private ActiveDirOperationTypeCyclicGenerator activeDirOperationTypeGenerator;
    private FixedDataSourceGenerator dataSourceGenerator;
    private BooleanPercentageGenerator isSecuritySensitiveOperationGenerator;
    private BooleanPercentageGenerator isUserAdministratorGenerator;
    private RandomStringGenerator objectNameGenerator;
    private OperationResultPercentageGenerator resultGenerator;

    public ActiveDirectoryEventsGenerator() throws GeneratorException {
        timeGenerator = new TimeGenerator();

        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();

        eventIDGen = new EntityEventIDFixedPrefixGenerator(user.getUsername());

        activeDirOperationTypeGenerator = new ActiveDirOperationTypeCyclicGenerator();
        isSecuritySensitiveOperationGenerator = new BooleanPercentageGenerator(1);  // 1% of operations are security sensitive
        isUserAdministratorGenerator = new BooleanPercentageGenerator(2);                    // 2% of users are administrators
        objectNameGenerator = new RandomStringGenerator(20);                // object name will be random 20 chars length string
        resultGenerator = new OperationResultPercentageGenerator();                          // 100% "Success"
        dataSourceGenerator = new FixedDataSourceGenerator();                                // "Quest"
    }


    public List<ActiveDirectoryEvent> generate () throws GeneratorException {
        List<ActiveDirectoryEvent> evList = new ArrayList<>() ;

        // fill list of events
        while (getTimeGenerator().hasNext()) {
            Instant eventTime = getTimeGenerator().getNext();

            User user = getUserGenerator().getNext();

            ActiveDirectoryEvent ev = new ActiveDirectoryEvent(

                    eventTime,
                    user.getNormalizedUsername(),
                    (String) getActiveDirOperationTypeGenerator().getNext(),
                    getIsSecuritySensitiveOperationGenerator().getNext(),
                    getIsUserAdministratorGenerator().getNext(),
                    getObjectNameGenerator().getNext(),
                    (String) getResultGenerator().getNext(),
                    (String) getDataSourceGenerator().getNext()
                    );

            evList.add(ev);
        }
        return evList;
    }

    public TimeGenerator getTimeGenerator() {
        return timeGenerator;
    }

    public void setTimeGenerator(TimeGenerator timeGenerator) {
        this.timeGenerator = timeGenerator;
    }

    public EntityEventIDFixedPrefixGenerator getEventIDGen() {
        return eventIDGen;
    }

    public void setEventIDGen(EntityEventIDFixedPrefixGenerator eventIDGen) {
        this.eventIDGen = eventIDGen;
    }

    public IUserGenerator getUserGenerator() {
        return userGenerator;
    }

    public void setUserGenerator(IUserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    public ActiveDirOperationTypeCyclicGenerator getActiveDirOperationTypeGenerator() {
        return activeDirOperationTypeGenerator;
    }

    public void setActiveDirOperationTypeGenerator(ActiveDirOperationTypeCyclicGenerator activeDirOperationTypeGenerator) {
        this.activeDirOperationTypeGenerator = activeDirOperationTypeGenerator;
    }

    public FixedDataSourceGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(FixedDataSourceGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public BooleanPercentageGenerator getIsSecuritySensitiveOperationGenerator() {
        return isSecuritySensitiveOperationGenerator;
    }

    public void setIsSecuritySensitiveOperationGenerator(BooleanPercentageGenerator isSecuritySensitiveOperationGenerator) {
        this.isSecuritySensitiveOperationGenerator = isSecuritySensitiveOperationGenerator;
    }

    public BooleanPercentageGenerator getIsUserAdministratorGenerator() {
        return isUserAdministratorGenerator;
    }

    public void setIsUserAdministratorGenerator(BooleanPercentageGenerator isUserAdministratorGenerator) {
        this.isUserAdministratorGenerator = isUserAdministratorGenerator;
    }

    public RandomStringGenerator getObjectNameGenerator() {
        return objectNameGenerator;
    }

    public void setObjectNameGenerator(RandomStringGenerator objectNameGenerator) {
        this.objectNameGenerator = objectNameGenerator;
    }

    public OperationResultPercentageGenerator getResultGenerator() {
        return resultGenerator;
    }

    public void setResultGenerator(OperationResultPercentageGenerator resultGenerator) {
        this.resultGenerator = resultGenerator;
    }
}
