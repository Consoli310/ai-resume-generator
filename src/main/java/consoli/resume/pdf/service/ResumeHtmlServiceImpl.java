package consoli.resume.pdf.service;

import consoli.resume.ai.dto.GeneratedResumeDTO;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class ResumeHtmlServiceImpl
        implements ResumeHtmlService {

    private final TemplateEngine templateEngine;

    public ResumeHtmlServiceImpl(
            TemplateEngine templateEngine
    ) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String generateHtml(
            GeneratedResumeDTO resume
    ) {

        Context context = new Context();

        context.setVariable(
                "summary",
                resume.summary()
        );

        context.setVariable(
                "skills",
                resume.skills()
        );

        context.setVariable(
                "experience",
                resume.experience()
        );

        context.setVariable(
                "education",
                resume.education()
        );

        return templateEngine.process(
                "resume-template",
                context
        );
    }
}