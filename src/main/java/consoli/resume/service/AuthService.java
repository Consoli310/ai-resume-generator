package consoli.resume.service;

import consoli.resume.dto.auth.AuthResponseDTO;
import consoli.resume.dto.auth.LoginRequestDTO;
import consoli.resume.dto.auth.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO register(
            RegisterRequestDTO request
    );

    AuthResponseDTO login(
            LoginRequestDTO request
    );
}