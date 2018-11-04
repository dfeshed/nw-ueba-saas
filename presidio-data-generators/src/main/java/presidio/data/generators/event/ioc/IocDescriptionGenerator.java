package presidio.data.generators.event.ioc;

import presidio.data.domain.event.ioc.IocEvent;

public class IocDescriptionGenerator implements IIocDescriptionGenerator {

    private String buildIocDescription(IocEvent iocEvent){
        return "IOC description - TBD";
    }

    @Override
    public void updateIocDescription(IocEvent iocEvent) {
        iocEvent.setIocEventDescription(buildIocDescription(iocEvent));
    }
}
