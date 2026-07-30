package com.parking.system.service.impl;

import com.parking.system.dto.UserDto;
import com.parking.system.entity.User;
import com.parking.system.exception.EntityNotFoundException;
import com.parking.system.exception.ValidationException;
import com.parking.system.repository.UserRepository;
import com.parking.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ValidationException("Email is already registered!");
        }
        if (userRepository.existsByPhone(userDto.getPhone())) {
            throw new ValidationException("Phone number is already registered!");
        }

        User user = User.builder()
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole())
                .build();

        user = userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return mapToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void blockUserIfDuesPending(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        user.setEnabled(true);
        userRepository.save(user);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        dto.setWalletBalance(user.getWalletBalance());
        // Do not map password back
        return dto;
    }
}
