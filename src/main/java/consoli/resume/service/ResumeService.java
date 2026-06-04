package consoli.resume.service;

import consoli.resume.dto.request.ResumeSimpleRequestDTO;

public interface ResumeService {

    byte[] downloadSimpleResume(
            ResumeSimpleRequestDTO request
    );
}
