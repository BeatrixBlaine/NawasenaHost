package com.nawasenahost.customerservice.repository;

import com.nawasenahost.customerservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByHotelIdOrderByCustomerIdDesc(int hotelId);

    @Query("select c from Customer c where c.hotelId = :hotelId and (" +
            "lower(c.firstName) like lower(concat('%', :q, '%')) or " +
            "lower(c.lastName) like lower(concat('%', :q, '%')) or " +
            "lower(c.email) like lower(concat('%', :q, '%')) or " +
            "c.phone like concat('%', :q, '%')) order by c.customerId desc")
    List<Customer> search(@Param("hotelId") int hotelId, @Param("q") String q);

}
