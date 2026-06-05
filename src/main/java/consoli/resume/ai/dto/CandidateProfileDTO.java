package consoli.resume.ai.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import consoli.resume.ai.jackson.FlexibleStringListDeserializer;

import java.util.List;

public record CandidateProfileDTO(

        String name,
        String email,
        String phone,
        String city,
        String linkedin,
        String github,
        String summary,
        List<String> skills,
        List<String> education,
        List<String> experience,
        @JsonDeserialize(using = FlexibleStringListDeserializer.class)
        List<String> projects,
        @JsonDeserialize(using = FlexibleStringListDeserializer.class)
        List<String> languages

) {
}
