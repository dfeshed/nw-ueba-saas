package fortscale.utils.pxGrid;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class pxGridTestInt {

	@Test @Ignore
	public void testPrivateKeyGeneration(){
		KeysGenerationHandler handler = new KeysGenerationHandler();
		String password = "P@ssw0rd";
		try {
			/*handler.generatePrivateKey();
			handler.generateCSRrequest();*/
			//handler.generateSelfSignedCert();
			/*handler.generatePKCS12(password);
			handler.importIntoIdentityKeystore(password);
			handler.convertPemToDer();
			handler.addISEIdentityCertToIdentityKeystore(password);
			handler.importPxGridClientCertToIdentityKeystore(password);
			handler.importIseIdentityCertToTrustKeystore(password);*/
			String base64 = handler.generateKeySelfSignedCert();

		}  catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
