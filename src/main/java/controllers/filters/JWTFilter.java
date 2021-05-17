package controllers.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import models.database.repositories.StudentRepository;
import models.entities.Student;
import models.statics.Exceptions;

@Component
public class JWTFilter implements Filter {
    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String jwt = request.getHeader("jwtToken");
//        System.out.println("CORSFilter HTTP Request: " + request.getHeader("jwtToken"));
        Claims claims = decodeJWT(jwt);
        Student s = null;
        //IS NULL
        if(claims == null){
            response.setStatus(401);
//            chain.doFilter(request, response);
        } else {
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            if(now.compareTo(claims.getExpiration())>0){
                try {
                    s = StudentRepository.get((String) claims.get("id"), false);
                } catch (Exceptions.StudentNotFound e) {
                    response.setStatus(403);
//                    chain.doFilter(request, response);
                }
            } else {
                response.setStatus(403);
//                chain.doFilter(request, response);
            }
        }
        servletRequest.setAttribute("student", s);
        chain.doFilter(servletRequest,servletResponse);
    }

    public Claims decodeJWT(String token) {
        return Jwts.parser()
                .setSigningKey("bolbolestan")
                .parseClaimsJws(token).getBody();
    }
    @Override
    public void destroy() {}
}
