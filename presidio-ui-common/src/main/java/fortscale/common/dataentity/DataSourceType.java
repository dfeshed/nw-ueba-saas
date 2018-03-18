package fortscale.common.dataentity;

/**
 * Created by tomerd on 7/31/2017.
 */
public enum DataSourceType {
    LOGON("authentication"),
    ACTIVE_DIRECTORY("active_directory"),
    File("file");

    private String value;

    DataSourceType(String value){
        this.setValue(value);
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return this.getValue();
    }

    public static DataSourceType getEnum(String value){
        for (DataSourceType type : values()){
            if (type.getValue().equalsIgnoreCase(value))
                return type;
        }

        throw new IllegalArgumentException();
    }


}
