package com.svit.server_vitals.controller;

import com.svit.server_vitals.config.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String adminHome(@AuthenticationPrincipal CustomOAuth2User user) {
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/dashboard";
        }
        return "admin-home";
    }

    @GetMapping("/admin/settings")
    public String adminSettings(@AuthenticationPrincipal CustomOAuth2User user) {
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/dashboard";
        }
        return "admin-settings";
    }
}
