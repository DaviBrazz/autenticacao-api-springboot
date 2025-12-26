package com.api.autenticacao.controllers;

import com.api.autenticacao.domain.user.AuthenticationDTO;
import com.api.autenticacao.domain.user.LoginResponseDTO;
import com.api.autenticacao.domain.user.RegisterDTO;
import com.api.autenticacao.domain.user.User;
import com.api.autenticacao.domain.user.UserRole;
import com.api.autenticacao.infra.exceptions.ErrorResponseDTO;
import com.api.autenticacao.infra.security.TokenService;
import com.api.autenticacao.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository repository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --- LOGIN ---

    @Test
    void login_deveRetornarToken_quandoCredenciaisValidas() {
        AuthenticationDTO dto = new AuthenticationDTO("usuario", "senha");
        User user = new User("1", "usuario", "senha", UserRole.USER);
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn("token123");

        ResponseEntity<?> response = controller.login(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((LoginResponseDTO) response.getBody()).token()).isEqualTo("token123");
    }

    @Test
    void login_deveRetornarErro_quandoCredenciaisInvalidas() {
        AuthenticationDTO dto = new AuthenticationDTO("usuario", "senha");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        ResponseEntity<?> response = controller.login(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ErrorResponseDTO body = (ErrorResponseDTO) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(401);
        assertThat(body.message()).isEqualTo("Login ou senha inválidos");
    }

    // --- REGISTER ---

    @Test
    void register_deveCriarUsuario_quandoNaoExistir() {
        RegisterDTO dto = new RegisterDTO("novoUsuario", "senha", UserRole.USER);

        when(repository.findByLogin("novoUsuario")).thenReturn(null);

        ResponseEntity<?> response = controller.register(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void register_deveRetornarErro_quandoUsuarioJaExistir() {
        RegisterDTO dto = new RegisterDTO("existente", "senha", UserRole.USER);
        User existing = new User("1", "existente", "senha", UserRole.USER);

        when(repository.findByLogin("existente")).thenReturn(existing);

        ResponseEntity<?> response = controller.register(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ErrorResponseDTO body = (ErrorResponseDTO) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(409);
        assertThat(body.message()).isEqualTo("Usuário já existe");
    }
}
