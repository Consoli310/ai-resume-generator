package consoli.resume.service.impl;

import consoli.resume.ai.AIClient;
import consoli.resume.ai.dto.CandidateProfileDTO;
import consoli.resume.ai.dto.GeneratedResumeDTO;
import consoli.resume.dto.request.ResumeSimpleRequestDTO;
import consoli.resume.pdf.service.ResumeHtmlService;
import consoli.resume.pdf.service.ResumePdfService;
import consoli.resume.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResumeServiceImpl
        implements ResumeService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ResumeServiceImpl.class
            );

    private final AIClient aiClient;
    private final ResumeHtmlService resumeHtmlService;
    private final ResumePdfService resumePdfService;

    public ResumeServiceImpl(
            AIClient aiClient,
            ResumeHtmlService resumeHtmlService,
            ResumePdfService resumePdfService
    ) {
        this.aiClient = aiClient;
        this.resumeHtmlService = resumeHtmlService;
        this.resumePdfService = resumePdfService;
    }

    @Override
    public byte[] downloadSimpleResume(
            ResumeSimpleRequestDTO request
    ) {

        CandidateProfileDTO profile =
                aiClient.parseCandidateProfile(
                        request.currentResumeText()
                );

        log.info(
                "Parsed candidate profile for email={}",
                profile.email()
        );

        GeneratedResumeDTO generatedResume =
                aiClient.generateTailoredResume(
                        profile,
                        request.jobDescription()
                );

        String html =
                resumeHtmlService.generateHtml(
                        generatedResume
                );

        return resumePdfService.generatePdf(
                html
        );
    }
}
