package com.recruitment.user;

import com.recruitment.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User createUser(UserDto dto) {
        User entity = userMapper.toEntity(dto);
        return userRepository.save(entity);
    }

    public User updateUser(String userId, UserDto dto) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        existing.setUsername(dto.getUsername());
        existing.setPassword(dto.getPassword());
        existing.setRoles(dto.getRoles());
        existing.setActive(dto.isActive());
        return userRepository.save(existing);
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }
}