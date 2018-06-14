package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import java.time.LocalDate;
import java.util.Map;

public class DailyHistogram<T, E> {

    LocalDate date;
    Map<T, E> histogram;

    public DailyHistogram(LocalDate date, Map<T, E> histogram) {
        this.date = date;
        this.histogram = histogram;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<T, E> getHistogram() {
        return histogram;
    }

    public void setHistogram(Map<T, E> histogram) {
        this.histogram = histogram;
    }
}
