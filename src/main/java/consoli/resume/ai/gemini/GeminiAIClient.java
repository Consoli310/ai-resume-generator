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
                You are a senior recruiter and ATS resume specialist.

                STRICT RULES:

                1. Use ONLY provided information
                2. NEVER invent experiences
                3. NEVER create dates, cities or companies
                4. If information is missing, omit it
                5. Create ATS-friendly professional content
                6. Return ONLY valid JSON
                7. No markdown
                8. No explanations
                9. No code block formatting

                JSON schema:

                {
                  "summary": "string",
                  "skills": [
                    "string"
                  ],
                  "experience": [
                    "string"
                  ],
                  "education": [
                    "string"
                  ]
                }

                Candidate:

                %s

                Job:

                %s
                """
                .formatted(
                        request.userData(),
                        request.jobApplication()
                );
    }
}