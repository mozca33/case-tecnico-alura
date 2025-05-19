package br.com.alura.AluraFake.user.dtos;

import br.com.alura.AluraFake.user.enums.Role;

public record UserPatchDTO(
        String name,
        String email,
        Role role) {
}