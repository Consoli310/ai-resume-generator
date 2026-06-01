package consoli.resume;

import consoli.resume.dto.auth.AuthResponseDTO;
import consoli.resume.dto.auth.LoginRequestDTO;
import consoli.resume.dto.auth.RegisterRequestDTO;
import consoli.resume.entity.UserEntity;
import consoli.resume.entity.UserRole;
import consoli.resume.exception.AuthenticationException;
import consoli.resume.exception.ConflictException;
import consoli.resume.repository.UserRepository;
import consoli.resume.security.JwtService;
import consoli.resume.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(
        MockitoExtension.class
)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequestDTO request =
                new RegisterRequestDTO(
                        "Matheus",
                        "matheus@email.com",
                        "123456"
                );

        when(

                userRepository.findByEmail(
                        request.email()
                )

        ).thenReturn(
                Optional.empty()
        );

        when(

                passwordEncoder.encode(
                        request.password()
                )

        ).thenReturn(
                "encodedPassword"
        );

        AuthResponseDTO response =
                authService.register(
                        request
                );

        assertEquals(
                "User registered successfully",
                response.token()
        );

        verify(
                userRepository
        ).save(
                any()
        );
    }

    @Test
    void shouldThrowConflictWhenEmailAlreadyExists() {

        RegisterRequestDTO request =
                new RegisterRequestDTO(
                        "Matheus",
                        "matheus@email.com",
                        "123456"
                );

        when(

                userRepository.findByEmail(
                        request.email()
                )

        ).thenReturn(
                Optional.of(
                        new UserEntity()
                )
        );

        assertThrows(

                ConflictException.class,

                () ->

                        authService.register(
                                request
                        )

        );

        verify(
                userRepository,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequestDTO request =
                new LoginRequestDTO(
                        "matheus@email.com",
                        "123456"
                );

        UserEntity user =
                new UserEntity();

        user.setEmail(
                "matheus@email.com"
        );

        user.setPassword(
                "encodedPassword"
        );

        when(

                userRepository.findByEmail(
                        request.email()
                )

        ).thenReturn(
                Optional.of(
                        user
                )
        );

        when(

                passwordEncoder.matches(
                        request.password(),
                        user.getPassword()
                )

        ).thenReturn(
                true
        );

        when(

                jwtService.generateToken(
                        user.getEmail()
                )

        ).thenReturn(
                "fake-jwt-token"
        );

        AuthResponseDTO response =
                authService.login(
                        request
                );

        assertEquals(
                "fake-jwt-token",
                response.token()
        );

        verify(
                jwtService
        ).generateToken(
                user.getEmail()
        );
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenPasswordIsInvalid() {

        LoginRequestDTO request =
                new LoginRequestDTO(
                        "matheus@email.com",
                        "wrong-password"
                );

        UserEntity user =
                new UserEntity();

        user.setEmail(
                "matheus@email.com"
        );

        user.setPassword(
                "encodedPassword"
        );

        when(

                userRepository.findByEmail(
                        request.email()
                )

        ).thenReturn(
                Optional.of(
                        user
                )
        );

        when(

                passwordEncoder.matches(
                        request.password(),
                        user.getPassword()
                )

        ).thenReturn(
                false
        );

        assertThrows(

                AuthenticationException.class,

                () ->

                        authService.login(
                                request
                        )

        );

        verify(
                jwtService,
                never()
        ).generateToken(
                anyString()
        );
    }
}