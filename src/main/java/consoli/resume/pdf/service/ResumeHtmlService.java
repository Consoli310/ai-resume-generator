package consoli.resume.pdf.service;

import consoli.resume.ai.dto.GeneratedResumeDTO;

public interface ResumeHtmlService {

    String generateHtml(
            GeneratedResumeDTO resume
    );
}
