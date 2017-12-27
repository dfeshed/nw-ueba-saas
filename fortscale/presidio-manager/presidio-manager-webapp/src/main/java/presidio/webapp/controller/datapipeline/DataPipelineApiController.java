package presidio.webapp.controller.datapipeline;

import fortscale.common.SDK.PipelineState;
import fortscale.utils.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import presidio.manager.api.service.ManagerService;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Controller
public class DataPipelineApiController implements DataPipelineApi {
    private static final Logger logger = Logger.getLogger(DataPipelineApiController.class);

    private final ManagerService managerService;

    public DataPipelineApiController(ManagerService managerService) {
        this.managerService = managerService;
    }

    public ResponseEntity<Void> dataPipelineCleanAndRerunPost() {
        managerService.cleanAndRerun();
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<Void> dataPipelineStartPost() {
        // do some magic!
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<PipelineState> dataPipelineStatusGet() {
        try {
            return new ResponseEntity<PipelineState>(managerService.getPipelineState(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("failed to return data pipline state", e);
            return new ResponseEntity<PipelineState>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Void> dataPipelineStopPost() {
        // do some magic!
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
