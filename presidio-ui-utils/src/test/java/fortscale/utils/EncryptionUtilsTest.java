package fortscale.utils;

import junit.framework.Assert;
import org.junit.Test;

public class EncryptionUtilsTest {

	@Test
	public void test_enc() throws Exception {
		String enc = EncryptionUtils.encrypt("dotan");
		String dec = EncryptionUtils.decrypt(enc);

		Assert.assertEquals("dotan", dec);
	}

}
