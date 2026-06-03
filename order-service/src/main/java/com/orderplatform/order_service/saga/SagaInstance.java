package com.orderplatform.order_service.saga;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "saga_instances",
        indexes = {
                @Index(name = "idx_saga_order_id", columnList = "order_id"),
                @Index(name = "idx_saga_status", columnList = "status"),
                @Index(name = "idx_saga_updated_at", columnList = "updated_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false)
    private SagaStep currentStep;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    @Column(name = "last_event_type")
    private String lastEventType;

    @Column(name = "last_event_at")
    private LocalDateTime lastEventAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (lastEventAt == null) {
            lastEventAt = now;
        }

        if (maxRetries == 0) {
            maxRetries = 3;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isTerminal() {
        return status == SagaStatus.COMPLETED
                || status == SagaStatus.COMPENSATED
                || status == SagaStatus.FAILED;
    }

    public boolean needsManualReview() {
        return status == SagaStatus.NEEDS_MANUAL_REVIEW;
    }

    public boolean canRetry() {
        return !isTerminal()
                && !needsManualReview()
                && retryCount < maxRetries;
    }

    public void incrementRetryCount() {
        retryCount++;
    }
}
