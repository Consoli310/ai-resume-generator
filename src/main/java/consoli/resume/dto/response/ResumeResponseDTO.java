package consoli.resume.dto.response;

import consoli.resume.ai.dto.GeneratedResumeDTO;

import java.time.LocalDateTime;

public record ResumeResponseDTO(

        GeneratedResumeDTO generatedResume,
        String htmlPreview,
        LocalDateTime createdAt

) {
}