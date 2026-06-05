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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/resume")
@Tag(name = "Resume Tailoring", description = "Endpoints para processamento e download de currículos otimizados via IA")
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
    @Operation(summary = "Gerar PDF do currículo (JSON)", description = "Envia descrição de vaga e currículo atual no corpo JSON e retorna o PDF gerado pela IA.")
    @ApiResponse(responseCode = "200", description = "PDF gerado e retornado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autorizado / Token JWT inválido ou ausente")
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
    @Operation(summary = "Gerar PDF do currículo (Multipart Form-Data)", description = "Envia descrição de vaga e currículo atual como campos de formulário e retorna o PDF gerado pela IA.")
    @ApiResponse(responseCode = "200", description = "PDF gerado e retornado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autorizado / Token JWT inválido ou ausente")
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
