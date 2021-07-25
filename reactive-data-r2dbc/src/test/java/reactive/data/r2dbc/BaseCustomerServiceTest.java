package reactive.data.r2dbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

abstract public class BaseCustomerServiceTest {
    private SimpleCustomerRepository customerRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerDatabaseInitializer initializer;

    abstract public SimpleCustomerRepository getCustomerRepository();

    @BeforeEach
    public void reset() throws Exception {
        this.customerRepository = this.getCustomerRepository();
        StepVerifier.create(this.initializer.resetCustomerTable()).verifyComplete();
    }

    @Test
    public void badUpsert() throws Exception {
        var badEmail = "bad";
        var firstWrite = this.customerService.upsert(badEmail)
                .thenMany(this.customerRepository.findAll());
        StepVerifier.create(firstWrite).expectError().verify();
        StepVerifier.create(this.customerRepository.findAll()).expectNextCount(0).verifyComplete();
    }
}
