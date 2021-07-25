package reactive.data.r2dbc.basics;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import reactive.data.r2dbc.Customer;
import reactive.data.r2dbc.SimpleCustomerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Repository
@Log4j2
@RequiredArgsConstructor
public class CustomerRepository implements SimpleCustomerRepository {
    private final ConnectionManager connectionManager;
    private final BiFunction<Row, RowMetadata, Customer> mapper = (row, rowMetadata) -> new Customer(row.get("id", Integer.class), row.get("email", String.class));

    @Override
    public Mono<Customer> save(Customer c) {
        return connectionManager
                .inConnection(
                        conn -> Flux
                                .from(conn
                                        .createStatement("INSERT INTO customer(email) VALUES($1)")
                                        .bind("$1", c.getEmail())
                                        .returnGeneratedValues("id").execute())
                                .flatMap(r -> r.map((row, rowMetadata) -> {
                                    var id = row.get("id", Integer.class);
                                    return new Customer(id, c.getEmail());
                                })))
                .single()
                .log();
    }

    @Override
    public Flux<Customer> findAll() {
        return connectionManager.inConnection(conn -> Flux
                .from(conn.createStatement("select * from customer").execute())
                .flatMap(result -> result.map(mapper)));
    }

    @Override
    public Mono<Customer> update(Customer c) {
        return connectionManager.inConnection(conn -> Flux
                .from(conn.createStatement("update customer set email = $1 where id = $2")
                        .bind("$1", c.getEmail())
                        .bind("$2", c.getId())
                        .execute()))
                .then(findById(c.getId()));
    }

    @Override
    public Mono<Customer> findById(Integer id) {
        return connectionManager.inConnection(conn -> Flux
                .from(conn.createStatement("select * from customer where id = $1")
                        .bind("$1", id)
                        .execute()))
                .flatMap(result -> result.map(this.mapper))
                .single()
                .log();
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return connectionManager.inConnection(conn -> Flux
                .from(conn.createStatement("delete from customer where id = $1")
                        .bind("$1", id)
                        .execute()))
                .then();
    }
}
