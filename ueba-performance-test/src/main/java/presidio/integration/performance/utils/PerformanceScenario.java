package presidio.integration.performance.utils;

import fortscale.common.general.Schema;
import presidio.data.generators.event.performance.PerformanceStabilityScenario;

import java.time.Instant;

public class PerformanceScenario {
    private final Instant startDay;
    private final Instant endDay;
    private final Schema schema;
    private final PerformanceStabilityScenario scenario;


    PerformanceScenario(Instant startDay, Instant endDay, Schema schema, PerformanceStabilityScenario scenario){
        this.startDay = startDay;
        this.endDay = endDay;
        this.schema = schema;
        this.scenario = scenario;
    }

    public Instant getStartDay() {
        return startDay;
    }

    public Instant getEndDay() {
        return endDay;
    }

    public PerformanceStabilityScenario getScenario() {
        return scenario;
    }

    public Schema getSchema() {
        return schema;
    }

    @Override
    public String toString(){
        return schema.toString().concat(" scenario from ").concat(startDay.toString()).concat(" to ").concat(endDay.toString());
    }
}
