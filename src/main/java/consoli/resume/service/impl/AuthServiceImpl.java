package consoli.resume.service.impl;

import consoli.resume.dto.auth.AuthResponseDTO;
import consoli.resume.dto.auth.RegisterRequestDTO;
import consoli.resume.entity.UserEntity;
import consoli.resume.repository.UserRepository;
import consoli.resume.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import consoli.resume.dto.auth.LoginRequestDTO;
import consoli.resume.security.JwtService;
import consoli.resume.entity.UserRole;
import consoli.resume.exception.AuthenticationException;
import consoli.resume.exception.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthServiceImpl
        implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final Logger
            log =
            LoggerFactory.getLogger(
                    AuthServiceImpl.class
            );

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponseDTO register(
            RegisterRequestDTO request
    ) {

        if (

                userRepository
                        .findByEmail(
                                request.email()
                        )
                        .isPresent()

        ) {
            log.warn(
                    "Registration failed. Email already exists={}",
                    request.email()
            );
            throw new ConflictException(
                    "Email already registered"
            );
        }

        UserEntity user =
                new UserEntity();

        user.setName(
                request.name()
        );

        user.setEmail(
                request.email()
        );

        user.setPassword(

                passwordEncoder.encode(
                        request.password()
                )

        );

        user.setRole(
                UserRole.USER
        );

        userRepository.save(
                user
        );

        log.info(
                "User registered successfully email={}",
                user.getEmail()
        );

        return new AuthResponseDTO(
                "User registered successfully"
        );
    }

    @Override
    public AuthResponseDTO login(
            LoginRequestDTO request
    ) {

        UserEntity user =

                userRepository
                        .findByEmail(
                                request.email()
                        )
                        .orElseThrow(

                                () ->

                                        new AuthenticationException(
                                                "Invalid credentials"
                                        )

                        );

        boolean matches =

                passwordEncoder.matches(

                        request.password(),
                        user.getPassword()

                );

        if (

                !matches

        ) {
            log.warn(
                    "Invalid login attempt email={}",
                    request.email()
            );
            throw new AuthenticationException(
                    "Invalid credentials"
            );
        }

        String token =

                jwtService.generateToken(
                        user.getEmail()
                );
        log.info(
                "Login successful email={}",
                user.getEmail()
        );
        return new AuthResponseDTO(
                token
        );
    }
}