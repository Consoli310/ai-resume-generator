package consoli.resume.ai.dto;

import java.util.List;

public record GeminiResponseDTO(

        List<CandidateDTO> candidates

) {
}
