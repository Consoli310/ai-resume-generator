package consoli.resume.ai;

import consoli.resume.ai.dto.CandidateProfileDTO;
import consoli.resume.ai.dto.GeneratedResumeDTO;

public interface AIClient {

    CandidateProfileDTO parseCandidateProfile(
            String resumeText
    );

    GeneratedResumeDTO generateTailoredResume(
            CandidateProfileDTO profile,
            String jobDescription
    );
}
