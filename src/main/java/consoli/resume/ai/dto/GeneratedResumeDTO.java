package consoli.resume.ai.dto;

import java.util.List;

public record GeneratedResumeDTO(

        String summary,
        List<String> skills,
        List<String> experience,
        List<String> education

) {
}
