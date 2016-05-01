package fortscale.monitoring.metricAdapter;

import fortscale.monitoring.metricAdapter.config.MetricAdapterConfig;
import fortscale.utils.monitoring.stats.models.engine.*;
import org.influxdb.dto.Point;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=MetricAdapterConfig.class)
public class MetricAdapterTest {
    @Autowired
    MetricAdapter metricAdapter;

    @Test
    public void a() throws InterruptedException, IllegalAccessException, NoSuchFieldException {
        metricAdapter.process();
    }
    @Test
    public void ShouldConvertEngineDataToPointsSuccefully() throws Exception {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("theater", "cameri"));
        tags.add(new Tag("show", "Macbeth"));
        List<DoubleField> doubleFields = new ArrayList<>();
        doubleFields.add(new DoubleField("rating", 4.5));
        List<LongField> longFields = new ArrayList<>();
        longFields.add(new LongField("guests", 150000L));
        List<StringField> stringFields = new ArrayList<>();
        stringFields.add(new StringField("quote", "Come what come may, time and the hour runs through the roughest day"));
        Long time = 1460976051L;
        MetricGroup metricGroup = new MetricGroup("shakespeares MetricGroup", "shakespeareClass", time, tags, longFields, doubleFields, stringFields);
        List<MetricGroup> metricGroups = new ArrayList<>();
        metricGroups.add(metricGroup);
        EngineData engineData = new EngineData(100L, metricGroups);
        List<Point> points = MetricAdapter.engineDataToPoints(engineData);
        Assert.assertEquals(points.get(0).toString(), "Point [name=shakespeares MetricGroup, time=1460976051, tags={show=Macbeth, theater=cameri}, precision=SECONDS, fields={guests=150000, quote=Come what come may, time and the hour runs through the roughest day, rating=4.5}, useInteger=true]");
    }
}
