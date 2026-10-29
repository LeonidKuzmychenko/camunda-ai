package lk.tech.camundaai;

import io.camunda.client.CamundaClient;
import lk.tech.camundaai.faq.FaqLoaderWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

// Без запущенной Camunda: клиент отключён и заменён моком
@SpringBootTest(properties = "camunda.client.enabled=false")
class CamundaAiApplicationTests {

    @MockitoBean
    CamundaClient camundaClient;

    @Autowired
    FaqLoaderWorker faqLoaderWorker;

    @Test
    void contextLoads() {
    }

    @Test
    void loadsFaqFromClasspath() throws Exception {
        assertThat((String) faqLoaderWorker.loadFaq().get("faq")).contains("Доставка");
    }

}
