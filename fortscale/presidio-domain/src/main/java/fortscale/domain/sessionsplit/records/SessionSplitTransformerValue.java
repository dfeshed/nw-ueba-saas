package fortscale.domain.sessionsplit.records;

import fortscale.domain.core.entityattributes.Ja3;
import fortscale.domain.core.entityattributes.SslSubject;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class SessionSplitTransformerValue implements Comparable<SessionSplitTransformerValue>, Serializable{

    private Instant dateTime;
    private int sessionSplit;
    private SslSubject sslSubject;
    private List<String> sslCas;
    private Ja3 ja3;
    private String ja3s;

    public SessionSplitTransformerValue(Instant dateTime, int sessionSplit, SslSubject sslSubject, List<String> sslCas, Ja3 ja3, String ja3s) {
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

    public SslSubject getSslSubject() {
        return sslSubject;
    }

    public List<String> getSslCas() {
        return sslCas;
    }

    public Ja3 getJa3() {
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

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public void setSslSubject(SslSubject sslSubject) {
        this.sslSubject = sslSubject;
    }

    public void setSslCas(List<String> sslCas) {
        this.sslCas = sslCas;
    }

    public void setJa3(Ja3 ja3) {
        this.ja3 = ja3;
    }

    public void setJa3s(String ja3s) {
        this.ja3s = ja3s;
    }
}
