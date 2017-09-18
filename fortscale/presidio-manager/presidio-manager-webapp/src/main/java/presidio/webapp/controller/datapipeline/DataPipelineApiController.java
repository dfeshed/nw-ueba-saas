package presidio.webapp.controller.datapipeline;

import fortscale.common.SDK.PipelineState;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import presidio.manager.api.service.ManagerService;
import presidio.webapp.model.datapipeline.CleanupCmd;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Controller
public class DataPipelineApiController implements DataPipelineApi {

    private final ManagerService managerService;

    public DataPipelineApiController(ManagerService managerService) {
        this.managerService = managerService;
    }

    public ResponseEntity<Void> dataPipelineCleanupPost(@ApiParam(value = "" ,required=true ) @RequestBody CleanupCmd body) {
        // do some magic!
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<Void> dataPipelineStartPost() {
        // do some magic!
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<PipelineState> dataPipelineStatusGet() {
        return new ResponseEntity<PipelineState>(managerService.getPipelineState(),HttpStatus.OK);
    }

    public ResponseEntity<Void> dataPipelineStopPost() {
        // do some magic!
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
