package presidio.collector.services.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.collector.services.api.ReaderService;
import presidio.sdk.api.domain.DlpFileRecord;
import presidio.sdk.api.services.CoreManagerService;

/**
 * Created by shays on 21/05/2017.
 */
public class ReaderServiceImpl extends ReaderService{

    @Autowired
    private CoreManagerService coreManagerService;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public void run( String... params) throws Exception{
        log.info("Start csv processing");
        DlpFileRecord dlpFileRecord = new DlpFileRecord();
        coreManagerService.store(dlpFileRecord);
        log.info("Finish csv processing");
    }
}
