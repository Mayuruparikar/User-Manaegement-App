package com.task.user_management.service;

import com.task.user_management.entity.User;
import com.task.user_management.exception.InvalidRoleException;
import com.task.user_management.exception.UserNotFoundException;
import com.task.user_management.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    private static final Set<String> ALLOWED_ROLES = new HashSet<>(Arrays.asList("USER", "ADMIN"));

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User register(User user) {
        if (user.getRoles().isEmpty()) {
            user.getRoles().add("USER");
        } else {
            for (String role : user.getRoles()) {
                if (!ALLOWED_ROLES.contains(role.toUpperCase())) {
                    throw new InvalidRoleException("Invalid role provided: '" + role + "'. Allowed roles are USER or ADMIN.");
                }
            }
        }

        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new UserNotFoundException("Cannot delete: User not found with ID: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().toArray(new String[0]))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}