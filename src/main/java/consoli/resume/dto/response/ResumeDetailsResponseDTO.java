package consoli.resume.dto.response;

import java.time.LocalDateTime;

public record ResumeDetailsResponseDTO(

        Long id,
        String generatedResumeJson,
        String htmlPreview,
        String jobTitle,
        String company,
        LocalDateTime createdAt

) {
}