package fortscale.utils.pxGrid;

import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class pxGridTestInt {

	@Test
	public void testPrivateKeyGeneration(){
		KeysGenerationHandler handler = new KeysGenerationHandler();
		try {
			handler.generatePrivateKey();
			handler.generateCSRrequest();
			handler.generateSelfSignedCert();
			handler.generatePKCS12();
			handler.importIntoIdentityKeystore();
			handler.convertPemToDer();
			handler.addISEIdentityCertToIdentityKeystore();
			handler.importPxGridClientCertToIdentityKeystore();
			handler.importIseIdentityCertToTrustKeystore();
		}  catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
