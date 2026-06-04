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
                "name",
                ResumeTextSanitizer.sanitize(
                        resume.name()
                )
        );

        context.setVariable(
                "email",
                ResumeTextSanitizer.sanitize(
                        resume.email()
                )
        );

        context.setVariable(
                "phone",
                ResumeTextSanitizer.sanitize(
                        resume.phone()
                )
        );

        context.setVariable(
                "city",
                ResumeTextSanitizer.sanitize(
                        resume.city()
                )
        );

        context.setVariable(
                "linkedin",
                ResumeTextSanitizer.sanitize(
                        resume.linkedin()
                )
        );

        context.setVariable(
                "github",
                ResumeTextSanitizer.sanitize(
                        resume.github()
                )
        );

        context.setVariable(
                "targetTitle",
                ResumeTextSanitizer.sanitize(
                        resume.targetTitle()
                )
        );

        context.setVariable(
                "summary",
                ResumeTextSanitizer.sanitize(
                        resume.summary()
                )
        );

        context.setVariable(
                "skills",
                ResumeTextSanitizer.sanitizeList(
                        resume.skills()
                )
        );

        context.setVariable(
                "experience",
                ResumeTextSanitizer.sanitizeList(
                        resume.experience()
                )
        );

        context.setVariable(
                "education",
                ResumeTextSanitizer.sanitizeList(
                        resume.education()
                )
        );

        context.setVariable(
                "projects",
                ResumeTextSanitizer.sanitizeList(
                        resume.projects()
                )
        );

        return templateEngine.process(
                "resume-template",
                context
        );
    }
}