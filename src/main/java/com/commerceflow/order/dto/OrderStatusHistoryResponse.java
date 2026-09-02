package com.commerceflow.order.dto;

import com.commerceflow.order.OrderStatus;

import java.time.LocalDateTime;

public class OrderStatusHistoryResponse {

    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private LocalDateTime changedAt;

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(OrderStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}