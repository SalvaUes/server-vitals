package com.svit.server_vitals.config;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final List<String> ADMIN_EMAILS = List.of(
        "zc99001@ues.edu.sv",
        "luis.ibarra@ues.edu.sv",
        "ba22004@ues.edu.sv",
        "rl22021@ues.edu.sv",
        "vl23003@ues.edu.sv"
    );

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User user = super.loadUser(userRequest);
        Map<String, Object> attributes = user.getAttributes();

        String email = (String) attributes.get("email");
        String role = ADMIN_EMAILS.contains(email) ? "ROLE_ADMIN" : "ROLE_USER";

        return new CustomOAuth2User(user, role);
    }
}


