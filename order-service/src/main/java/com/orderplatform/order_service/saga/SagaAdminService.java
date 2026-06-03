package com.orderplatform.order_service.saga;

import com.orderplatform.order_service.saga.dto.ManualResolveRequest;
import com.orderplatform.order_service.saga.dto.ManualReviewRequest;
import com.orderplatform.order_service.saga.dto.SagaInstanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaAdminService {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStateMachine sagaStateMachine;
    private final SagaInstanceMapper sagaInstanceMapper;
    private final SagaRetryService sagaRetryService;

    public Page<SagaInstanceResponse> getSagas(
            SagaStatus status,
            Pageable pageable
    ) {
        Page<SagaInstance> sagas = status == null
                ? sagaInstanceRepository.findAll(pageable)
                : sagaInstanceRepository.findByStatus(status, pageable);

        return sagas.map(sagaInstanceMapper::toResponse);
    }

    public SagaInstanceResponse getSaga(Long id) {
        SagaInstance saga = sagaInstanceRepository.findById(id)
                .orElseThrow(() -> new InvalidSagaStateException(
                        "Saga not found: id=" + id
                ));

        return sagaInstanceMapper.toResponse(saga);
    }

    public SagaInstanceResponse markManualReview(
            Long id,
            ManualReviewRequest request
    ) {
        SagaInstance saga = sagaInstanceRepository.findById(id)
                .orElseThrow(() -> new InvalidSagaStateException(
                        "Saga not found: id=" + id
                ));

        SagaInstance updated = sagaStateMachine.markManualReview(
                saga.getOrderId(),
                request.reason()
        );

        return sagaInstanceMapper.toResponse(updated);
    }

    public SagaInstanceResponse resolveManually(
            Long id,
            ManualResolveRequest request
    ) {
        SagaInstance saga = sagaInstanceRepository.findById(id)
                .orElseThrow(() -> new InvalidSagaStateException(
                        "Saga not found: id=" + id
                ));

        SagaInstance updated = sagaStateMachine.resolveManualReview(
                saga.getOrderId(),
                request.finalStatus(),
                request.reason()
        );

        return sagaInstanceMapper.toResponse(updated);
    }

    public SagaInstanceResponse retry(Long id) {
        SagaInstance saga = sagaRetryService.retry(id);

        return sagaInstanceMapper.toResponse(saga);
    }
}
