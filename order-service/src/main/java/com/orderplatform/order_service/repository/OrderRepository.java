package com.orderplatform.order_service.repository;

import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findALlByStatus(OrderStatus status, Pageable pageable);

    //Temp solution for resolving N+1 problem
    @Query(
            """ 
                    SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items 
                    """)
    List<Order> findALlWIthItems();
}
