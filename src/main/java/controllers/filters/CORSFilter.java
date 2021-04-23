package controllers.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CORSFilter implements Filter {

	private static String webAppOrigin = "http://localhost:3000";

	@Override
	public void doFilter(
		ServletRequest servletRequest,
		ServletResponse servletResponse,
		FilterChain chain
	)
		throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		System.out.println("CORSFilter HTTP Request: " + request.getMethod());

		// Authorize (allow) all domains to consume the content
		((HttpServletResponse) servletResponse).addHeader(
				"Access-Control-Allow-Origin",
				webAppOrigin
			);
		((HttpServletResponse) servletResponse).addHeader(
				"Access-Control-Allow-Methods",
				"GET, OPTIONS, HEAD, PUT, POST, DELETE"
			);
		((HttpServletResponse) servletResponse).addHeader(
				"Access-Control-Allow-Headers",
				"Origin, X-Requested-With, Content-Type, Accept"
			);

		HttpServletResponse resp = (HttpServletResponse) servletResponse;

		// For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
		if (request.getMethod().equals("OPTIONS")) {
			resp.setStatus(HttpServletResponse.SC_ACCEPTED);
			return;
		}

		// pass the request along the filter chain
		chain.doFilter(request, servletResponse);
	}

	@Override
	public void init(FilterConfig fConfig) {}

	@Override
	public void destroy() {}
}
