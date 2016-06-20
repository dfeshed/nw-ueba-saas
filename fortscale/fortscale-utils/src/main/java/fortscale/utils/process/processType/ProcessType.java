package fortscale.utils.process.processType;

/**
 * determine processType. one of the uses is to differ utilities from process
 */
public enum ProcessType {
    UTILITY("Utility"),
    DAEMON ("Daemon");
    private final String text;

    ProcessType(final String text)
    {
        this.text=text;
    }

    // Pretty print process type by overriding "toString"
    @Override
    public String toString() {
        return text;
    }
}
