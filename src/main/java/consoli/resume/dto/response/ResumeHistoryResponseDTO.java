package consoli.resume.dto.response;

import java.time.LocalDateTime;

public record ResumeHistoryResponseDTO(

        Long id,
        String jobTitle,
        String company,
        LocalDateTime createdAt

) {
}