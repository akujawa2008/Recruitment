package com.recruitment.user;

import com.recruitment.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = new User("id1", "username", "pass", List.of(Role.valueOf("ADMIN")), true);
        userDto = new UserDto("id1", "username", "pass", List.of(Role.valueOf("ADMIN")), true);
    }

    @Test
    void createUser_shouldSaveUser() {
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.createUser(userDto);
        assertNotNull(saved);
        assertEquals("username", saved.getUsername());
    }

    @Test
    void updateUser_whenNotFound_shouldThrow() {
        when(userRepository.findById("noid")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser("noid", userDto));
    }

    @Test
    void updateUser_whenFound_shouldSave() {
        when(userRepository.findById("id1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updated = userService.updateUser("id1", userDto);
        assertEquals("username", updated.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findById_shouldReturnUser() {
        when(userRepository.findById("id1")).thenReturn(Optional.of(user));
        Optional<User> opt = userService.findById("id1");
        assertTrue(opt.isPresent());
    }

    @Test
    void findAll_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> list = userService.findAll();
        assertEquals(1, list.size());
    }

    @Test
    void deleteUser_whenNotFound_shouldThrow() {
        when(userRepository.existsById("x")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser("x"));
    }

    @Test
    void deleteUser_whenFound_shouldDelete() {
        when(userRepository.existsById("id1")).thenReturn(true);
        userService.deleteUser("id1");
        verify(userRepository, times(1)).deleteById("id1");
    }
}