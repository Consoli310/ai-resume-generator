package consoli.resume.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResumeSimpleRequestDTO(

        @NotBlank
        String jobDescription,

        @NotBlank
        String currentResumeText

) {
}