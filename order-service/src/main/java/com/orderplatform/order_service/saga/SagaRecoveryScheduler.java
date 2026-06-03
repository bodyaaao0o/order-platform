package com.orderplatform.order_service.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaRecoveryScheduler {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStateMachine sagaStateMachine;

    @Value("${saga.recovery.stuck-timeout-minutes:10}")
    private long stuckTimeoutMinutes;

    @Scheduled(fixedDelayString = "${saga.recovery.fixed-delay-ms:60000}")
    public void detectStuckSagas() {
        LocalDateTime updatedBefore =
                LocalDateTime.now().minusMinutes(stuckTimeoutMinutes);

        List<SagaStatus> recoverableStatuses = List.of(
                SagaStatus.INVENTORY_RESERVATION_REQUESTED,
                SagaStatus.PAYMENT_REQUESTED,
                SagaStatus.COMPENSATION_REQUESTED
        );

        List<SagaInstance> stuckSagas =
                sagaInstanceRepository.findByStatusInAndUpdatedAtBefore(
                        recoverableStatuses,
                        updatedBefore
                );

        if (stuckSagas.isEmpty()) {
            return;
        }

        log.warn(
                "Detected stuck sagas: count={}, updatedBefore={}",
                stuckSagas.size(),
                updatedBefore
        );

        stuckSagas.forEach(this::moveToManualReview);
    }

    private void moveToManualReview(SagaInstance saga) {
        String reason = "Saga stuck in status "
                + saga.getStatus()
                + " since "
                + saga.getUpdatedAt();

        sagaStateMachine.markManualReview(
                saga.getOrderId(),
                reason
        );
    }
}
