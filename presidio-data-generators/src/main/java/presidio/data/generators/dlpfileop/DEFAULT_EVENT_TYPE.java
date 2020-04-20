package presidio.data.generators.dlpfileop;

public enum DEFAULT_EVENT_TYPE {
    FILE_MOVE   ("move"),
    FILE_COPY   ("copy"),
    FILE_DELETE ("delete"),
    FILE_RECYCLE("recycle"),
    FILE_OPEN   ("open");

    public final String value;
    DEFAULT_EVENT_TYPE (String value){
        this.value = value;
    }
}
