package com.order.microservice.repositories;

import com.order.microservice.entities.OrderEntity;

import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderEntity,Long>{
    
}
