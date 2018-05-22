package presidio.nw.flume.sdk;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.flume.source.sdk.EventsStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.CloseableIterator;

import java.time.Instant;
import java.util.Map;

public abstract class AbstractNetwitnessEventsStream implements EventsStream {

    private static Logger logger = LoggerFactory.getLogger(AbstractNetwitnessEventsStream.class);

    private CloseableIterator<Map<String, Object>> eventsIterator;
    private Schema schema;

    public abstract CloseableIterator<Map<String, Object>> iterator(Schema schema, Instant startDate, Instant endDate, Map<String, String> config);


    public void startStreaming(Schema schema, Instant startDate, Instant endDate, Map<String, String> config){
        this.eventsIterator = iterator(schema, startDate, endDate, config);
        this.schema = schema;
    }

    public boolean hasNext(){
      return eventsIterator.hasNext();
    }

    public AbstractDocument next(){
        AbstractDocument document = null;
        Map<String, Object> event = eventsIterator.next();
        if (event != null) {
            document = NetwitnessDocumentBuilder.getInstance().buildDocument(schema, event);
            logger.debug("NW document: " + document);
        }
        return document;
    }

    public void stopStreaming(){
        eventsIterator.close();
    }

}

