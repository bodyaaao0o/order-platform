package com.orderplatform.order_service.saga;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {

    Optional<SagaInstance> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    Page<SagaInstance> findByStatus(SagaStatus status, Pageable pageable);

    List<SagaInstance> findByStatusInAndUpdatedAtBefore(
            Collection<SagaStatus> statuses,
            LocalDateTime updatedBefore
    );
}
