package com.commerceflow.payment;

import com.commerceflow.payment.dto.PaymentResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.commerceflow.payment.dto.PaymentResponse;
import com.commerceflow.payment.dto.PaymentResponse;
import com.commerceflow.product.dto.PageResponse;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResponse createPayment(
            @Valid @RequestBody PaymentRequest request
    ) {
        return paymentService.createPayment(request);
    }

    @PutMapping("/{paymentId}/success")
    public ResponseEntity<PaymentResponse> markPaymentSuccess(
            @PathVariable
            @Min(value = 1, message = "Payment ID must be at least 1")
            Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.markPaymentSuccess(paymentId)
        );
    }

    @PutMapping("/{paymentId}/failed")
    public ResponseEntity<PaymentResponse> markPaymentFailed(
            @PathVariable
            @Min(value = 1, message = "Payment ID must be at least 1")
            Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.markPaymentFailed(paymentId)
        );
    }

    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable
            @Min(value = 1, message = "Payment ID must be at least 1")
            Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.refundPayment(paymentId)
        );
    }
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentByOrderId(orderId)
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable
            @Min(value = 1, message = "Payment ID must be at least 1")
            Long paymentId
    ) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(paymentId)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments() {

        return ResponseEntity.ok(
                paymentService.getMyPayments()
        );
    }
    @GetMapping
    public ResponseEntity<PageResponse<PaymentResponse>> getAllPayments(

            @RequestParam(required = false)
            PaymentStatus status,

            @RequestParam(required = false)
            PaymentMethod paymentMethod,

            @RequestParam(required = false)
            LocalDateTime from,

            @RequestParam(required = false)
            LocalDateTime to,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction
    ) {

        return ResponseEntity.ok(
                paymentService.getAllPayments(
                        status,
                        paymentMethod,
                        from,
                        to,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }


}