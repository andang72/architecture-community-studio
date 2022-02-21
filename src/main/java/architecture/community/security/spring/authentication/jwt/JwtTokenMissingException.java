package architecture.community.security.spring.authentication.jwt;

public class JwtTokenMissingException  extends Exception {

    public JwtTokenMissingException(String string) {
        super(string);
    }
    
}
