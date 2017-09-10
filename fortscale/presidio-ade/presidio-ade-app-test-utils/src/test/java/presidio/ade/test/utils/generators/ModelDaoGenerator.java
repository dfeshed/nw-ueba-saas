package presidio.ade.test.utils.generators;

import fortscale.ml.model.Model;
import fortscale.ml.model.store.ModelDAO;
import presidio.data.generators.common.*;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.IEventGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by barak_schuster on 9/10/17.
 */
public class ModelDaoGenerator implements IEventGenerator<ModelDAO> {
    private CyclicValuesGenerator<String> contextIdGenerator;
    private IStringGenerator sessionIdGenerator;
    private ITimeGenerator endTimeGenerator;
    private IModelGenerator modelGenerator;
    private int modelsNumberOfDaysBack;

    public ModelDaoGenerator(IModelGenerator modelGenerator) throws GeneratorException {
        contextIdGenerator = new StringRegexCyclicValuesGenerator("testUser[1-3]{1}");
        sessionIdGenerator= new CustomStringGenerator("testSession");
        endTimeGenerator = new TimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), 1440, 3, 3);
        modelsNumberOfDaysBack = 30;
    }

    @Override
    public List<ModelDAO> generate() throws GeneratorException {
        ArrayList<ModelDAO> evList = new ArrayList<>();

        while (this.endTimeGenerator.hasNext()) {
            String sessionId = sessionIdGenerator.getNext();
            Instant endTime = endTimeGenerator.getNext();
            Instant startTime = endTime.minus(Duration.ofDays(modelsNumberOfDaysBack));
            ModelDAO modelDAO = null;
            for (String contextId : contextIdGenerator.getValues()) {
                Model model = modelGenerator.getNext();
                modelDAO = new ModelDAO(sessionId, contextId, model, startTime, endTime);
            }
            evList.add(modelDAO);
        }

        return evList;
    }

    public void setContextIdGenerator(CyclicValuesGenerator contextIdGenerator) {
        this.contextIdGenerator = contextIdGenerator;
    }

    public void setSessionIdGenerator(IStringGenerator sessionIdGenerator) {
        this.sessionIdGenerator = sessionIdGenerator;
    }

    public void setEndTimeGenerator(ITimeGenerator endTimeGenerator) {
        this.endTimeGenerator = endTimeGenerator;
    }

    public void setModelGenerator(IModelGenerator modelGenerator) {
        this.modelGenerator = modelGenerator;
    }

    public void setModelsNumberOfDaysBack(int modelsNumberOfDaysBack) {
        this.modelsNumberOfDaysBack = modelsNumberOfDaysBack;
    }
}
