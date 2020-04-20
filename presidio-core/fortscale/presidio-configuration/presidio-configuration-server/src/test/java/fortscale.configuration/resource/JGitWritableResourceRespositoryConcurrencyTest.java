package fortscale.configuration.resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.context.ConfigurableApplicationContext;

@Ignore
public class JGitWritableResourceRespositoryConcurrencyTest {


    private JGitWritableResourceRespositoryConcurrencyTest repository;
    private ConfigurableApplicationContext context;
    private NativeEnvironmentRepository nativeRepository;

    @Before
    public void init() {
        this.context = new SpringApplicationBuilder(JGitWritableResourceRespositoryConcurrencyTest.class).web(WebApplicationType.NONE).run();
        this.nativeRepository = new NativeEnvironmentRepository(this.context.getEnvironment(), null);
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