package com.inventory.microservice.events;

import com.inventory.microservice.dto.CustomerOrder;
import com.inventory.microservice.dto.Stock;

public class InventoryEvent {

    private String type;

    private CustomerOrder order;
    
    private Stock stock;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CustomerOrder getOrder() {
        return order;
    }

    public void setOrder(CustomerOrder order) {
        this.order = order;
    }

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

}
