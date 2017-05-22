package fortscale.services.impl;

import fortscale.domain.DlpFileRecord;
import fortscale.services.api.CoreManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shays on 21/05/2017.
 */
public class ReaderServiceImpl {

    @Autowired
    CoreManagerService coreManagerService;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public void run( String... params) throws Exception{
        log.info("Start csv processing");
        DlpFileRecord dlpFileRecord = new DlpFileRecord();
        coreManagerService.store(dlpFileRecord);
        log.info("Finish csv processing");
    }
}
