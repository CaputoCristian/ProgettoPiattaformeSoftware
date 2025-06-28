package org.example.progetto.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class CustomJwtConverter implements Converter<Jwt, CustomJwt> {

    @Override
    public CustomJwt convert(@NonNull Jwt jwt) {

        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);


        System.out.println(jwt.getClaims());
        var customJwt = new CustomJwt(jwt, authorities);
        customJwt.setEmail(jwt.getClaimAsString("email"));
        customJwt.setFirstName(jwt.getClaimAsString("given_name")); //TODO check
        customJwt.setLastName(jwt.getClaimAsString("family_name"));
        customJwt.setAddress(jwt.getClaimAsString("address"));
        customJwt.setTelephoneNumber(jwt.getClaimAsString("telephoneNumber"));
        customJwt.setBirthDate(jwt.getClaimAsString("birthDate"));
        return customJwt;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        var authorities = new ArrayList<GrantedAuthority>();

        var realm_access = jwt.getClaimAsMap("realm_access");
        if (realm_access != null && realm_access.get("roles") != null) {
            var roles = realm_access.get("roles");
            if (roles instanceof List l) {
                l.forEach(role ->
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                );
            }
        }

        return authorities;
    }
}