package presidio.input.core.services.api;

public interface InputExecutionService {
    void run() throws Exception;

    boolean init(String... params) throws Exception;
}
