package consoli.resume.controller;

import consoli.resume.dto.request.ResumeGenerateRequestDTO;
import consoli.resume.dto.response.ResumeDetailsResponseDTO;
import consoli.resume.dto.response.ResumeResponseDTO;
import consoli.resume.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import consoli.resume.dto.response.ResumeHistoryResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/resume")
@Tag(
        name = "Resume API",
        description =
                "Operations related to AI-generated resumes"
)
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(
            ResumeService resumeService
    ) {
        this.resumeService = resumeService;
    }

    @Operation(
            summary =
                    "Generate resume preview",
            description =
                    "Generates AI-based resume content and HTML preview without downloading PDF"
    )
    @PostMapping("/generate")
    public ResponseEntity<ResumeResponseDTO> generate(

            @Valid
            @RequestBody
            ResumeGenerateRequestDTO request

    ) {

        ResumeResponseDTO response =
                resumeService.generateResume(
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }

    @Operation(
            summary =
                    "Download resume PDF",
            description =
                    "Generates and downloads resume PDF using Gemini AI and HTML template"
    )
    @PostMapping("/download")
    public ResponseEntity<byte[]> download(

            @Valid
            @RequestBody
            ResumeGenerateRequestDTO request

    ) {

        byte[] pdf =
                resumeService.downloadResume(
                        request
                );

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=resume.pdf"
                )
                .header(
                        "Content-Type",
                        "application/pdf"
                )
                .body(pdf);
    }

    @Operation(
            summary =
                    "List resume history",
            description =
                    "Returns paginated history of generated resumes"
    )
    @GetMapping("/history")
    public ResponseEntity<
            Page<ResumeHistoryResponseDTO>
            > history(

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "5"
            )
            int size

    ) {

        return ResponseEntity.ok(
                resumeService.getHistory(
                        page,
                        size
                )
        );
    }

    @Operation(
            summary =
                    "Get resume details",
            description =
                    "Returns complete stored resume information by id"
    )
    @GetMapping("/{id}")
    public ResponseEntity<
            ResumeDetailsResponseDTO
            > getById(

            @PathVariable
            Long id

    ) {

        ResumeDetailsResponseDTO
                resume =
                resumeService
                        .getResumeById(
                                id
                        );

        return ResponseEntity.ok(
                resume
        );
    }
}