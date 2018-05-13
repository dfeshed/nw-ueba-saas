package source.sdk;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.flume.source.sdk.EventsStream;
import org.springframework.data.util.CloseableIterator;

import java.time.Instant;
import java.util.Map;

public abstract class AbstractNetwitnessEventsStream implements EventsStream {

    private CloseableIterator<Map<String, Object>> eventsIterator;
    private Schema schema;


    public abstract CloseableIterator<Map<String, Object>> iterator(Schema schema, Instant startDate, Instant endDate);


    public void startStreaming(Schema schema, Instant startDate, Instant endDate){
        this.eventsIterator = iterator(schema, startDate, endDate);
        this.schema = schema;
    }

    public boolean hasNext(){
      return eventsIterator.hasNext();
    }

    public AbstractDocument next(){
        Map<String, Object> event = eventsIterator.next();
        AbstractDocument document = NetwitnessDocumentBuilder.getInstance().buildDocument(schema, event);
        return document;
    }

    public void stopStreaming(){
        eventsIterator.close();
    }

}

