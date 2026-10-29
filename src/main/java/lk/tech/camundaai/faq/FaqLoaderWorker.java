package lk.tech.camundaai.faq;

import io.camunda.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Читает файл FAQ и передаёт его текст в процесс (переменная {@code faq}).
 * Дальше этот текст попадает в системный промпт AI Agent connector.
 */
@Slf4j
@Component
public class FaqLoaderWorker {

    private final Resource faqResource;

    public FaqLoaderWorker(@Value("${faq.location}") Resource faqResource) {
        this.faqResource = faqResource;
    }

    @JobWorker(type = "faq-load")
    public Map<String, Object> loadFaq() throws IOException {
        // Читаем при каждом вызове, чтобы правки FAQ подхватывались без перезапуска
        String faq = faqResource.getContentAsString(StandardCharsets.UTF_8);
        log.info("Loaded FAQ from {} ({} chars)", faqResource.getDescription(), faq.length());
        return Map.of("faq", faq);
    }
}
