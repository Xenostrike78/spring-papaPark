package com.parking.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // ========== Auth Pages (Public) ==========

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // ========== User Pages ==========

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    @GetMapping("/vehicles")
    public String vehiclesPage() {
        return "vehicles";
    }

    @GetMapping("/parking")
    public String parkingPage() {
        return "parking";
    }

    @GetMapping("/payments")
    public String paymentsPage() {
        return "payments";
    }

    @GetMapping("/wallet")
    public String walletPage() {
        return "wallet";
    }

    // ========== Admin Pages ==========

    @GetMapping("/admin/dashboard")
    public String adminDashboardPage() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/slots")
    public String adminSlotsPage() {
        return "admin/slots";
    }

    @GetMapping("/admin/users")
    public String adminUsersPage() {
        return "admin/users";
    }

    @GetMapping("/admin/sessions")
    public String adminSessionsPage() {
        return "admin/sessions";
    }
}
