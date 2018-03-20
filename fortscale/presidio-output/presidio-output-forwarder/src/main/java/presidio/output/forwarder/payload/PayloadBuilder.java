package presidio.output.forwarder.payload;

public interface PayloadBuilder<T> {

    public String buildPayload(T object) throws Exception;



}
