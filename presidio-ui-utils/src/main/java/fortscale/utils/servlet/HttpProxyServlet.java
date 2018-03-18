package fortscale.utils.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public final class HttpProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private URL url;
	private HttpClient proxy;

	@Override
	public void init(final ServletConfig config) throws ServletException {

		super.init(config);

		try {
			url = new URL(config.getInitParameter("url"));
		} catch (MalformedURLException me) {
			throw new ServletException("Proxy URL is invalid", me);
		}
		proxy = new HttpClient();
		proxy.getHostConfiguration().setHost(url.getHost());
	}

	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		@SuppressWarnings("unchecked")
		Map<String, String[]> requestParameters = request.getParameterMap();

		StringBuilder query = new StringBuilder();
		for (String name : requestParameters.keySet()) {
			for (String value : requestParameters.get(name)) {

				if (query.length() == 0) {
					query.append("?");
				} else {
					query.append("&");
				}

				name = URLEncoder.encode(name, "UTF-8");
				value = URLEncoder.encode(value, "UTF-8");

				query.append(String.format("&%s=%s", name, value));
			}
		}

		String uri = String.format("%s%s", url.toString(), query.toString());
		GetMethod proxyMethod = new GetMethod(uri);

		proxy.executeMethod(proxyMethod);
		write(proxyMethod.getResponseBodyAsStream(), response.getOutputStream());
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		@SuppressWarnings("unchecked")
		Map<String, String[]> requestParameters = request.getParameterMap();

		String uri = url.toString();
		PostMethod proxyMethod = new PostMethod(uri);
		for (String name : requestParameters.keySet()) {
			for (String value : requestParameters.get(name)) {
				proxyMethod.addParameter(name, value);
			}
		}

		proxy.executeMethod(proxyMethod);
		write(proxyMethod.getResponseBodyAsStream(), response.getOutputStream());
	}

	private void write(final InputStream inputStream,
			final OutputStream outputStream) throws IOException {
		int b;
		while ((b = inputStream.read()) != -1) {
			outputStream.write(b);
		}

		outputStream.flush();
	}

	@Override
	public String getServletInfo() {
		return "Http Proxy Servlet";
	}
}
