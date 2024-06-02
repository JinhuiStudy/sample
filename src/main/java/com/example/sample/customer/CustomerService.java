package com.example.sample.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Page<CustomerDTO> getCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(CustomerEntity::toDTO);
    }

    public Optional<CustomerEntity> getCustomer(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public CustomerEntity update(CustomerUpdateRequest request, CustomerEntity entity) {
        entity.setTel(request.tel());
        entity.setName(request.name());
        return customerRepository.save(entity);
    }


    @Transactional
    public CustomerEntity save(CustomerInsertRequest request) {
        return customerRepository.save(new CustomerEntity(
            null,
            request.name(),
            request.tel()
        ));
    }

    @Transactional
    public void deleteCustomer(CustomerEntity customer) {
        customerRepository.delete(customer);
    }

}
