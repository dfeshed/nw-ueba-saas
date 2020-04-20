package fortscale.utils.pagination.events;

import java.time.Instant;

public class SimpleUserEvent {

    private Instant date;
    private String name;
    private String dataSource;

    public SimpleUserEvent(String name, Instant date, String dataSource) {
        this.name = name;
        this.date = date;
        this.dataSource = dataSource;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
