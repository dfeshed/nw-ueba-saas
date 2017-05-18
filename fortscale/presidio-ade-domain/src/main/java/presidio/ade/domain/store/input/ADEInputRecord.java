package presidio.ade.domain.store.input;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.Instant;
import java.util.Map;

/**
 * Created by barak_schuster on 5/18/17.
 */
@Document
public class ADEInputRecord {

    @Id
    private String id;
    private Instant creationTime;
    private Instant eventTime;
    private Map<String, Object> fields;

}
