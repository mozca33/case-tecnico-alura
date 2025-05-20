package br.com.alura.AluraFake.user.mapper;

import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.service.UserService;

import java.util.List;

import org.springframework.stereotype.Component;
import br.com.alura.AluraFake.user.dtos.UserDTO;
import br.com.alura.AluraFake.user.dtos.UserPatchDTO;

@Component
public class UserMapper {

    private final UserService userService;

    UserMapper(UserService userService) {
        this.userService = userService;
    }

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO(user.getName(), user.getEmail(), user.getRole(), null);

        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User(dto.name(), dto.email(), dto.role());

        return user;
    }

    public List<UserDTO> toDTO(List<User> users) {
        return users.stream()
                .map(user -> new UserDTO(user.getName(), user.getEmail(), user.getRole(), null))
                .toList();
    }

    public User toEntity(UserPatchDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        User user = new User(
                userDTO.name() != null ? userDTO.name() : null,
                userDTO.email() != null ? userDTO.email() : null,
                userDTO.role() != null ? userDTO.role() : null);
        userService.validateUser(user);
        return user;
    }
}