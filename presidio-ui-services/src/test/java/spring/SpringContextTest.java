package spring;

import fortscale.spring.PresidioUiServiceConfiguration;
import fortscale.utils.mongodb.config.SpringMongoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = {PresidioUiServiceConfiguration.class,  SpringMongoConfiguration.class})
@TestPropertySource("classpath:test.properties")

public class SpringContextTest {


        @Test
        public void contextLoads() {
        }




}
