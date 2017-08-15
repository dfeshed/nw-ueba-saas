package fortscale.configuration.resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ByteArrayResource;

import static org.junit.Assert.assertNotNull;

@Ignore
public class JGitWritableResourceRespositoryTest {

    private JGitWritableResourceRespositoryTest repository;
    private ConfigurableApplicationContext context;
    private NativeEnvironmentRepository nativeRepository;

    @Before
    public void init() {
        this.context = new SpringApplicationBuilder(JGitWritableResourceRespositoryTest.class).web(false).run();
        this.nativeRepository = new NativeEnvironmentRepository(this.context.getEnvironment());
        //this.repository = new GenericResourceWritableRespository(this.nativeRepository);
        //this.repository.setResourceLoader(this.context);
        this.context.close();
    }


    @Test
    public void storeResource() {
        // don't review. need to be implemented
    }


    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }
}
