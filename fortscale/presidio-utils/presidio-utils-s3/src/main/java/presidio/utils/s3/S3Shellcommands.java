package presidio.utils.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.stereotype.Component;
import presidio.utils.s3.services.NWGatewayService;

@Component
public class S3Shellcommands implements CommandMarker {
    @Autowired
    private NWGatewayService nwGatewayService;

}
