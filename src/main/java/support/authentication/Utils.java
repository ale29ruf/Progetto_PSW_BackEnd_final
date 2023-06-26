package support.authentication;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*Questa classe serve per estrapolare i dati dal token in maniera pulita e semplice */

@UtilityClass
@Log4j2
public class Utils {

    private static String CLIENT_NAME = "server-store";

    public Jwt getPrincipal() {
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String getAuthServerId(){
        return getPrincipal().getClaims().get("sid").toString();
    }

    public String getUsername() {
        return getPrincipal().getClaims().get("preferred_username").toString();
    }

    public String getEmail() {
        return  getPrincipal().getClaims().get("email").toString();
    }

    public String getRole(){
        return JwtAuthenticationConverter.getRole(getPrincipal()).toString();
    }

}
