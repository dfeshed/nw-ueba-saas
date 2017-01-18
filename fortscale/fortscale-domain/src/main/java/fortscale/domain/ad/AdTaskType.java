package fortscale.domain.ad;


public enum AdTaskType {
    FETCH("Fetch"), ETL("ETL"), FETCH_ETL(FETCH.getType() + "_" + ETL.getType());

    private final String type;

    AdTaskType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
