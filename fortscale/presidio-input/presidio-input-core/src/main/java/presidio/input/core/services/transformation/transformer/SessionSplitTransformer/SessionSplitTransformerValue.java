package presidio.input.core.services.transformation.transformer.SessionSplitTransformer;



import java.time.Instant;
import java.util.List;

public class SessionSplitTransformerValue implements Comparable<SessionSplitTransformerValue> {

    private Instant dateTime;
    private int sessionSplit;
    private String sslSubject;
    private List<String> sslCas;
    private String ja3;
    private String ja3s;

    public SessionSplitTransformerValue(Instant dateTime, int sessionSplit, String sslSubject, List<String> sslCas, String ja3, String ja3s) {
        this.sslSubject = sslSubject;
        this.sslCas = sslCas;
        this.ja3 = ja3;
        this.ja3s = ja3s;
        this.dateTime = dateTime;
        this.sessionSplit = sessionSplit;
    }

    @Override
    public int compareTo(SessionSplitTransformerValue splitTransformerValue) {
        return (-1) * this.dateTime.compareTo(splitTransformerValue.getDateTime());
    }

    public String getSslSubject() {
        return sslSubject;
    }

    public List<String> getSslCas() {
        return sslCas;
    }

    public String getJa3() {
        return ja3;
    }

    public String getJa3s() {
        return ja3s;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public int getSessionSplit() {
        return sessionSplit;
    }

    public void setSessionSplit(int sessionSplit) {
        this.sessionSplit = sessionSplit;
    }
}
