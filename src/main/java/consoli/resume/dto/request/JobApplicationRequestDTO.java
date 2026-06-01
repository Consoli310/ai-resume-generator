package consoli.resume.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JobApplicationRequestDTO(

        @NotBlank(
                message =
                        "Job title is required"
        )
        String jobTitle,

        @NotBlank(
                message =
                        "Company is required"
        )
        String company,

        @NotBlank(
                message =
                        "Job description is required"
        )
        String jobDescription

) {
}