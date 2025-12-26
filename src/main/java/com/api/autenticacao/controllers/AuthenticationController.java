package com.api.autenticacao.controllers;


import com.api.autenticacao.domain.user.AuthenticationDTO;
import com.api.autenticacao.domain.user.LoginResponseDTO;
import com.api.autenticacao.domain.user.RegisterDTO;
import com.api.autenticacao.domain.user.User;
import com.api.autenticacao.infra.exceptions.ErrorResponseDTO;
import com.api.autenticacao.infra.security.TokenService;
import com.api.autenticacao.repositories.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {

        log.info("Tentativa de login para o usuário: {}", data.login());

        try {
            var usernamePassword =
                    new UsernamePasswordAuthenticationToken(data.login(), data.password());

            var auth = authenticationManager.authenticate(usernamePassword);

            log.info("Login realizado com sucesso para usuário: {}", data.login());

            var token = tokenService.generateToken((User) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (Exception e) {
            log.warn("Falha no login para o usuário: {}", data.login());
            return ResponseEntity.status(401).body(
                    new ErrorResponseDTO(
                            401,
                            "UNAUTHORIZED",
                            "Login ou senha inválidos",
                            Instant.now()
                    )
            );
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {

        log.info("Tentativa de registro do usuário: {}", data.login());

        if (repository.findByLogin(data.login()) != null) {
            log.warn("Registro negado: usuário já existe ({})", data.login());
            return ResponseEntity.status(409).body(
                    new ErrorResponseDTO(
                            409,
                            "CONFLICT",
                            "Usuário já existe",
                            Instant.now()
                    )
            );
        }

        String encryptedPassword =
                new BCryptPasswordEncoder().encode(data.password());

        User newUser =
                new User(data.login(), encryptedPassword, data.role());

        repository.save(newUser);

        log.info("Usuário registrado com sucesso: {}", data.login());

        return ResponseEntity.status(201).build();
    }
}
