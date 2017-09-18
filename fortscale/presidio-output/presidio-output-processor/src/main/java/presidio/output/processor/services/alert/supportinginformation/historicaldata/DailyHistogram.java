package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import java.time.LocalDate;
import java.util.Map;

public class DailyHistogram<T> {

    LocalDate date;
    Map<T, Double> histogram;

    public DailyHistogram(LocalDate date, Map<T, Double> histogram) {
        this.date = date;
        this.histogram = histogram;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<T, Double> getHistogram() {
        return histogram;
    }

    public void setHistogram(Map<T, Double> histogram) {
        this.histogram = histogram;
    }
}
