package com.api.autenticacao.domain.user;

public record RegisterDTO(String login, String password, UserRole role) {
}
