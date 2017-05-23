package presidio.collector.services.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by shays on 17/05/2017.
 */

public interface ReaderService {

    void run(String... params) throws Exception;
}
