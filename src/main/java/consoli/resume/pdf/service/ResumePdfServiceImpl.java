package consoli.resume.pdf.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import consoli.resume.exception.AIIntegrationException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ResumePdfServiceImpl
        implements ResumePdfService {

    @Override
    public byte[] generatePdf(
            String html
    ) {

        try (

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()

        ) {

            PdfRendererBuilder builder =
                    new PdfRendererBuilder();

            builder.useFastMode();

            builder.withHtmlContent(
                    html,
                    null
            );

            builder.toStream(
                    outputStream
            );

            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new AIIntegrationException(
                    "Failed to generate PDF",
                    e
            );
        }
    }
}