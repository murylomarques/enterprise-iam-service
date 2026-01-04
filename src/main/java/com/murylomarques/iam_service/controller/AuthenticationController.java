package com.murylomarques.iam_service.controller;

import com.murylomarques.iam_service.dto.AuthenticationRequest;
import com.murylomarques.iam_service.dto.AuthenticationResponse;
import com.murylomarques.iam_service.dto.RefreshTokenRequest;
import com.murylomarques.iam_service.dto.RegisterRequest;
import com.murylomarques.iam_service.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(service.refreshToken(request));
    }
}