package br.com.alura.AluraFake.user.dtos;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.models.User;

public record UserDTO(
        @NotNull @Length(min = 3, max = 50) String name,
        @NotNull @NotBlank @Email String email,
        @NotNull Role role,
        @Pattern(regexp = "^$|^.{6}$", message = "Password must be exactly 6 characters long if provided") String password) {

    public User toModel() {
        return new User(name, email, role);
    }

}
