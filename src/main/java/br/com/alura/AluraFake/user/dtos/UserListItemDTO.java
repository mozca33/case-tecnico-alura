package br.com.alura.AluraFake.user.dtos;

import java.io.Serializable;

import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.models.User;

public class UserListItemDTO implements Serializable {

    private String name;
    private String email;
    private Role role;

    public UserListItemDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

}
