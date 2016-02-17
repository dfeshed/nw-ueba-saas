package fortscale.collection.jobs.demo;

/**
 * Created by Amir Keren on 17/02/2016.
 */
public class BaseLineEvents {

    private DemoGenericEvent demoEvent;
    private DemoUtils.DataSource dataSource;

    public DemoGenericEvent getDemoEvent() {
        return demoEvent;
    }

    public void setDemoEvent(DemoGenericEvent demoEvent) {
        this.demoEvent = demoEvent;
    }

    public DemoUtils.DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DemoUtils.DataSource dataSource) {
        this.dataSource = dataSource;
    }

}