package com.commerceflow.order;

public enum OrderStatus {

    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus newStatus) {

        return switch (this) {

            case PENDING ->
                    newStatus == CONFIRMED
                            || newStatus == CANCELLED;

            case CONFIRMED ->
                    newStatus == SHIPPED
                            || newStatus == CANCELLED;

            case SHIPPED ->
                    newStatus == DELIVERED;

            case DELIVERED, CANCELLED ->
                    false;
        };
    }
}