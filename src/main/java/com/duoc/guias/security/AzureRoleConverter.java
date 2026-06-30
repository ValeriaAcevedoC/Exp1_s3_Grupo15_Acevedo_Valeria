package com.duoc.guias.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.List;

public class AzureRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // System.out.println(jwt.getClaims());

        String rol = jwt.getClaimAsString("extension_consultaRole");

        if (rol != null) {

            if (rol.equalsIgnoreCase("admin")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            if (rol.equalsIgnoreCase("descarga")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_DESCARGA"));
            }
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}