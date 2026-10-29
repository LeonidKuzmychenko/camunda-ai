package lk.tech.camundaai;

import io.camunda.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Deployment(resources = {"classpath*:/bpmn/*.bpmn", "classpath*:/bpmn/*.form"})
public class CamundaAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamundaAiApplication.class, args);
    }

}
