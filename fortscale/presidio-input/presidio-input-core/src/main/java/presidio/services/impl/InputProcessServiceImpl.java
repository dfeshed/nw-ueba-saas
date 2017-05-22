package presidio.services.impl;

import presidio.services.api.InputProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by shays on 17/05/2017.
 */

public class InputProcessServiceImpl implements InputProcessService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    public void run(int id, String... var1) throws Exception{

        String name = var1.length>0?var1[0]:"Shay";
        log.info("{} is the king version {}",name ,id+"");
    }
}
