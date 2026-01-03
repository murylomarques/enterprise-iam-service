package com.murylomarques.iam_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {

    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Olá Usuário Comum! Você está logado.");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // <--- A MÁGICA ACONTECE AQUI
    public ResponseEntity<String> sayHelloAdmin() {
        return ResponseEntity.ok("Olá ADMIN! Se você vê isso, você manda no sistema! 👮‍♂️");
    }
}