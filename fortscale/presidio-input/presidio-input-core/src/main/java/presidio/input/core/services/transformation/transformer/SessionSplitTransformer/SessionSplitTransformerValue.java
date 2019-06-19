package presidio.input.core.services.transformation.transformer.SessionSplitTransformer;



import java.time.Instant;
import java.util.List;

public class SessionSplitTransformerValue implements Comparable<SessionSplitTransformerValue> {

    private Instant dateTime;
    private int sessionSplit;
    private String sslSubject;
    private List<String> sslCa;
    private String ja3;

    public SessionSplitTransformerValue(Instant dateTime, int sessionSplit, String sslSubject, List<String> sslCa, String ja3) {
        this.sslSubject = sslSubject;
        this.sslCa = sslCa;
        this.ja3 = ja3;
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

    public List<String> getSslCa() {
        return sslCa;
    }

    public String getJa3() {
        return ja3;
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
