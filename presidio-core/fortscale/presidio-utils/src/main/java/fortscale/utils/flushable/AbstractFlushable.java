package fortscale.utils.flushable;

public abstract class AbstractFlushable {

    public void registerFlushableService(FlushableService flushableService){
        flushableService.register(this);
    }

    public abstract void flush();

}
