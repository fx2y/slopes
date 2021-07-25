package reactive.data.r2dbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerService {
    private final SimpleCustomerRepository repository;
    private final TransactionalOperator operator;
    private final CustomerDatabaseInitializer initializer;

    public Publisher<Void> resetDatabase() {
        return this.initializer.resetCustomerTable();
    }

    public Flux<Customer> upsert(String email) {
        var customers = this.repository.findAll()
                .filter(customer -> customer.getEmail().equalsIgnoreCase(email))
                .flatMap(match -> this.repository.update(new Customer(match.getId(), email)))
                .switchIfEmpty(this.repository.save(new Customer(null, email)));
        var validatedResults = errorIfEmailsAreInvalid(customers);
        return this.operator.transactional(validatedResults);
    }

    private Flux<Customer> errorIfEmailsAreInvalid(Flux<Customer> input) {
        return input.filter(c -> c.getEmail().contains("@"))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("the email needs to be of the form a@b.com!")));
    }
}
