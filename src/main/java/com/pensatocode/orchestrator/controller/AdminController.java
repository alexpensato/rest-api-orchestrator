package com.pensatocode.orchestrator.controller;

import com.pensatocode.orchestrator.model.ApiRegistration;
import com.pensatocode.orchestrator.model.User;
import com.pensatocode.orchestrator.service.ApiRegistrationService;
import com.pensatocode.orchestrator.service.FilterScriptService;
import com.pensatocode.orchestrator.service.TotpService;
import com.pensatocode.orchestrator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ApiRegistrationService apiRegistrationService;

    @Autowired
    private TotpService totpService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilterScriptService filterScriptService;

    @GetMapping
    public Mono<String> adminDashboard(Model model) {
        return Mono.zip(
                apiRegistrationService.getApiCount(),
                userService.getUserCount()
        ).doOnNext(tuple -> {
            model.addAttribute("apiCount", tuple.getT1());
            model.addAttribute("userCount", tuple.getT2());
        }).thenReturn("admin/dashboard");
    }

    @GetMapping("/login")
    public Mono<String> loginPage() {
        return Mono.just("admin/login");
    }

    @PostMapping("/login")
    public Mono<String> verifyTotp(@RequestParam String username, @RequestParam String password, @RequestParam String totpCode) {
        return totpService.verifyCode(username, totpCode)
                .flatMap(isValid -> isValid ? Mono.just("redirect:/admin") : Mono.just("redirect:/admin/login?error"));
    }

    @GetMapping("/users")
    public Mono<String> userList(Model model) {
        return userService.getAllUsers()
                .collectList()
                .doOnNext(users -> model.addAttribute("users", users))
                .thenReturn("admin/user-list");
    }

    @GetMapping("/users/register")
    public Mono<String> showUserRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return Mono.just("admin/user-registration");
    }

    @PostMapping("/users/register")
    public Mono<String> registerUser(@ModelAttribute User user, Model model) {
        return userService.createUser(user)
                .flatMap(savedUser -> totpService.getQrCodeUrl(savedUser.getUsername()))
                .doOnNext(qrCodeUrl -> model.addAttribute("qrCodeUrl", qrCodeUrl))
                .thenReturn("admin/user-registration-complete");
    }

    @GetMapping("/api-registrations")
    public Mono<String> apiRegistrationList(Model model) {
        return apiRegistrationService.getAllApiRegistrations()
                .collectList()
                .doOnNext(registrations -> model.addAttribute("registrations", registrations))
                .thenReturn("admin/api-registration-list");
    }

    @GetMapping("/api-registrations/register")
    public Mono<String> showApiRegistrationForm(Model model) {
        model.addAttribute("apiRegistration", new ApiRegistration());
        return Mono.just("admin/api-registration-form");
    }

    @PostMapping("/api-registrations/register")
    public Mono<String> registerApi(@ModelAttribute ApiRegistration apiRegistration) {
        return apiRegistrationService.createApiRegistration(apiRegistration)
                .thenReturn("redirect:/admin/api-registrations");
    }

    @GetMapping("/api-registrations/edit/{id}")
    public Mono<String> showEditApiForm(@PathVariable Long id, Model model) {
        return apiRegistrationService.getApiRegistrationById(id)
                .doOnNext(registration -> model.addAttribute("apiRegistration", registration))
                .thenReturn("admin/api-registration-form");
    }

    @PostMapping("/api-registrations/edit/{id}")
    public Mono<String> updateApi(@PathVariable Long id, @ModelAttribute ApiRegistration apiRegistration) {
        return apiRegistrationService.updateApiRegistration(id, apiRegistration)
                .thenReturn("redirect:/admin/api-registrations");
    }

    @GetMapping("/api-registrations/delete/{id}")
    public Mono<String> deleteApi(@PathVariable Long id) {
        return apiRegistrationService.deleteApiRegistration(id)
                .thenReturn("redirect:/admin/api-registrations");
    }

    @GetMapping("/filter-scripts")
    public Mono<String> filterScriptsPage(Model model) {
        return apiRegistrationService.getAllApiRegistrations()
                .collectList()
                .doOnNext(endpoints -> model.addAttribute("endpoints", endpoints))
                .thenReturn("admin/filter-scripts");
    }

    @PostMapping("/filter-scripts/{endpoint}")
    @ResponseBody
    public Mono<ResponseEntity<String>> saveFilterScript(@PathVariable String endpoint, @RequestBody String script) {
        return filterScriptService.saveScript(endpoint, script)
                .thenReturn(ResponseEntity.ok("Script saved successfully"));
    }

    @GetMapping("/filter-scripts/{endpoint}")
    @ResponseBody
    public Mono<ResponseEntity<String>> getFilterScript(@PathVariable String endpoint) {
        return filterScriptService.getScript(endpoint)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/filter-scripts/{endpoint}")
    @ResponseBody
    public Mono<ResponseEntity<String>> deleteFilterScript(@PathVariable String endpoint) {
        return filterScriptService.deleteScript(endpoint)
                .thenReturn(ResponseEntity.ok("Script deleted successfully"));
    }

    @GetMapping("/generate-totp/{username}")
    @ResponseBody
    public Mono<ResponseEntity<String>> generateTotp(@PathVariable String username) {
        return totpService.generateSecretKey(username)
                .map(secretKey -> ResponseEntity.ok("TOTP secret generated for " + username));
    }

    @GetMapping("/totp-qr/{username}")
    @ResponseBody
    public Mono<ResponseEntity<String>> getTotpQr(@PathVariable String username) {
        return totpService.getQrCodeUrl(username)
                .map(ResponseEntity::ok);
    }
}
