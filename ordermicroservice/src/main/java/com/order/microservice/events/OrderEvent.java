package com.order.microservice.events;

import com.order.microservice.dto.CustomerOrder;

public class OrderEvent {

    private String type;
    
    private CustomerOrder order;

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

    
}
