package consoli.resume.service.impl;

import consoli.resume.ai.AIClient;
import consoli.resume.ai.dto.GeneratedResumeDTO;
import consoli.resume.dto.request.ResumeGenerateRequestDTO;
import consoli.resume.dto.response.ResumeDetailsResponseDTO;
import consoli.resume.dto.response.ResumeHistoryResponseDTO;
import consoli.resume.dto.response.ResumeResponseDTO;
import consoli.resume.entity.ResumeEntity;
import consoli.resume.entity.UserEntity;
import consoli.resume.exception.ResourceNotFoundException;
import consoli.resume.pdf.service.ResumeHtmlService;
import consoli.resume.pdf.service.ResumePdfService;
import consoli.resume.repository.ResumeRepository;
import consoli.resume.repository.UserRepository;
import consoli.resume.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResumeServiceImpl
        implements ResumeService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final AIClient aiClient;
    private final ResumeHtmlService resumeHtmlService;
    private final ResumePdfService resumePdfService;

    private static final Logger
            log =
            LoggerFactory.getLogger(
                    ResumeServiceImpl.class
            );

    public ResumeServiceImpl(
            AIClient aiClient,
            ResumeHtmlService resumeHtmlService,
            ResumePdfService resumePdfService,
            ResumeRepository resumeRepository,
            UserRepository userRepository
    ) {
        this.aiClient = aiClient;
        this.resumeHtmlService = resumeHtmlService;
        this.resumePdfService = resumePdfService;
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    private UserEntity
    getAuthenticatedUser() {

        Authentication auth =

                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                auth.getName();

        return userRepository
                .findByEmail(
                        email
                )
                .orElseThrow(

                        () ->

                                new RuntimeException(
                                        "User not found"
                                )
                );
    }

    @Override
    public ResumeResponseDTO generateResume(
            ResumeGenerateRequestDTO request
    ) {

        ResumeBuildResult result =
                buildResumeData(
                        request
                );

        return new ResumeResponseDTO(
                result.generatedResume(),
                result.html(),
                result.entity()
                        .getCreatedAt()
        );
    }

    @Override
    public byte[] downloadResume(
            ResumeGenerateRequestDTO request
    ) {

        ResumeBuildResult result =
                buildResumeData(
                        request
                );

        return resumePdfService
                .generatePdf(
                        result.html()
                );
    }

    @Override
    public Page<ResumeHistoryResponseDTO>
    getHistory(
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                "createdAt"
                        ).descending()
                );

        UserEntity user =
                getAuthenticatedUser();

        return resumeRepository
                .findByUser(

                        user,
                        pageable

                )
                .map(resume ->

                        new ResumeHistoryResponseDTO(
                                resume.getId(),
                                resume.getJobTitle(),
                                resume.getCompany(),
                                resume.getCreatedAt()
                        )

                );
    }

    @Override
    public ResumeDetailsResponseDTO
    getResumeById(
            Long id
    ) {

        ResumeEntity resume =
                resumeRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Resume not found"
                                        )
                        );

        return new ResumeDetailsResponseDTO(
                resume.getId(),
                resume.getGeneratedResumeJson(),
                resume.getHtmlPreview(),
                resume.getJobTitle(),
                resume.getCompany(),
                resume.getCreatedAt()
        );
    }

    private ResumeBuildResult
    buildResumeData(
            ResumeGenerateRequestDTO request
    ) {

        GeneratedResumeDTO generatedResume =
                aiClient.generateResume(
                        request
                );

        String html =
                resumeHtmlService.generateHtml(
                        generatedResume
                );

        UserEntity user =
                getAuthenticatedUser();

        ResumeEntity entity =
                new ResumeEntity();

        entity.setUser(
                user
        );

        entity.setGeneratedResumeJson(
                generatedResume.toString()
        );

        entity.setHtmlPreview(
                html
        );

        entity.setJobTitle(
                request.jobApplication()
                        .jobTitle()
        );

        entity.setCompany(
                request.jobApplication()
                        .company()
        );

        entity.setCreatedAt(
                LocalDateTime.now()
        );

        ResumeEntity saved =
                resumeRepository.save(
                        entity
                );

        log.info(
                "Resume generated and saved. id={}",
                saved.getId()
        );

        return new ResumeBuildResult(
                generatedResume,
                html,
                saved
        );
    }

    private record ResumeBuildResult(

            GeneratedResumeDTO generatedResume,
            String html,
            ResumeEntity entity

    ) {
    }
}