package fortscale.streaming.task;

import fortscale.streaming.service.SpringService;
import junit.framework.TestCase;
import org.springframework.core.env.Environment;

import java.util.List;

public class HDFSWriterStreamTaskTest extends TestCase {

    public void testWrappedInit() throws Exception {

        SpringService.init("classpath*:resources/streamingTest-context.xml");

        Environment env = SpringService.getInstance().resolve(Environment.class);

        List<String> fields = env.getProperty("${impala.score.ldapauth.table.fields}",List.class);

    }
}
