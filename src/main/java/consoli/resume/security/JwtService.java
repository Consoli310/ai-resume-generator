package consoli.resume.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import java.security.Key;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(
            String email
    ) {

        Key key =

                Keys.hmacShaKeyFor(
                        secret.getBytes()
                );

        return Jwts.builder()

                .subject(
                        email
                )

                .issuedAt(
                        new Date()
                )

                .expiration(

                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60
                        )

                )

                .signWith(
                        key,
                        SignatureAlgorithm.HS256
                )

                .compact();
    }

    private Key getKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes()
        );
    }

    public String extractEmail(
            String token
    ) {

        Claims claims =

                Jwts.parser()

                        .verifyWith(
                                (javax.crypto.SecretKey)
                                        getKey()
                        )

                        .build()

                        .parseSignedClaims(
                                token
                        )

                        .getPayload();

        return claims.getSubject();
    }

    public boolean isTokenValid(
            String token
    ) {

        try {

            extractEmail(
                    token
            );

            return true;

        } catch (

                Exception e

        ) {

            return false;
        }
    }
}