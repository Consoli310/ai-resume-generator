package consoli.resume.pdf.service;

public interface ResumePdfService {

    byte[] generatePdf(
            String html
    );
}