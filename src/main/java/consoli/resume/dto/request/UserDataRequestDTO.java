package consoli.resume.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UserDataRequestDTO(

        @NotBlank(
                message =
                        "Name is required"
        )
        String name,

        @Email(
                message =
                        "Invalid email"
        )
        @NotBlank(
                message =
                        "Email is required"
        )
        String email,

        @NotBlank(
                message =
                        "Phone is required"
        )
        String phone,

        String city,

        String linkedin,

        String github,

        @Size(
                max = 500,
                message =
                        "Summary too long"
        )
        String summary,

        @NotEmpty(
                message =
                        "Skills cannot be empty"
        )
        List<String> skills,

        List<String> education,

        List<String> experience

) {
}