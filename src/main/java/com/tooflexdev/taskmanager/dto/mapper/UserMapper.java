package com.tooflexdev.taskmanager.dto.mapper;

import com.tooflexdev.taskmanager.domain.User;
import com.tooflexdev.taskmanager.dto.UserRequestDTO;
import com.tooflexdev.taskmanager.dto.UserResponseDTO;
import com.tooflexdev.taskmanager.domain.Role;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        if (dto.getRoles() != null) {
            user.setRoles(dto.getRoles().stream()
                    .map(roleName -> {
                        Role role = new Role();
                        role.setName(roleName);
                        return role;
                    })
                    .collect(Collectors.toSet())
            );
        }
        return user;
    }
}
