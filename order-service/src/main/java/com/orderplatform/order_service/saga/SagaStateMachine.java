package com.orderplatform.order_service.saga;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaStateMachine {

    private static final int DEFAULT_MAX_RETRIES = 3;

    private final SagaInstanceRepository sagaInstanceRepository;

    @Transactional
    public SagaInstance startSaga(Long orderId) {
        return sagaInstanceRepository.findByOrderId(orderId)
                .orElseGet(() -> {
                    SagaInstance saga = SagaInstance.builder()
                            .orderId(orderId)
                            .status(SagaStatus.STARTED)
                            .currentStep(SagaStep.ORDER_CREATED)
                            .retryCount(0)
                            .maxRetries(DEFAULT_MAX_RETRIES)
                            .lastEventType("ORDER_CREATED")
                            .lastEventAt(LocalDateTime.now())
                            .build();

                    SagaInstance saved = sagaInstanceRepository.save(saga);

                    log.info("Saga started: orderId={}, sagaId={}",
                            orderId,
                            saved.getId());

                    return saved;
                });
    }

    @Transactional
    public SagaInstance markInventoryRequested(Long orderId) {
        SagaInstance saga = getSaga(orderId);

        if(saga.getStatus() == SagaStatus.INVENTORY_RESERVATION_REQUESTED) {
            return saga;
        }

        ensureStatus(
                saga,
                SagaStatus.STARTED,
                "Inventory reservation can only be requested after saga start"
        );

        moveTo(
                saga,
                SagaStatus.INVENTORY_RESERVATION_REQUESTED,
                SagaStep.INVENTORY_RESERVATION,
                "INVENTORY_RESERVE_REQUESTED",
                null
        );

        return saga;
    }

    @Transactional
    public SagaInstance markInventoryReserved(Long orderId) {
        SagaInstance saga = getSaga(orderId);

        if (saga.getStatus() == SagaStatus.INVENTORY_RESERVED) {
            return saga;
        }

        if (saga.isTerminal()) {
            log.info("Ignoring inventory reserved event for terminal saga: orderId={}, status={}",
                    orderId,
                    saga.getStatus());
            return saga;
        }

        ensureStatus(
                saga,
                SagaStatus.INVENTORY_RESERVATION_REQUESTED,
                "Inventory can only be reserved after reservation request"
        );

        moveTo(
                saga,
                SagaStatus.INVENTORY_RESERVED,
                SagaStep.PAYMENT_PROCESSING,
                "INVENTORY_RESERVED",
                null
        );

        return saga;
    }

    @Transactional
    public SagaInstance markInventoryFailed(Long orderId, String reason) {
        SagaInstance saga = getSaga(orderId);

        if (saga.isTerminal()) {
            return saga;
        }

        moveTo(
                saga,
                SagaStatus.FAILED,
                SagaStep.FINISHED,
                "INVENTORY_FAILED",
                reason
        );

        saga.setCompletedAt(LocalDateTime.now());

        return saga;
    }

    @Transactional
    public SagaInstance markPaymentRequested(Long orderId) {
        SagaInstance saga = getSaga(orderId);

        if (saga.getStatus() == SagaStatus.PAYMENT_REQUESTED) {
            return saga;
        }

        ensureStatus(
                saga,
                SagaStatus.INVENTORY_RESERVED,
                "Payment can only be requested after inventory reservation"
        );

        moveTo(
                saga,
                SagaStatus.PAYMENT_REQUESTED,
                SagaStep.PAYMENT_PROCESSING,
                "PAYMENT_REQUESTED",
                null
        );

        return saga;
    }

    @Transactional
    public SagaInstance markPaymentCompleted(Long orderId) {
        SagaInstance saga = getSaga(orderId);

        if (saga.getStatus() == SagaStatus.PAYMENT_COMPLETED
                || saga.getStatus() == SagaStatus.SHIPMENT_REQUESTED
                || saga.getStatus() == SagaStatus.SHIPMENT_CREATED
                || saga.getStatus() == SagaStatus.COMPLETED) {
            return saga;
        }

        if (saga.isTerminal()) {
            log.info("Ignoring payment completed event for terminal saga: orderId={}, status={}",
                    orderId,
                    saga.getStatus());
            return saga;
        }

        ensureStatus(
                saga,
                SagaStatus.PAYMENT_REQUESTED,
                "Payment can only complete after payment request"
        );

        moveTo(
                saga,
                SagaStatus.PAYMENT_COMPLETED,
                SagaStep.SHIPMENT_PROCESSING,
                "PAYMENT_COMPLETED",
                null
        );

        return saga;
    }

    @Transactional
    public SagaInstance markShipmentRequested(Long orderId) {
        SagaInstance saga = getSaga(orderId);

        if (saga.getStatus() == SagaStatus.SHIPMENT_REQUESTED
                || saga.getStatus() == SagaStatus.SHIPMENT_CREATED
                || saga.getStatus() == SagaStatus.COMPLETED) {
            return saga;
        }

        if (saga.isTerminal()) {
            log.info("Ignoring shipment requested event for terminal saga: orderId={}, status={}",
                    orderId,
                    saga.getStatus());
            return saga;
        }

        ensureStatus(
                saga,
                SagaStatus.PAYMENT_COMPLETED,
                "Shipment can only be requested after payment completion"
        );

        moveTo(
                saga,
                SagaStatus.SHIPMENT_REQUESTED,
                SagaStep.SHIPMENT_PROCESSING,
                "SHIPMENT_REQUESTED",
                null
        );

        return saga;
    }

    @Transactional
    public SagaInstance markShipmentCreated(Long orderId) {
        SagaInstance saga = getSaga(orderId);

        if (saga.getStatus() == SagaStatus.SHIPMENT_CREATED) {
            return saga;
        }

        if (saga.isTerminal()) {
            log.info("Ignoring shipment created event for terminal saga: orderId={}, status={}",
                    orderId,
                    saga.getStatus());
            return saga;
        }

        ensureStatus(
                saga,
                SagaStatus.SHIPMENT_REQUESTED,
                "Shipment can only be created after shipment request"
        );

        moveTo(
                saga,
                SagaStatus.SHIPMENT_CREATED,
                SagaStep.SHIPMENT_PROCESSING,
                "SHIPMENT_CREATED",
                null
        );

        return saga;
    }

    @Transactional
    public SagaInstance markShipmentDelivered(Long orderId) {
        SagaInstance saga = getSaga(orderId);

        if (saga.getStatus() == SagaStatus.COMPLETED) {
            return saga;
        }

        if (saga.isTerminal()) {
            log.info("Ignoring shipment delivered event for terminal saga: orderId={}, status={}",
                    orderId,
                    saga.getStatus());
            return saga;
        }

        ensureStatus(
                saga,
                SagaStatus.SHIPMENT_CREATED,
                "Shipment can only be delivered after shipment creation"
        );

        moveTo(
                saga,
                SagaStatus.COMPLETED,
                SagaStep.FINISHED,
                "SHIPMENT_DELIVERED",
                null
        );

        saga.setCompletedAt(LocalDateTime.now());

        return saga;
    }

    @Transactional
    public SagaInstance markPaymentFailed(Long orderId, String reason) {
        SagaInstance saga = getSaga(orderId);

        if (saga.isTerminal()) {
            return saga;
        }

        ensureStatus(
                saga,
                SagaStatus.PAYMENT_REQUESTED,
                "Payment can only fail after payment request"
        );

        moveTo(
                saga,
                SagaStatus.COMPENSATION_REQUESTED,
                SagaStep.INVENTORY_COMPENSATION,
                "PAYMENT_FAILED",
                reason
        );

        return saga;
    }

    @Transactional
    public SagaInstance markCompensated(Long orderId) {
        SagaInstance saga = getSaga(orderId);

        if (saga.getStatus() == SagaStatus.COMPENSATED) {
            return saga;
        }

        ensureStatus(
                saga,
                SagaStatus.COMPENSATION_REQUESTED,
                "Saga can only be compensated after compensation request"
        );

        moveTo(
                saga,
                SagaStatus.COMPENSATED,
                SagaStep.FINISHED,
                "INVENTORY_RELEASED",
                null
        );

        saga.setCompletedAt(LocalDateTime.now());

        return saga;
    }

    @Transactional
    public SagaInstance markManualReview(Long orderId, String reason) {
        SagaInstance saga = getSaga(orderId);

        if (saga.getStatus() == SagaStatus.NEEDS_MANUAL_REVIEW) {
            return saga;
        }

        moveTo(
                saga,
                SagaStatus.NEEDS_MANUAL_REVIEW,
                saga.getCurrentStep(),
                "MANUAL_REVIEW_REQUIRED",
                reason
        );

        log.warn("Saga moved to manual review: orderId={}, reason={}",
                orderId,
                reason);

        return saga;
    }

    @Transactional
    public SagaInstance incrementRetry(Long orderId) {
        SagaInstance saga = getSaga(orderId);

        if (!saga.canRetry()) {
            markManualReview(
                    orderId,
                    "Max retry attempts reached"
            );

            return saga;
        }

        saga.incrementRetryCount();
        saga.setLastEventAt(LocalDateTime.now());

        log.info("Saga retry incremented: orderId={}, retryCount={}/{}",
                orderId,
                saga.getRetryCount(),
                saga.getMaxRetries());

        return saga;
    }

    @Transactional
    public SagaInstance resolveManualReview(
            Long orderId,
            SagaStatus finalStatus,
            String reason
    ) {
        SagaInstance saga = getSaga(orderId);

        ensureStatus(
                saga,
                SagaStatus.NEEDS_MANUAL_REVIEW,
                "Saga can only be manually resolved from NEEDS_MANUAL_REVIEW"
        );

        if (finalStatus != SagaStatus.COMPLETED
                && finalStatus != SagaStatus.COMPENSATED
                && finalStatus != SagaStatus.FAILED) {
            throw new InvalidSagaStateException(
                    "Manual resolution final status must be COMPLETED, COMPENSATED, or FAILED"
            );
        }

        moveTo(
                saga,
                finalStatus,
                SagaStep.FINISHED,
                "MANUAL_RESOLUTION",
                reason
        );

        saga.setCompletedAt(LocalDateTime.now());

        log.info(
                "Saga manually resolved: orderId={}, finalStatus={}, reason={}",
                orderId,
                finalStatus,
                reason
        );

        return saga;
    }

    private SagaInstance getSaga(Long orderId) {
        return sagaInstanceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new InvalidSagaStateException(
                        "Saga not found for orderId=" + orderId
                ));
    }

    private void moveTo(
            SagaInstance saga,
            SagaStatus status,
            SagaStep step,
            String eventType,
            String failureReason
    ) {
        SagaStatus previousStatus = saga.getStatus();

        saga.setStatus(status);
        saga.setCurrentStep(step);
        saga.setLastEventType(eventType);
        saga.setLastEventAt(LocalDateTime.now());
        saga.setFailureReason(failureReason);

        log.info("Saga state changed: orderId={}, {} -> {}, step={}",
                saga.getOrderId(),
                previousStatus,
                status,
                step);
    }

    private void ensureStatus(
            SagaInstance saga,
            SagaStatus expectedStatus,
            String message
    ) {
        if (saga.getStatus() != expectedStatus) {
            throw new InvalidSagaStateException(
                    message
                            + ". orderId="
                            + saga.getOrderId()
                            + ", currentStatus="
                            + saga.getStatus()
                            + ", expectedStatus="
                            + expectedStatus
            );
        }
    }
}
