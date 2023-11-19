package com.shipment.microservice.repositories;

import com.shipment.microservice.entities.Shipment;

import org.springframework.data.repository.CrudRepository;

public interface ShipmentRepository extends CrudRepository<Shipment,Long>{

}
