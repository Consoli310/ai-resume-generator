package consoli.resume.pdf.service;

import java.util.List;

final class ResumeTextSanitizer {

    private ResumeTextSanitizer() {
    }

    static String sanitize(
            String value
    ) {

        if (value == null) {
            return null;
        }

        return value
                .replace(
                        "```json",
                        ""
                )
                .replace(
                        "```JSON",
                        ""
                )
                .replace(
                        "```",
                        ""
                )
                .replaceAll(
                        "\\*\\*",
                        ""
                )
                .replaceAll(
                        "(?m)^#{1,6}\\s*",
                        ""
                )
                .trim();
    }

    static List<String> sanitizeList(
            List<String> values
    ) {

        if (values == null) {
            return null;
        }

        return values.stream()
                .map(
                        ResumeTextSanitizer::sanitize
                )
                .filter(
                        value ->
                                value != null
                                        && !value.isBlank()
                )
                .toList();
    }
}
