package fortscale.utils.flushable;

import java.util.ArrayList;
import java.util.List;

public class FlushableService {

    private List<AbstractFlushable> flushables = new ArrayList<>();

    public void register(AbstractFlushable flushable){
        flushables.add(flushable);
    }


    public void flush(){
        flushables.forEach(flushable -> flushable.flush());
    }



}
