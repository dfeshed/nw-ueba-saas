package fortscale.domain.core;

public enum EventResult {
    SUCCESS,
    FAILURE;

    public static EventResult getEventResult(String result) {
        return EventResult.valueOf(result.toUpperCase());
    }
}
