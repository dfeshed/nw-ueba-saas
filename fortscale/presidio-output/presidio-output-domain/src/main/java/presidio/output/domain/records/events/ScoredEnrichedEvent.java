package presidio.output.domain.records.events;

public class ScoredEnrichedEvent {

    private EnrichedEvent enrichedEvent;
    private Double score;

    public ScoredEnrichedEvent(EnrichedEvent enrichedEvent, Double score) {
        this.enrichedEvent = enrichedEvent;
        this.score = score;
    }

    public EnrichedEvent getEnrichedEvent() {
        return enrichedEvent;
    }

    public void setEnrichedEvent(EnrichedEvent enrichedEvent) {
        this.enrichedEvent = enrichedEvent;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
