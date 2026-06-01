package consoli.resume.service;

import consoli.resume.dto.request.ResumeGenerateRequestDTO;
import consoli.resume.dto.response.ResumeDetailsResponseDTO;
import consoli.resume.dto.response.ResumeHistoryResponseDTO;
import consoli.resume.dto.response.ResumeResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ResumeService {

    ResumeResponseDTO generateResume(
            ResumeGenerateRequestDTO request
    );

    byte[] downloadResume(
            ResumeGenerateRequestDTO request
    );

    Page<ResumeHistoryResponseDTO>
    getHistory(
            int page,
            int size
    );

    ResumeDetailsResponseDTO
    getResumeById(
            Long id
    );
}