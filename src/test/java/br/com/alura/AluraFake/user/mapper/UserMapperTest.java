package br.com.alura.AluraFake.user.mapper;

import br.com.alura.AluraFake.user.dtos.UserDTO;
import br.com.alura.AluraFake.user.dtos.UserPatchDTO;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserMapperTest {

    private UserService userService;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userMapper = new UserMapper(userService);
    }

    @Test
    void toDTO_shouldReturnNull_whenUserIsNull() {
        assertNull(userMapper.toDTO((User) null));
    }

    @Test
    void toDTO_shouldMapUserToUserDTO_whenUserIsValid() {
        User user = new User("test", "test@example.com", Role.INSTRUCTOR, null);
        UserDTO dto = userMapper.toDTO(user);

        assertNotNull(dto);
        assertEquals("test", dto.name());
        assertEquals("test@example.com", dto.email());
        assertEquals(Role.INSTRUCTOR, dto.role());
        assertNull(dto.password());
    }

    @Test
    void toEntity_shouldReturnNull_whenUserDTOIsNull() {
        assertNull(userMapper.toEntity((UserDTO) null));
    }

    @Test
    void toEntity_shouldMapUserDTOToUser_whenDTOIsValid() {
        UserDTO dto = new UserDTO("test", "test@example.com", Role.STUDENT, null);
        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("test", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(Role.STUDENT, user.getRole());
    }

    @Test
    void toDTO_shouldReturnListOfDTOs_whenUserListIsValid() {
        User user1 = new User("User1", "user1@example.com", Role.INSTRUCTOR);
        User user2 = new User("User2", "user2@example.com", Role.INSTRUCTOR);
        List<UserDTO> dtos = userMapper.toDTO(List.of(user1, user2));

        assertEquals(2, dtos.size());
        assertEquals("User1", dtos.get(0).name());
        assertEquals("user2@example.com", dtos.get(1).email());
    }

    @Test
    void toEntityFromPatchDTO_shouldMapPatchDTOToUser_whenAllFieldsPresent() {
        UserPatchDTO patchDTO = new UserPatchDTO("Patched Name", "patched@example.com", Role.INSTRUCTOR);
        doNothing().when(userService).validateUser(any(User.class));

        User user = userMapper.toEntity(patchDTO);

        assertNotNull(user);
        assertEquals("Patched Name", user.getName());
        assertEquals("patched@example.com", user.getEmail());
        assertEquals(Role.INSTRUCTOR, user.getRole());
        verify(userService, times(1)).validateUser(any(User.class));
    }

    @Test
    void toEntityFromPatchDTO_shouldSetEmptyStrings_whenFieldsAreNull() {
        UserPatchDTO patchDTO = new UserPatchDTO(null, null, null);
        doNothing().when(userService).validateUser(any(User.class));

        User user = userMapper.toEntity(patchDTO);

        assertNotNull(user);
        assertEquals(null, user.getName());
        assertEquals(null, user.getEmail());
        assertNull(user.getRole());
        verify(userService, times(1)).validateUser(any(User.class));
    }

    @Test
    void toEntityFromPatchDTO_shouldReturnNull_whenPatchDTOIsNull() {
        assertNull(userMapper.toEntity((UserPatchDTO) null));
    }
}