package mysite.com.real.auth.controller;

import lombok.RequiredArgsConstructor;
import mysite.com.real.auth.dto.AuthenticationResponse;
import mysite.com.real.auth.dto.LoginRequest;
import mysite.com.real.auth.dto.RegisterRequest;
import mysite.com.real.auth.service.AuthenticationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}