package presidio.ade.test.utils.generators.models;

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
 * Generates {@link ModelDAO} for each of the generated context by {@link this#contextIdGenerator} for each time (by default once a day)
 * Created by barak_schuster on 9/10/17.
 */
public class ModelDaoGenerator implements IEventGenerator<ModelDAO> {
    private static final String DEFAULT_MODEL_SESSION = "testSession";
    private CyclicValuesGenerator<String> contextIdGenerator;
    private IStringGenerator sessionIdGenerator;
    private ITimeGenerator endTimeGenerator;
    private IModelGenerator modelGenerator;
    private int modelsNumberOfDaysBack;

    public ModelDaoGenerator(IModelGenerator modelGenerator) throws GeneratorException {
        this.contextIdGenerator = new StringRegexCyclicValuesGenerator("userId\\#\\#\\#testUser[1-2]{0,1}");
        this.sessionIdGenerator= new CustomStringGenerator(DEFAULT_MODEL_SESSION);
        this.endTimeGenerator = new TimeGenerator(LocalTime.of(0,0),LocalTime.of(23,59),1440,30,1);
        this.modelsNumberOfDaysBack = 30;
        this.modelGenerator = modelGenerator;
    }

    @Override
    public List<ModelDAO> generate() throws GeneratorException {
        ArrayList<ModelDAO> evList = new ArrayList<>();

        while (this.endTimeGenerator.hasNext()) {
            String sessionId = sessionIdGenerator.getNext();
            Instant endTime = endTimeGenerator.getNext();
            Instant startTime = endTime.minus(Duration.ofDays(modelsNumberOfDaysBack));
            for (String contextId : contextIdGenerator.getValues()) {
                Model model = modelGenerator.getNext();
                ModelDAO modelDAO = new ModelDAO(sessionId, contextId, model, startTime, endTime);
                evList.add(modelDAO);
            }
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
