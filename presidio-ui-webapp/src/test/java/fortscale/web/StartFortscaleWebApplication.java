package fortscale.web;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Created by galiar on 09/02/2015.
 */
public class StartFortscaleWebApplication {

	public static void main(final String[] args) throws Exception {

		Server server = new Server(8080);
		String rootPath = StartFortscaleWebApplication.class.getClassLoader().getResource(".").toString();
		WebAppContext webapp = new WebAppContext(rootPath + "../../src/main/webapp", "/fortscale-webapp");
		server.setHandler(webapp);
		server.start();
		server.join();

	}
}
