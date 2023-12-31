package com.kohhx.authservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Secret key for JWT token generation
    private final String SECRET = "4E635266556A586E3272357538782F413F4428472B4B6250655367566B597033";
//    private final String SECRET = "7b98d94cf6a9bc809367d528726c597dbedfa1733b7e5c370f8c02fc98ac20d0";

    /**
     * JWT Token Generation section
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Create token based following payloads -> username, signed key and expiration date
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Create sign key based on SECRET KEY declared at the top
    private Key getSignKey() {
        byte[] keyBtytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBtytes);
    }

    /**
     * Logic for JWT Auth filtering for Authentication verification
     */

    // Extract all claims
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    // Function to extract and return back desired field in the claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract username from the incoming JWT token used for authentication
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract Expiration of token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate the token
    public void validateToken(String token) {
        Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token);
    }

//    // Validate the token (Check if username equal to user detail and if token is expired)
//    public Boolean validateToken(String token, UserDetails userDetails){
//        final String username =  extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }

}
