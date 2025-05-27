package com.svit.server_vitals.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Value("${auth0.namespace:https://svits.com/}")
    private String auth0Namespace;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> claims = oidcUser.getClaims();
        
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get(auth0Namespace + "roles");

        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());

        if (roles != null) {
            roles.stream()
                 .map(role -> "ROLE_" + role.toUpperCase())
                 .map(SimpleGrantedAuthority::new)
                 .forEach(authorities::add);
        }

        if (authorities.stream().noneMatch(ga -> ga.getAuthority().startsWith("ROLE_"))) {
             authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Corrección aquí: Obtener la clave del atributo de nombre del userRequest.
        String nameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (nameAttributeKey == null || nameAttributeKey.isEmpty()) {
            // Si no está configurado explícitamente, OidcUserService usa "sub" por defecto.
            // DefaultOidcUser también lo hará si se le pasa null o una cadena vacía aquí,
            // pero es más explícito pasarlo si lo conocemos.
            nameAttributeKey = "sub"; 
        }
        
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), nameAttributeKey);
    }
}