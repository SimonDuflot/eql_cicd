package com.formation;

import com.formation.service.CustomerService;
import com.formation.web.error.ConflictException;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import io.micrometer.common.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CustomerServiceIntegrationTest {

    @Autowired
    CustomerService customerService;

    @Test
    void getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        Assertions.assertEquals(5, customers.size());
    }

    @Test
    void getCustomer() {

        // Given
        // When
        Customer customer = customerService.getCustomer("054b145c-ddbc-4136-a2bd-7bf45ed1bef7");
        // Then
        Assertions.assertNotNull(customer);
        Assertions.assertEquals("Reynolds", customer.getLastName());
    }

    @Test
    void getCustomer_NotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> this.customerService.getCustomer("d972b30f-21cc-4" +
                "11f-b374-685ce23cd317"), "should throw a NotFoundException");
    }

    @Test
    void addCustomer() {
        Customer customer = new Customer("", "Simon", "Duflot", "mail@mail.test", "76544536", "123 Rue de la rue, Ville, Codepostal");

        customer = customerService.addCustomer(customer);

        Assertions.assertTrue(StringUtils.isNotBlank(customer.getCustomerId()));
        Assertions.assertEquals("Simon", customer.getFirstName());
        this.customerService.deleteCustomer(customer.getCustomerId());
    }

    @Test
    void addCustom_alreadyExists() {
        Customer customer = new Customer("", "Simon", "Duflot", "penatibus.et@lectusa.com", "76544536", "123 Rue de la rue, Ville, Codepostal");

        assertThrows(ConflictException.class, () -> customerService.addCustomer(customer), "Should throw a ConflictException");

    }


}
