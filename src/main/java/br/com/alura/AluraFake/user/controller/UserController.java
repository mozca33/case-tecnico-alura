package br.com.alura.AluraFake.user.controller;

import br.com.alura.AluraFake.user.dtos.UserDTO;
import br.com.alura.AluraFake.user.dtos.UserPatchDTO;
import br.com.alura.AluraFake.user.mapper.UserMapper;
import br.com.alura.AluraFake.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Transactional
    @PostMapping()
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toDTO(
                        userService.createUser(userMapper.toEntity(userDTO))));
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> listAllUsers() {
        return ResponseEntity.ok().body(userMapper.toDTO(userService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(userService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toDTO(
                        userService.updateUser(id, userMapper.toEntity(userDTO))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> patchUser(@PathVariable Long id, @RequestBody UserPatchDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toDTO(
                        userService.updateUser(id, userMapper.toEntity(userDTO))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
