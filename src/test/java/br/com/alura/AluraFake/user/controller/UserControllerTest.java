package br.com.alura.AluraFake.user.controller;

import br.com.alura.AluraFake.user.dtos.UserDTO;
import br.com.alura.AluraFake.user.dtos.UserPatchDTO;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.mapper.UserMapper;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private UserPatchDTO userPatchDTO;
    private List<UserDTO> userDTOList;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO("Test User", "test@example.com", Role.INSTRUCTOR, null);

        userPatchDTO = new UserPatchDTO("PatchedName", null, null);

        userDTOList = Arrays.asList(userDTO);
    }

    @Test
    void createUser_shouldReturnCreatedUser_whenValidUserIsProvided() throws Exception {
        User user = new User();
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(user);
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void listAllUsers_shouldReturnListOfUsers_whenUsersExist() throws Exception {
        User user = new User();
        when(userService.findAll()).thenReturn(Arrays.asList(user));
        when(userMapper.toDTO(anyList())).thenReturn(userDTOList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test User"));
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() throws Exception {
        User user = new User();
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser_whenUserExists() throws Exception {
        User user = new User();
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(user);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void patchUser_shouldReturnPatchedUser_whenValidPatchIsProvided() throws Exception {
        User user = new User();
        when(userMapper.toEntity(any(UserPatchDTO.class))).thenReturn(user);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPatchDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void deleteUser_shouldReturnNoContent_whenUserExists() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}