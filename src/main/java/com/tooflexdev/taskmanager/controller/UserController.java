package com.tooflexdev.taskmanager.controller;

import com.tooflexdev.taskmanager.dto.UserRequestDTO;
import com.tooflexdev.taskmanager.dto.UserResponseDTO;
import com.tooflexdev.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for managing users and their roles")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

@GetMapping
@Operation(
    summary = "Get all users",
    description = "Retrieve a paginated list of all users in the system",
    parameters = {
        @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
        @Parameter(name = "size", description = "Number of items per page", example = "10"),
        @Parameter(
            name = "sort",
            description = "Sort field and direction (e.g., 'username,asc')",
            example = "username,asc",
            schema = @Schema(type = "string", allowableValues = {
                "id,asc", "id,desc",
                "username,asc", "username,desc",
                "email,asc", "email,desc",
                "createdAt,asc", "createdAt,desc"
            })
        )
    },
    responses = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    }
)
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieve user details by their unique ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "The unique ID of the user", example = "1")
            @PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @Operation(
            summary = "Get user by username",
            description = "Retrieve user details by their username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<UserDetails> getUserByUsername(
            @Parameter(description = "The username of the user", example = "johndoe")
            @PathVariable String username) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(username);
            return ResponseEntity.ok(userDetails);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    @Operation(
            summary = "Get user by email",
            description = "Retrieve user details by their email address",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @Parameter(description = "The email address of the user", example = "john.doe@example.com")
            @PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Create a new user with basic details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    public ResponseEntity<UserResponseDTO> createUser(
            @Parameter(description = "User details", required = true)
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            UserResponseDTO createdUser = userService.createUser(userRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/role/{roleName}")
    @Operation(
            summary = "Create a new user with a role",
            description = "Create a new user and assign them a role",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data or role")
            }
    )
    public ResponseEntity<UserResponseDTO> createUserWithRole(
            @Parameter(description = "The role to assign to the user", example = "USER")
            @PathVariable String roleName,
            @Parameter(description = "User details", required = true)
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            UserResponseDTO createdUser = userService.createUserWithRole(userRequestDTO, roleName);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/roles/{roleName}")
    @Operation(
            summary = "Assign a role to a user",
            description = "Add a specific role to an existing user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid role or user ID"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<UserResponseDTO> addRoleToUser(
            @Parameter(description = "The unique ID of the user", example = "1")
            @PathVariable Long id,
            @Parameter(description = "The role to assign to the user", example = "ADMIN")
            @PathVariable String roleName) {
        try {
            UserResponseDTO updatedUser = userService.addRoleToUser(id, roleName);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/roles")
    @Operation(
            summary = "Get roles of a user",
            description = "Retrieve all roles assigned to a user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<Set<String>> getUserRoles(
            @Parameter(description = "The unique ID of the user", example = "1")
            @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user.getRoles()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing user",
            description = "Update details of an existing user by their unique ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "The unique ID of the user to update", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated user details", required = true)
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.updateUser(id, userRequestDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user",
            description = "Remove a user from the system by their unique ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "The unique ID of the user to delete", example = "1")
            @PathVariable Long id) {
        return userService.deleteUser(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}