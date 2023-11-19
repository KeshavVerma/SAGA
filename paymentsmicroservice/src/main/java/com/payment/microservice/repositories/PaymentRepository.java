package com.payment.microservice.repositories;

import java.util.List;

import com.payment.microservice.entities.Payment;

import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    public List<Payment> findByOrderId(long orderId);
}
