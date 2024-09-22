package com.github.blog.service;

import com.github.blog.exception.EmailAlreadyInUseException;
import com.github.blog.exception.InvalidEmail;
import com.github.blog.exception.PasswordConstraintException;
import com.github.blog.model.User;
import com.github.blog.model.auth.LoginRequest;
import com.github.blog.model.auth.RegisterUserRequest;
import com.github.blog.model.auth.Role;
import com.github.blog.repository.RoleRepository;
import com.github.blog.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private static final String DEFAULT_ROLE_NAME = "ROLE_USER";

    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*\\d)[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");


    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository, RoleRepository roleRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserRequest request)  {
        validateRegisterUser(request);
        User user = new User()
                .setName(request.getFullName())
                .setEmail(request.getEmail())
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setRegistrationDate(LocalDateTime.now());

        var defaultRole = getUserRole();
        user.setRoles(Set.of(defaultRole));

        return userRepository.save(user);
    }

    public User authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            throw new UsernameNotFoundException("Couldn't find user with email" + request.getEmail());
        }

        return userRepository.findByEmail(request.getEmail())
                .orElseThrow();
    }

    private Role getUserRole() {
        var optionalRole = roleRepository.findByName(DEFAULT_ROLE_NAME);
        return optionalRole.orElseGet(this::createDefaultRole);
    }

    private Role createDefaultRole() {
        return new Role().setName(DEFAULT_ROLE_NAME);
    }

    private void validateRegisterUser(RegisterUserRequest request) {
        var email = request.getEmail();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmail("Invalid email format");
        }

        var optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new EmailAlreadyInUseException("Email address already in use");
        }

        var password = request.getPassword();
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new PasswordConstraintException("Password's length should be at least 8 symbols");

        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new PasswordConstraintException("Password should contains at least one digit and one capital letter");
        }

    }
}
