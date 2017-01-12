package fortscale.streaming.service;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;
import java.util.Set;

/**
 * Created by baraks on 1/5/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FortscaleValueResolverTest {
    @Configuration
    public static class springConfig
    {
        @Bean
        public FortscaleValueResolver fortscaleValueResolver()
        {
            return new FortscaleValueResolver();
        }
        @Bean
        public static TestPropertiesPlaceholderConfigurer FortscaleValueResolverTestPropertiesPlaceholderConfigurer()
        {
            Properties properties = new Properties();
            properties.put("a","aaa");
            properties.put("b","bbb");
            properties.put("c","${a},${b}");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
    @Autowired
    private FortscaleValueResolver fortscaleValueResolver;

    @Autowired
    private Environment environment;

    @Test
    public void resolveStringValueToStringSet() throws Exception {

        Set<String> resolvedSet = fortscaleValueResolver.resolveStringValueToSet("${c}", ",");
        Assert.assertEquals(2,resolvedSet.size());
        Assert.assertTrue(resolvedSet.contains("aaa"));
        Assert.assertTrue(resolvedSet.contains("bbb"));
    }

}