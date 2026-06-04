package consoli.resume.ai.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class FlexibleStringListDeserializer
        extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(
            JsonParser parser,
            DeserializationContext context
    ) throws IOException {

        JsonNode node =
                parser.getCodec().readTree(
                        parser
                );

        if (
                node == null
                        || node.isNull()
        ) {
            return List.of();
        }

        if (!node.isArray()) {
            return List.of(
                    toStringEntry(
                            node
                    )
            );
        }

        List<String> result =
                new ArrayList<>();

        for (JsonNode item : node) {
            result.add(
                    toStringEntry(
                            item
                    )
            );
        }

        return result;
    }

    private String toStringEntry(
            JsonNode node
    ) {

        if (
                node == null
                        || node.isNull()
        ) {
            return "";
        }

        if (node.isTextual()) {
            return node.asText();
        }

        if (node.isObject()) {
            return formatProjectObject(
                    node
            );
        }

        return node.asText();
    }

    private String formatProjectObject(
            JsonNode node
    ) {

        String name =
                firstText(
                        node,
                        "project_name",
                        "projectName",
                        "name",
                        "title"
                );

        String description =
                firstText(
                        node,
                        "description",
                        "summary",
                        "details"
                );

        String technologies =
                formatTechnologies(
                        node.get(
                                "technologies"
                        ),
                        node.get(
                                "tech"
                        ),
                        node.get(
                                "stack"
                        )
                );

        StringBuilder formatted =
                new StringBuilder();

        if (
                name != null
                        && !name.isBlank()
        ) {
            formatted.append(
                    name.trim()
            );
        }

        if (!technologies.isBlank()) {
            if (!formatted.isEmpty()) {
                formatted
                        .append(" (")
                        .append(technologies)
                        .append(")");
            } else {
                formatted.append(
                        technologies
                );
            }
        }

        if (
                description != null
                        && !description.isBlank()
        ) {
            if (!formatted.isEmpty()) {
                formatted.append(
                        " — "
                );
            }
            formatted.append(
                    description
                            .replace(
                                    '\n',
                                    ' '
                            )
                            .trim()
            );
        }

        return formatted.toString().trim();
    }

    private String formatTechnologies(
            JsonNode... nodes
    ) {

        for (JsonNode node : nodes) {
            if (
                    node == null
                            || node.isNull()
            ) {
                continue;
            }
            if (node.isArray()) {
                return StreamSupport.stream(
                                node.spliterator(),
                                false
                        )
                        .map(
                                JsonNode::asText
                        )
                        .filter(
                                value ->
                                        !value.isBlank()
                        )
                        .reduce(
                                (left, right) ->
                                        left + ", " + right
                        )
                        .orElse(
                                ""
                        );
            }
            if (node.isTextual()) {
                return node.asText();
            }
        }

        return "";
    }

    private String firstText(
            JsonNode node,
            String... fieldNames
    ) {

        for (String fieldName : fieldNames) {
            JsonNode value =
                    node.get(
                            fieldName
                    );
            if (
                    value != null
                            && value.isTextual()
                            && !value.asText().isBlank()
            ) {
                return value.asText();
            }
        }

        return null;
    }
}
