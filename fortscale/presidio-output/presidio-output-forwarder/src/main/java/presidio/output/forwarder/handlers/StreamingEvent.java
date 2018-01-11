package presidio.output.forwarder.handlers;

import java.util.Date;

public class StreamingEvent {

    private Date from;
    private Date to;

    public StreamingEvent(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
}
