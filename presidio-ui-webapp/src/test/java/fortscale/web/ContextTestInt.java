package fortscale.web;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/fongo-context-test.xml"})
@WebAppConfiguration( "classpath*:webapp-config.xml"  )
public class ContextTestInt {


	

	
	@Test
	public void testContext() {

	}
}
