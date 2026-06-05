package consoli.resume.controller;

import consoli.resume.dto.auth.AuthResponseDTO;
import consoli.resume.dto.auth.RegisterRequestDTO;
import consoli.resume.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import consoli.resume.dto.auth.LoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints para registro e login de usuários")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Registra um novo usuário no sistema e retorna uma confirmação.")
    @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados fornecidos são inválidos")
    @ApiResponse(responseCode = "499", description = "E-mail já está em uso")
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
    @Operation(summary = "Efetuar login de usuário", description = "Efetua login com e-mail e senha e retorna o token JWT.")
    @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso, token retornado")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
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
