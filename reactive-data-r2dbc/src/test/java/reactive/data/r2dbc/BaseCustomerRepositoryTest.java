package reactive.data.r2dbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.InputStreamReader;

public abstract class BaseCustomerRepositoryTest {
    @Autowired
    private CustomerDatabaseInitializer initializer;
    @Value("classpath:/schema.sql")
    private Resource resource;
    private String sql;

    public abstract SimpleCustomerRepository getRepository();

    @BeforeEach
    public void setupResource() throws Exception {
        Assertions.assertTrue(this.resource.exists());
        try (var in = new InputStreamReader(this.resource.getInputStream())) {
            this.sql = FileCopyUtils.copyToString(in);
        }
    }

    @Test
    public void delete() {
        var repository = this.getRepository();
        var data = repository.findAll().flatMap(c -> repository.deleteById(c.getId()))
                .thenMany(Flux.just(
                        new Customer(null, "first@email.com"),
                        new Customer(null, "second@email.com"),
                        new Customer(null, "third@email.com")))
                .flatMap(repository::save);
        StepVerifier.create(data)
                .expectNextCount(3)
                .verifyComplete();
        StepVerifier.create(repository.findAll().take(1)
                .flatMap(customer -> repository.deleteById(customer.getId())))
                .verifyComplete();
        StepVerifier.create(repository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }
}
