package presidio.output.domain.records.events;

public class ScoredEnrichedUserEvent {

    private EnrichedUserEvent enrichedUserEvent;
    private Double score;

    public ScoredEnrichedUserEvent(EnrichedUserEvent enrichedUserEvent, Double score) {
        this.enrichedUserEvent = enrichedUserEvent;
        this.score = score;
    }

    public EnrichedUserEvent getEnrichedUserEvent() {
        return enrichedUserEvent;
    }

    public void setEnrichedUserEvent(EnrichedUserEvent enrichedUserEvent) {
        this.enrichedUserEvent = enrichedUserEvent;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
