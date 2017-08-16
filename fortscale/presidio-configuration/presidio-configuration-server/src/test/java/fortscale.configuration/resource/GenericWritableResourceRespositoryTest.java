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
public class GenericWritableResourceRespositoryTest {

    private GenericWritableResourceRepository repository;
    private ConfigurableApplicationContext context;
    private NativeEnvironmentRepository nativeEnvironmentRepository;

    @Before
    public void init() {
        this.context = new SpringApplicationBuilder(GenericWritableResourceRespositoryTest.class).web(false).run();
        this.nativeEnvironmentRepository = new NativeEnvironmentRepository(this.context.getEnvironment());
        this.repository = new GenericWritableResourceRepository(this.nativeEnvironmentRepository);
        this.repository.setResourceLoader(this.context);
        this.context.close();
    }


    @Test
    public void storeResource() {
        // don't review. need to be implemented
        ByteArrayResource resource = new ByteArrayResource("{foo:bar}".getBytes());
        this.repository.store("test","default","master","json", resource);
        assertNotNull(this.nativeEnvironmentRepository.findOne("test","default", "master" ));
    }

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }
}
