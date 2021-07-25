package reactive.data.r2dbc.basics;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactive.data.r2dbc.BaseCustomerRepositoryTest;
import reactive.data.r2dbc.SimpleCustomerRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CustomerRepositoryTest extends BaseCustomerRepositoryTest {
    @Autowired
    private SimpleCustomerRepository customerRepository;

    @Override
    public SimpleCustomerRepository getRepository() {
        return this.customerRepository;
    }
}
