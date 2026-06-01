package consoli.resume.controller;

import consoli.resume.dto.auth.AuthResponseDTO;
import consoli.resume.dto.auth.RegisterRequestDTO;
import consoli.resume.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import consoli.resume.dto.auth.LoginRequestDTO;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO>
    register(

            @Valid
            @RequestBody
            RegisterRequestDTO request

    ) {

        return ResponseEntity.ok(

                authService.register(
                        request
                )

        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO>
    login(

            @Valid
            @RequestBody
            LoginRequestDTO request

    ) {

        return ResponseEntity.ok(

                authService.login(
                        request
                )

        );
    }
}