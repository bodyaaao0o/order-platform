package com.orderplatform.order_service.controller;

import com.orderplatform.order_service.saga.SagaAdminService;
import com.orderplatform.order_service.saga.SagaStatus;
import com.orderplatform.order_service.saga.dto.ManualResolveRequest;
import com.orderplatform.order_service.saga.dto.ManualReviewRequest;
import com.orderplatform.order_service.saga.dto.SagaInstanceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/sagas")
public class SagaAdminController {

    private final SagaAdminService sagaAdminService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<SagaInstanceResponse> getSagas(
            @RequestParam(required = false) SagaStatus status,
            Pageable pageable
    ) {
        return sagaAdminService.getSagas(status, pageable);
    }

    @PostMapping("/{id}/manual-review")
    @PreAuthorize("hasRole('ADMIN')")
    public SagaInstanceResponse markManualReview(
            @PathVariable Long id,
            @Valid @RequestBody ManualReviewRequest request
    ) {
        return sagaAdminService.markManualReview(id, request);
    }

    @PostMapping("/{id}/manual-resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public SagaInstanceResponse resolveManually(
            @PathVariable Long id,
            @Valid @RequestBody ManualResolveRequest request
    ) {
        return sagaAdminService.resolveManually(id, request);
    }

    @PostMapping("/{id}/retry")
    @PreAuthorize("hasRole('ADMIN')")
    public SagaInstanceResponse retry(@PathVariable Long id) {
        return sagaAdminService.retry(id);
    }
}
