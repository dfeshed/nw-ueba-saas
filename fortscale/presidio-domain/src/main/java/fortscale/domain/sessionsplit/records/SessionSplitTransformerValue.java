package fortscale.domain.sessionsplit.records;

import fortscale.domain.core.entityattributes.Ja3;
import fortscale.domain.core.entityattributes.SslSubject;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class SessionSplitTransformerValue implements Serializable{

    private int sessionSplit;
    private String sslSubject;
    private List<String> sslCas;
    private String ja3;
    private String ja3s;

    public SessionSplitTransformerValue(int sessionSplit, String sslSubject, List<String> sslCas, String ja3, String ja3s) {
        this.sslSubject = sslSubject;
        this.sslCas = sslCas;
        this.ja3 = ja3;
        this.ja3s = ja3s;
        this.sessionSplit = sessionSplit;
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

    public int getSessionSplit() {
        return sessionSplit;
    }

    public void setSessionSplit(int sessionSplit) {
        this.sessionSplit = sessionSplit;
    }

    public void setSslSubject(String sslSubject) {
        this.sslSubject = sslSubject;
    }

    public void setSslCas(List<String> sslCas) {
        this.sslCas = sslCas;
    }

    public void setJa3(String ja3) {
        this.ja3 = ja3;
    }

    public void setJa3s(String ja3s) {
        this.ja3s = ja3s;
    }
}
