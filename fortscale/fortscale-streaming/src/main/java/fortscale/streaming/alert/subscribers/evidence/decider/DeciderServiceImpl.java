package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by rans on 14/03/16.
 */
@Component
public class DeciderServiceImpl {

    private LinkedList<DeciderCommand> decidersLinkedList;
    /*static private Map<String,String> alertNameFromType;
    static {
        alertNameFromType = new HashMap<>();
        alertNameFromType.put("smart","Suspected ");
    }

    public String decideAlertName(List<EnrichedFortscaleEvent> evidencesEligibleForDecider){

        DeciderCommand deciderCommand = decidersLinkedList.getFirst();
        if (deciderCommand != null){
            String anomalyTypeForName = deciderCommand.getName(evidencesEligibleForDecider,decidersLinkedList);

        }

    }*/

    public LinkedList<DeciderCommand> getDecidersLinkedList() {
        return decidersLinkedList;
    }

    public void setDecidersLinkedList(LinkedList<DeciderCommand> decidersLinkedList) {
        this.decidersLinkedList = decidersLinkedList;
    }



}
