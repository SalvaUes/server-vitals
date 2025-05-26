package com.svit.server_vitals.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String userProfile(Model model, OAuth2AuthenticationToken authentication) {
        model.addAttribute("user", authentication.getPrincipal().getAttributes());
        return "profile";
    }
}