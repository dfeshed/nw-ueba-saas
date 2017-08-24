package presidio.data.generators.event.authentication;

import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.event.file.IFileDescriptionGenerator;

public class AuthenticationDescriptionGenerator implements IAuthenticationDescriptionGenerator{

    private String buildFileDescription(AuthenticationEvent authenticationEvent){
        String operationType = authenticationEvent.getOperationType();
        String authenticationDescription = "User " + authenticationEvent.getSrcMachineEntity().getDomainFQDN().toUpperCase() + "\\" + authenticationEvent.getUser().getUsername() +
                " " + operationType + " to computer " + authenticationEvent.getSrcMachineEntity().getMachineId();
        return authenticationDescription;
    }

    @Override
    public void updateFileDescription(AuthenticationEvent authenticaionEvent) {
        authenticaionEvent.setAuthenticationDescription(buildFileDescription(authenticaionEvent));
    }
}
