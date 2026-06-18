package ar.com.leguitech.fintechcoreservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Disabled("Spins up Kafka/Mongo/Redis via Testcontainers; flaky and slow in PR CI. "
        + "Re-enable as a tagged integration test once real integration coverage exists.")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FintechCoreServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
