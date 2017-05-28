package presidio.output.processor.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by shays on 17/05/2017.
 */

public class OutputProcessServiceImpl {

    Logger log = LoggerFactory.getLogger(this.getClass());

    public void run(String... var1) throws Exception{
        log.info("Output processing started");
    }
}
