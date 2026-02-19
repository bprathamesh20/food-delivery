package com.foodDelivery.dto;

import com.foodDelivery.entity.DeliveryAgent;

public class UpdateStatusRequest {
    private DeliveryAgent.AgentStatus status;

    public UpdateStatusRequest() {}

    public UpdateStatusRequest(DeliveryAgent.AgentStatus status) {
        this.status = status;
    }

    public DeliveryAgent.AgentStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryAgent.AgentStatus status) {
        this.status = status;
    }
}
