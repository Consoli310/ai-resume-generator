package consoli.resume.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ResumeGenerateRequestDTO(

        @Valid
        @NotNull(
                message =
                        "User data is required"
        )
        UserDataRequestDTO userData,

        @Valid
        @NotNull(
                message =
                        "Job application is required"
        )
        JobApplicationRequestDTO jobApplication

) {
}