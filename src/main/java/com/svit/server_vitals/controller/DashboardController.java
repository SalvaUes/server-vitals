package com.svit.server_vitals.controller;

import com.svit.server_vitals.config.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomOAuth2User user) {
        return "dashboard";
    }

    @GetMapping("/admin/config")
    public String adminConfig(@AuthenticationPrincipal CustomOAuth2User user) {
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/dashboard";
        }
        return "admin-config";
    }
}
