package com.formation;

import com.formation.data.entity.CustomerEntity;
import com.formation.data.repository.CustomerRepository;
import com.formation.service.CustomerService;
import com.formation.web.error.ConflictException;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.RequestEntity.delete;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    CustomerService customerService;

    @Mock
    CustomerRepository repository;

    @Test
    void getAllCustomers() {

        //Given
        Mockito.doReturn(getMockCustomers(2)).when(repository).findAll();

        //When
        List<Customer> customers = customerService.getAllCustomers();

        //Then
        Assertions.assertEquals(2, customers.size(), "FAUX");

    }

    private Iterable<CustomerEntity> getMockCustomers(int size) {
        List<CustomerEntity> customers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            customers.add(new CustomerEntity(UUID.randomUUID(),
                    "firstName " + i,
                    "lastName " + i,
                    "email " + i,
                    "phone " + i,
                    "address " + i));
        }
        return customers;
    }

    private CustomerEntity getMockCustomerEntity() {
        return new CustomerEntity(UUID.randomUUID(), "firstName", "lastName", "email", "phone", "address");
    }

    @Test
    void getCustomer() {

        CustomerEntity entity = getMockCustomerEntity();

        Optional<CustomerEntity> optional = Optional.of(entity);

        //Given
        Mockito.doReturn(optional).when(repository).findById(entity.getCustomerId());

        //When
        Customer customer = customerService.getCustomer(entity.getCustomerId().toString());

        //Then
        Assertions.assertNotNull(customer);
        Assertions.assertEquals("firstName", customer.getFirstName(), "FAUX BOLOSS");

    }

    @Test
    void getCustomer_notExists() {

        CustomerEntity entity = getMockCustomerEntity();
        Optional<CustomerEntity> optional = Optional.empty();

        Mockito.doReturn(optional).when(repository).findById(entity.getCustomerId());
        Assertions.assertThrows(NotFoundException.class, () -> customerService.getCustomer(entity.getCustomerId().toString()), "exception not thrown as expected");
    }

    @Test
    void findByEmailAddress() {

        CustomerEntity entity = getMockCustomerEntity();

        Mockito.doReturn(entity).when(repository).findByEmailAddress(entity.getEmailAddress());

        Customer customer = customerService.findByEmailAddress(entity.getEmailAddress());

        Assertions.assertNotNull(customer);
        Assertions.assertEquals("firstName", customer.getFirstName());
    }

    @Test
    void addCustomer() {

        // Créer un client mock
        CustomerEntity entity = getMockCustomerEntity();

        // Configurer le mock pour retourner null quand on cherche un client par email
        Mockito.when(repository.findByEmailAddress(entity.getEmailAddress())).thenReturn(null);
        // Configurer le mock pour retourner le client quand on le sauvegarde
        Mockito.when(repository.save(any(CustomerEntity.class))).thenReturn(entity);
        // Créer un client à ajouter (DTO)
        Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(), entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
        // Appeler la méthode à tester
        customer = customerService.addCustomer(customer);

        // Vérifier que la méthode a retourné un client
        Assertions.assertNotNull(customer);
        Assertions.assertEquals("email", customer.getEmailAddress(), "mauvais last name");
    }

    @Test
    void addCustomer_exists() {

        // Créer un client mock
        CustomerEntity entity = getMockCustomerEntity();

        // Configurer le mock pour sortir un client lorsqu'on cherche par email
        Mockito.when(repository.findByEmailAddress(entity.getEmailAddress())).thenReturn(entity);

        Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(), entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
        // On regarde si la bonne exception est rejetée
        Assertions.assertThrows(ConflictException.class, () -> customerService.addCustomer(customer), "mauvaise exception rejetée, doit être ConflictException");
    }

    @Test
    void updateCustomer() {
        // Créer un client mock
        CustomerEntity entity = getMockCustomerEntity();

        // Configurer le mock pour retourner un client après sauvegarde
        Mockito.when(repository.save(any(CustomerEntity.class))).thenReturn(entity);

        // Crée un client à mettre à jour (DTO)
        Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(), entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());

        //Appel la méthode à tester
        customer = customerService.updateCustomer(customer);

        // Vérifie si la méthode retourne un client
        Assertions.assertNotNull(customer);

        // Vérifie un attribut du client : ici le firstName.
        Assertions.assertEquals("firstName", customer.getFirstName());
    }

    @Test
    void updateCustomer_notExists() {
        CustomerEntity entity = getMockCustomerEntity();

        Optional<CustomerEntity> optional = Optional.empty();

        Mockito.doReturn(optional).when(repository).findById(entity.getCustomerId());

        Assertions.assertThrows(NotFoundException.class, () -> customerService.getCustomer(entity.getCustomerId().toString()), "should throw a NotFoundException");
    }

    @Test
    void deleteCustomer() {

        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(repository).deleteById(id);

        customerService.deleteCustomer(id.toString());
    }

}
