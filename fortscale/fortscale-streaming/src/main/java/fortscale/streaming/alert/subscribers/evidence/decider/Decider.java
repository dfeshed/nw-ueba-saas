package fortscale.streaming.alert.subscribers.evidence.decider;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by rans on 14/03/16.
 */
@Component
public class Decider {

    private LinkedList<DeciderCommand> decidersLinkedList;

//    public Decider(Set<String> decidersSet) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
//        for(String deciderCommandName : decidersSet){
//            DeciderCommand deciderCommand = (DeciderCommand)Class.forName(deciderCommandName).newInstance();
//            decidersLinkedList.addLast(deciderCommand);
//        }
//    }

    public LinkedList<DeciderCommand> getDecidersLinkedList() {
        return decidersLinkedList;
    }

    public void setDecidersLinkedList(LinkedList<DeciderCommand> decidersLinkedList) {
        this.decidersLinkedList = decidersLinkedList;
    }


//    public LinkedList<DeciderCommand> getDecidersLinkedList() {
//        return decidersLinkedList;
//    }


}
