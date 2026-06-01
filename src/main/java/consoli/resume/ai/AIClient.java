package consoli.resume.ai;

import consoli.resume.ai.dto.GeneratedResumeDTO;
import consoli.resume.dto.request.ResumeGenerateRequestDTO;

public interface AIClient {

    GeneratedResumeDTO generateResume(
            ResumeGenerateRequestDTO request
    );
}
