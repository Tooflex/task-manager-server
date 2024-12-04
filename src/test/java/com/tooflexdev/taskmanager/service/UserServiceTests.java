package com.tooflexdev.taskmanager.service;

import com.tooflexdev.taskmanager.domain.Role;
import com.tooflexdev.taskmanager.domain.User;
import com.tooflexdev.taskmanager.dto.UserRequestDTO;
import com.tooflexdev.taskmanager.dto.UserResponseDTO;
import com.tooflexdev.taskmanager.repository.RoleRepository;
import com.tooflexdev.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistentUser"));
        verify(userRepository, times(1)).findByUsername("nonexistentUser");
    }

    @Test
    void testGetAllUsers() {
        Pageable pageable = Pageable.unpaged();
        Page<User> mockPage = new PageImpl<>(Collections.emptyList());
        when(userRepository.findAll(pageable)).thenReturn(mockPage);

        Page<UserResponseDTO> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetUserById_UserExists() {
        User mockUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Optional<UserResponseDTO> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserResponseDTO> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUser_Success() {
        UserRequestDTO mockRequest = new UserRequestDTO();
        mockRequest.setUsername("newUser");
        mockRequest.setEmail("new@example.com");
        mockRequest.setPassword("password");

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        UserResponseDTO result = userService.createUser(mockRequest);

        assertNotNull(result);
        verify(userRepository, times(1)).existsByUsername("newUser");
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        UserRequestDTO mockRequest = new UserRequestDTO();
        mockRequest.setUsername("existingUser");
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(mockRequest));
        verify(userRepository, times(1)).existsByUsername("existingUser");
    }

    @Test
    void testCreateUserWithRole_RoleExists() {
        UserRequestDTO mockRequest = new UserRequestDTO();
        mockRequest.setUsername("newUser");
        mockRequest.setPassword("password");
        Role mockRole = new Role();

        when(roleRepository.findByName("USER_ROLE")).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        UserResponseDTO result = userService.createUserWithRole(mockRequest, "USER_ROLE");

        assertNotNull(result);
        verify(roleRepository, times(1)).findByName("USER_ROLE");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAddRoleToUser_UserAndRoleExist() {
        User mockUser = new User();
        Role mockRole = new Role();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(roleRepository.findByName("ADMIN_ROLE")).thenReturn(Optional.of(mockRole));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponseDTO result = userService.addRoleToUser(1L, "ADMIN_ROLE");

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findByName("ADMIN_ROLE");
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testDeleteUser_UserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        boolean result = userService.deleteUser(1L);

        assertFalse(result);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(1L);
    }
}
