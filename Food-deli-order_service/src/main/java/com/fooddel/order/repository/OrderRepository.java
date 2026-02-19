package com.fooddel.order.repository;

import com.fooddel.order.entity.Order;
import com.fooddel.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByRestaurantId(Long restaurantId);

    List<Order> findByOrderStatus(OrderStatus status);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
