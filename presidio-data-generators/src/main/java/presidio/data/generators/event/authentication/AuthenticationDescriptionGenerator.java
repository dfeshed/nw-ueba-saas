package presidio.data.generators.event.authentication;

import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.event.file.IFileDescriptionGenerator;
import presidio.data.generators.utils.StringUtils;

public class AuthenticationDescriptionGenerator implements IAuthenticationDescriptionGenerator{

    private String buildFileDescription(AuthenticationEvent authenticationEvent){
        String operationType = StringUtils.getFriendlyName(authenticationEvent.getOperationType());
        String authenticationDescription = "User " + authenticationEvent.getUser().getUsername() +
                " " + operationType + " to computer " + authenticationEvent.getSrcMachineEntity().getMachineId();
        return authenticationDescription;
    }

    @Override
    public void updateFileDescription(AuthenticationEvent authenticaionEvent) {
        authenticaionEvent.setAuthenticationDescription(buildFileDescription(authenticaionEvent));
    }
}
