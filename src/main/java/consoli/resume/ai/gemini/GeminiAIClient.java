package consoli.resume.ai.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import consoli.resume.ai.AIClient;
import consoli.resume.ai.dto.*;
import consoli.resume.dto.request.ResumeGenerateRequestDTO;
import consoli.resume.exception.AIIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GeminiAIClient
        implements AIClient {

    private static final Logger
            log =
            LoggerFactory.getLogger(
                    GeminiAIClient.class
            );

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.url}")
    private String url;

    public GeminiAIClient(
            RestClient restClient,
            ObjectMapper objectMapper
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public GeneratedResumeDTO generateResume(
            ResumeGenerateRequestDTO request
    ) {

        String prompt =
                buildPrompt(
                        request
                );

        log.info(
                "Sending request to Gemini model={}",
                model
        );

        GeminiRequestDTO body =
                new GeminiRequestDTO(
                        List.of(
                                new ContentDTO(
                                        List.of(
                                                new PartDTO(
                                                        prompt
                                                )
                                        )
                                )
                        )
                );

        GeminiResponseDTO response =
                restClient.post()
                        .uri(
                                "%s/v1beta/models/%s:generateContent"
                                        .formatted(
                                                url,
                                                model
                                        )
                        )
                        .header(
                                "x-goog-api-key",
                                apiKey
                        )
                        .body(
                                body
                        )
                        .retrieve()
                        .body(
                                GeminiResponseDTO.class
                        );

        if (

                response == null
                        || response.candidates() == null
                        || response.candidates().isEmpty()

        ) {

            throw new AIIntegrationException(
                    "Empty response from Gemini",
                    null
            );
        }

        String rawJson =
                response.candidates()
                        .get(0)
                        .content()
                        .parts()
                        .get(0)
                        .text();

        try {

            GeneratedResumeDTO dto =
                    objectMapper.readValue(
                            rawJson,
                            GeneratedResumeDTO.class
                    );

            log.info(
                    "Gemini response parsed successfully"
            );

            return dto;

        } catch (Exception e) {

            log.error(
                    "Failed to parse Gemini response",
                    e
            );

            throw new AIIntegrationException(
                    "Failed to parse Gemini response",
                    e
            );
        }
    }

    private String buildPrompt(
            ResumeGenerateRequestDTO request
    ) {

        return """
            You are a senior recruiter, ATS specialist, and expert resume writer.

            Your job is to transform the candidate information into a strong, professional, job-targeted resume output.

            CRITICAL RULES:
            1. Use ONLY the information provided in the input.
            2. NEVER invent employers, degrees, dates, titles, certifications, or responsibilities that were not provided.
            3. You may rewrite and improve wording, but must keep facts accurate.
            4. Tailor the content to the target job description as much as possible.
            5. Make the writing sound professional, modern, concise, and ATS-friendly.
            6. Prefer strong action-oriented language.
            7. Avoid repetitive wording.
            8. Do not mention that this is AI-generated.
            9. Do not add markdown, code fences, bullets outside JSON, explanations, or commentary.
            10. Return ONLY valid JSON matching the schema below.

            WRITING GUIDELINES:
            - summary: write a compelling professional summary in 3 to 5 sentences, around 50 to 80 words. It should reflect the candidate's background, strengths, target role fit, and relevant technologies or experience.
            - skills: return 6 to 12 skills, ordered by relevance to the target job. Prefer specific skills already present in the candidate data or clearly implied by it.
            - experience: rewrite each experience item into a professional resume-style statement. If there is only one experience item, expand it into a polished and impactful description without inventing facts.
            - education: keep education concise and professional. If the education items are sparse, rewrite them cleanly but do not fabricate details.

            JSON schema:
            {
              "summary": "string",
              "skills": ["string"],
              "experience": ["string"],
              "education": ["string"]
            }

            Candidate data:
            %s

            Target job data:
            %s
            """
                .formatted(
                        request.userData(),
                        request.jobApplication()
                );
    }
}