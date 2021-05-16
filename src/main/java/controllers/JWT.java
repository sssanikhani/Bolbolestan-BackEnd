package controllers;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWT {

    public static String createJWT(String issuer, long ttlMillis) {
        //JWT signature
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date dt = new Date(nowMillis + ttlMillis);
        //JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setIssuer(issuer)
                .setExpiration(dt)
                .signWith(signatureAlgorithm, "bolbolestan");

        return builder.compact();
    }
}