package com.nawasenahost.paymentservice.rest;

import com.nawasenahost.paymentservice.dto.PaymentRequest;
import com.nawasenahost.paymentservice.entity.Payment;
import com.nawasenahost.paymentservice.entity.PaymentMethod;
import com.nawasenahost.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentRestController {

    private final PaymentService paymentService;

    public PaymentRestController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/payments")
    public List<Payment> getPayments() {
        return paymentService.findAll();
    }

    @GetMapping("/payments/{paymentId}")
    public Payment getPayment(@PathVariable int paymentId) {
        return paymentService.findById(paymentId);
    }

    @PostMapping("/payments")
    public Payment addPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        return paymentService.save(paymentRequest);
    }

    @PutMapping("/payments/{paymentId}")
    public Payment updatePayment(@PathVariable int paymentId,
                                 @Valid @RequestBody PaymentRequest paymentRequest) {
        return paymentService.update(paymentId, paymentRequest);
    }

    @DeleteMapping("/payments/{paymentId}")
    public String deletePayment(@PathVariable int paymentId) {
        paymentService.deleteById(paymentId);
        return "Payment with Id: " + paymentId + " deleted";
    }

    @PostMapping("/payments/confirm-payment")
    public Payment confirmPayment(@RequestParam int paymentId,
                                  @RequestParam PaymentMethod paymentMethod) {
        return paymentService.confirmPayment(paymentId, paymentMethod);
    }

    @PostMapping("/payments/create-payment")
    public PaymentRequest createPayment(@RequestParam int bookingId) {
        return paymentService.createPayment(bookingId);
    }

    @PutMapping("/payments/cancel-payment-by-booking-id")
    public Payment cancelPaymentByBookingId(@RequestParam int bookingId) {
        return paymentService.cancelPaymentByBookingId(bookingId);
    }

}
