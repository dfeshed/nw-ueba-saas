package fortscale.streaming;

import org.junit.Assert;
import org.junit.Test;

public class SamzaSpringContextTest {

	@Test
	public void resolvingTypeTest(){
		ExtendedSamzaTaskContext context = new ExtendedSamzaTaskContext(null);
		StringBuilder builder = new StringBuilder();
		builder.append("testing SamzaSpringContext type resolving");
		context.registerBean(StringBuilder.class, builder);
		StringBuilder builder1 = context.resolve(StringBuilder.class);
		
		Assert.assertEquals(builder, builder1);		
		
	}
}
