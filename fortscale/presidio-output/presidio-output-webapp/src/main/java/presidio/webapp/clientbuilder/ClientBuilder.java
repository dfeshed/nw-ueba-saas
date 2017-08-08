package presidio.webapp.clientbuilder;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Arrays;

/**
 * Created by shays on 08/08/2017.
 */
public class ClientBuilder {


    public static void main(String[] args) {

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("./target/generated-sources/swagger/pom.xml"));
        request.setGoals(Arrays.asList("clean", "install"));

        Invoker invoker = new DefaultInvoker();
//        invoker.setMavenHome(new File("C:\\Programs\\apache-maven-3.3.1\\bin"));

        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
    }

}

