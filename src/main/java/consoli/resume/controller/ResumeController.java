package consoli.resume.controller;

import consoli.resume.dto.request.ResumeSimpleRequestDTO;
import consoli.resume.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(
            ResumeService resumeService
    ) {
        this.resumeService = resumeService;
    }

    @PostMapping(
            value = "/download",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<byte[]> downloadJson(

            @Valid
            @RequestBody
            ResumeSimpleRequestDTO request

    ) {

        return downloadPdf(
                request
        );
    }

    @PostMapping(
            value = "/download",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<byte[]> downloadMultipart(

            @RequestPart
            String jobDescription,

            @RequestPart
            String currentResumeText

    ) {

        return downloadPdf(
                new ResumeSimpleRequestDTO(
                        jobDescription,
                        currentResumeText
                )
        );
    }

    private ResponseEntity<byte[]> downloadPdf(
            ResumeSimpleRequestDTO request
    ) {

        byte[] pdf =
                resumeService.downloadSimpleResume(
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
                .body(
                        pdf
                );
    }
}
