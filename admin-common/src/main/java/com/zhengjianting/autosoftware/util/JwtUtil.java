package com.zhengjianting.autosoftware.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "auto-software-admin.jwt")
public class JwtUtil {
    private long expire;
    private String secret;
    private String header;

    public String generateToken(JwtPayload jwtPayload){
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + 1000 * expire);
        Map<String, Object> claims = new HashMap<>();
        claims.put("payload", jwtPayload);
        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setClaims(claims)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();

    }

    public Claims getClaimByToken(String token){
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e){
            return null;
        }
    }

    public boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }
}
