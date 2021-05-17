package controllers.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import models.database.LocalVars;

@WebFilter(filterName = "JWTFilter", urlPatterns = { "/student/*", "/offerings/*", "/auth/change-password" })
public class JWTFilter implements Filter {

	@Override
	public void doFilter(
		ServletRequest servletRequest,
		ServletResponse servletResponse,
		FilterChain chain
	)
		throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null) {
			response.setStatus(401);
			return;
		}

		String jwt = authHeader.substring(7); // without Bearer

		Claims claims = Jwts
			.parser()
			.setSigningKey(LocalVars.secretKey)
			.parseClaimsJws(jwt)
			.getBody();

		if (claims == null) {
			response.setStatus(401);
			return;
		}

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		Date exp = claims.getExpiration();
		if (exp.compareTo(now) < 0) {
			response.setStatus(403);
			return;
		}

		String id = (String) claims.get("id");
		servletRequest.setAttribute("id", id);
		chain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {}
}
