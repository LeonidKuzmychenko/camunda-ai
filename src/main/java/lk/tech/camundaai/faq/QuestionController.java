package lk.tech.camundaai.faq;

import io.camunda.client.CamundaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Запускает процесс faq-question с вопросом пользователя. */
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    static final String PROCESS_ID = "faq-question";

    private final CamundaClient camundaClient;

    @PostMapping
    public ResponseEntity<?> ask(@RequestBody QuestionRequest request) {
        if (request.question() == null || request.question().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "question is required"));
        }
        var instance = camundaClient.newCreateInstanceCommand()
                .bpmnProcessId(PROCESS_ID)
                .latestVersion()
                .variables(Map.of("question", request.question()))
                .send()
                .join();
        return ResponseEntity.ok(Map.of("processInstanceKey", String.valueOf(instance.getProcessInstanceKey())));
    }

    public record QuestionRequest(String question) {
    }
}
