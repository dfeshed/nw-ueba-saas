package fortscale.collection.jobs;

import fortscale.utils.test.category.HadoopTestCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class pxGridJobTest {

	private String keystorePath = "resources/certificates/pxGrid/clientSample2.jks";

	private String keystorePassphrase = "cisco123";

	private String truststorePath = "resources/certificates/pxGrid/rootSample.jks";
	private String truststorePassphrase = "cisco123";

	@Test public void testKeys() throws InstantiationException, IllegalAccessException {
		try {
			keystoreLoadTest(keystorePath, keystorePassphrase);
			keystoreLoadTest(truststorePath, truststorePassphrase);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
	}

	private void keystoreLoadTest(String filename, String password) throws GeneralSecurityException, IOException {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(filename), password.toCharArray());
	}
}
