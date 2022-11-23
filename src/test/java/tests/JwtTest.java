package tests;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JwtTest {

    @Test
    public void jwtTest(){
        String username = "1234qwer";
        String token = createJwtToken( username );

        System.out.println("JWT : " + token);
        System.out.println("Username : "+ getUsername(token));

    } 

    static final String SECRET = "ThisIsASecret";
    static final long EXPIRATIONTIME = 1; // 1분

    public String createJwtToken (String username ){
        ZonedDateTime now = ZonedDateTime.now();    
        ZonedDateTime expirationDateTime = now.plus(EXPIRATIONTIME, ChronoUnit.MINUTES);
        Date issueDate = Date.from(now.toInstant());
        Date expirationDate = Date.from(expirationDateTime.toInstant());
        return Jwts.builder().setSubject( username )
        .signWith(SignatureAlgorithm.HS512, SECRET).setIssuedAt(issueDate).setExpiration(expirationDate)
        .compact();
    }


    public String getUsername(String token){
        
        if( !isValid( token ) ){
            return null;
        }

        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.getSubject();

    }
	
	public boolean isValid(String authToken) {
		try {
			Jwts.parser().setSigningKey(SECRET).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) { 
            e.printStackTrace();
		} catch (MalformedJwtException e) { 
            e.printStackTrace();
		} catch (ExpiredJwtException e) { 
            e.printStackTrace();
		} catch (UnsupportedJwtException e) { 
		} catch (IllegalArgumentException e) { 
            e.printStackTrace();
		}
		return false;
	}

}
