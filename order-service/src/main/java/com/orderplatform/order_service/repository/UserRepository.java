package com.orderplatform.order_service.repository;

import com.orderplatform.order_service.entity.Order;
import com.orderplatform.order_service.entity.OrderStatus;
import com.orderplatform.order_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
