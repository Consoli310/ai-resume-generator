package consoli.resume.ai.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import consoli.resume.ai.AIClient;
import consoli.resume.ai.dto.*;
import consoli.resume.exception.AIIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpStatusCodeException;

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

    @Value("${gemini.fallback-model:gemini-2.5-flash-lite}")
    private String fallbackModel;

    private static final String API_VERSION = "v1beta";

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
    public GeneratedResumeDTO generateTailoredResume(
            CandidateProfileDTO profile,
            String jobDescription
    ) {

        String prompt =
                buildTailoredResumePrompt(
                        profile,
                        jobDescription
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
                callGeminiWithFallback(
                        body,
                        "resume generation"
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

        rawJson = cleanJsonResponse(rawJson);

        log.info(
                "Gemini raw response={}",
                rawJson
        );

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

    private String buildTailoredResumePrompt(
            CandidateProfileDTO profile,
            String jobDescription
    ) {

        return """
                You are a senior recruiter, ATS specialist and expert resume strategist.
                
                  Your job is to create a highly professional, ATS-friendly and strongly job-targeted resume.
                
                  Your mission is NOT to merely rewrite the resume.
                
                  Your mission is to maximize alignment between the candidate profile and the target job description while preserving factual career history.
                
                  CRITICAL RULES:
                
                  1. NEVER invent employers, companies, degrees, certifications, dates or work history.
                  2. NEVER fabricate professional experience, years of experience or projects that do not exist.
                  3. Preserve factual accuracy regarding the candidate's career and background.
                  4. You may improve wording, positioning, organization and recruiter appeal.
                  5. Tailor the resume aggressively to the target job description.
                  6. Prioritize ATS compatibility and recruiter relevance.
                  7. Mirror relevant terminology, keywords and language from the target job description whenever possible.
                  8. Use concise, professional and action-oriented language.
                  9. Include candidate contact information when available.
                  10. Include projects whenever available and relevant.
                  11. Generate a professional targetTitle aligned with the target role.
                  12. No markdown.
                  13. No explanations.
                  14. No code fences.
                  15. Return ONLY valid JSON.
                  16. NEVER use markdown formatting.
                  17. NEVER use **bold**, *, _, #, bullet markdown or markdown emphasis.
                  18. Output plain text only inside JSON values.
                
                  IMPORTANT STRATEGIC RULE:
                
                  You are encouraged to optimize and strengthen the candidate profile.
                
                  The resume should preserve the candidate's real history while strategically positioning them as a stronger fit for the target role.
                
                  SKILLS OPTIMIZATION RULES:
                
                  skills:
                
                  * This section is strategically important.
                  * Do NOT merely copy skills from the candidate profile.
                  * Include ALL skills relevant to the target job description.
                  * Skills MAY be added even if they do not explicitly appear in the candidate profile.
                  * Prioritize the target job description over original resume wording.
                  * Include adjacent, transferable and recruiter-relevant terminology.
                  * Include ATS keywords and engineering terminology relevant to the vacancy.
                  * Include technologies, methodologies and professional terminology commonly associated with the target role.
                  * Prefer a competitive and strategically aligned skill profile over a conservative one.
                  * Generate a recruiter-friendly and ATS-optimized skills section.
                  * Keep skills relevant and avoid random unrelated technologies.
                
                  WRITING GUIDELINES:
                
                  summary:
                
                  * 3 to 5 sentences
                  * 60 to 100 words
                  * highly professional and ATS-friendly
                  * highlight strengths and role alignment
                  * emphasize candidate positioning for the target role
                  * mirror relevant language from the job description
                  * sound recruiter-oriented and competitive
                
                  experience:
                
                  * rewrite into strong professional resume statements
                  * improve wording without inventing facts
                  * use action-oriented language
                  * highlight transferable skills when supported
                  * connect experiences to communication, teamwork, ownership and problem solving when relevant
                
                  education:
                
                  * rewrite professionally
                  * preserve factual accuracy
                
                  projects:
                
                  * always include when available
                  * preserve project identity and project name
                  * summarize professionally
                  * highlight technical relevance
                  * highlight technologies, architecture and technical impact
                  * emphasize relevance to the target job
                
                  TARGETING GOAL:
                
                  The final resume should:
                
                  * sound highly relevant to the target position
                  * maximize ATS matching
                  * increase recruiter appeal
                  * strategically align with the vacancy
                  * preserve authenticity while maximizing competitiveness
                
                  JSON schema:
                
                  {
                  "name":"string",
                  "email":"string",
                  "phone":"string",
                  "city":"string",
                  "linkedin":"string",
                  "github":"string",
                  "targetTitle":"string",
                  "summary":"string",
                  "skills":["string"],
                  "experience":["string"],
                  "education":["string"],
                  "projects":["string"]
                  }
                
                  Candidate structured profile:
                  %s
                
                  Target job:
                  %s
                
            """
                .formatted(
                        profile,
                        jobDescription
                );
    }

    private String buildCandidateParserPrompt(
            String resumeText
    ) {

        log.info(
                "Resume parser input length={}",
                resumeText.length()
        );

        return """
            You are an expert resume parser.

            Your task is to extract candidate information from raw resume text and convert it into structured JSON.

            STRICT RULES:
            1. Use ONLY information explicitly present.
            2. NEVER invent or infer missing data.
            3. Missing text fields must be null.
            4. Missing lists must be [].
            5. Return ONLY valid JSON.
            6. No markdown.
            7. No explanations.
            8. No code fences.

            EXTRACTION RULES:

            name:
            - candidate full name

            email:
            - detect email addresses

            phone:
            - detect phone numbers

            city:
            - detect city or location

            linkedin:
            - detect LinkedIn URL or profile

            github:
            - detect GitHub URL or username

            summary:
            - only if explicitly written

            skills:
            - technologies
            - frameworks
            - tools
            - technical skills

            education:
            - courses
            - colleges
            - training
            - certifications

            experience:
            - work experiences
            - professional background
            - previous jobs

            projects:
            - Extract ALL software, backend, academic, freelance or portfolio projects.
            - MUST be an array of plain strings only.
            - NEVER use JSON objects inside projects.
            - Each string must combine: project name, technologies and short description.
            - Example entry:
              "Event Manager API (Java, Spring Boot, PostgreSQL) — REST API for event management with authentication and tests."
            - Never leave projects empty if any software project exists in the resume.

            JSON schema:

            {
              "name":"string",
              "email":"string",
              "phone":"string",
              "city":"string",
              "linkedin":"string",
              "github":"string",
              "summary":"string",
              "skills":["string"],
              "education":["string"],
              "experience":["string"],
              "projects":["string only, no nested objects"]
            }

            Resume text:

            %s
            """
                .formatted(
                        resumeText
                );
    }


    @Override
    public CandidateProfileDTO parseCandidateProfile(
            String resumeText
    ) {

        String prompt =
                buildCandidateParserPrompt(
                        resumeText
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
                callGeminiWithFallback(
                        body,
                        "resume parser"
                );

        if (

                response == null
                        ||
                        response.candidates() == null
                        ||
                        response.candidates().isEmpty()

        ) {

            throw new AIIntegrationException(
                    "Empty Gemini parser response",
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

        rawJson = cleanJsonResponse(rawJson);

        log.info(
                "Candidate parser raw response={}",
                rawJson
        );

        try {
            log.info(
                    "RAW GEMINI PARSER RESPONSE:\n{}",
                    rawJson
            );
            return objectMapper.readValue(
                    rawJson,
                    CandidateProfileDTO.class
            );

        } catch (Exception e) {

            log.error(
                    "Failed to parse candidate profile",
                    e
            );

            throw new AIIntegrationException(
                    "Failed to parse candidate profile",
                    e
            );
        }
    }

    private GeminiResponseDTO callGeminiWithFallback(
            GeminiRequestDTO body,
            String operation
    ) {

        try {
            return postToGemini(
                    model,
                    body
            );
        } catch (HttpStatusCodeException e) {
            if (
                    e.getStatusCode().value() == 429
                            && !model.equals(
                            fallbackModel
                    )
            ) {
                log.warn(
                        "Gemini model {} rate limited during {}. Retrying with fallback model {}",
                        model,
                        operation,
                        fallbackModel
                );
                try {
                    return postToGemini(
                            fallbackModel,
                            body
                    );
                } catch (HttpStatusCodeException fallbackError) {
                    throw toGeminiIntegrationException(
                            fallbackError
                    );
                }
            }
            throw toGeminiIntegrationException(
                    e
            );
        }
    }

    private AIIntegrationException toGeminiIntegrationException(
            HttpStatusCodeException e
    ) {

        int status = e.getStatusCode().value();
        String detail = e.getResponseBodyAsString();

        if (status == 429) {
            return new AIIntegrationException(
                    "Gemini API rate limit exceeded on primary and fallback models. Wait a moment and try again.",
                    e
            );
        }

        return new AIIntegrationException(
                "Gemini API error (%d): %s".formatted(
                        status,
                        detail
                ),
                e
        );
    }

    private GeminiResponseDTO postToGemini(
            String modelToUse,
            GeminiRequestDTO body
    ) {

        return restClient.post()
                .uri(
                        "%s/%s/models/%s:generateContent"
                                .formatted(
                                        url,
                                        API_VERSION,
                                        modelToUse
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
    }

    private String cleanJsonResponse(String rawJson) {
        if (rawJson == null) {
            return "";
        }
        int firstBrace = rawJson.indexOf('{');
        int lastBrace = rawJson.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return rawJson.substring(firstBrace, lastBrace + 1);
        }
        return rawJson
                .replace("```json", "")
                .replace("```JSON", "")
                .replace("```", "")
                .trim();
    }
}
