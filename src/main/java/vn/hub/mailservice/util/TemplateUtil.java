package vn.hub.mailservice.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TemplateUtil {

    /**
     * Đọc và xử lý file template HTML
     *
     * @param templatePath đường dẫn tới file template trong classpath
     * @param variables    map chứa các biến cần thay thế
     * @return nội dung HTML đã được xử lý
     */
    public String processTemplate(String templatePath, Map<String, String> variables) {
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            byte[] fileBytes = Files.readAllBytes(resource.getFile().toPath());
            String templateContent = new String(fileBytes, StandardCharsets.UTF_8);

            return replaceVariables(templateContent, variables);
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file template: " + templatePath, e);
        }
    }

    /**
     * Thay thế các biến trong nội dung template
     *
     * @param content   nội dung template
     * @param variables map chứa các biến cần thay thế
     * @return nội dung đã thay thế các biến
     */
    private String replaceVariables(String content, Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) {
            return content;
        }

        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(content);

        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            String variableValue = variables.getOrDefault(variableName, "");
            matcher.appendReplacement(buffer, variableValue.replace("$", "\\$"));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}