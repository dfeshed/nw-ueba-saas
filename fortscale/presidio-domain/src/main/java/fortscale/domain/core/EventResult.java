package fortscale.domain.core;

public enum EventResult {
    SUCCESS("success"), FAILURE("failure");

    private String name;

    EventResult(String name) {
        this.name = name;
    }

    public static EventResult getEventResult(String result){
        return EventResult.valueOf(result.toUpperCase());
    }

    public static EventResult createEventResult(String name) throws IllegalArgumentException {
        return EventResult.valueOf(name.toUpperCase());
    }
}
