package com.nawasenahost.paymentservice.rest;

import com.nawasenahost.paymentservice.dto.PaymentReport;
import com.nawasenahost.paymentservice.dto.PaymentRequest;
import com.nawasenahost.paymentservice.dto.PaymentResponse;
import com.nawasenahost.paymentservice.entity.Payment;
import com.nawasenahost.paymentservice.entity.PaymentMethod;
import com.nawasenahost.paymentservice.entity.PaymentSource;
import com.nawasenahost.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/payments/hotel/{hotelId}")
    public List<Payment> getPaymentsByHotelId(@PathVariable int hotelId) {
        return paymentService.findByHotelId(hotelId);
    }

    @GetMapping("/payments/hotel/{hotelId}/report")
    public PaymentReport getReport(@PathVariable int hotelId,
                                   @RequestParam(required = false) LocalDate from,
                                   @RequestParam(required = false) LocalDate to) {

        LocalDate endDate = to != null ? to : LocalDate.now();
        LocalDate startDate = from != null ? from : endDate.minusDays(29);

        return paymentService.getReport(hotelId, startDate, endDate);
    }

    @PutMapping("/payments/refund-by-booking-id")
    public Payment refundPaymentByBookingId(@RequestParam int bookingId) {
        return paymentService.refundPaymentByBookingId(bookingId);
    }

    @PostMapping("/payments/backfill-hotel-id")
    public String backfillHotelId() {
        return "Payments updated: " + paymentService.backfillHotelId();
    }

    @GetMapping("/payments/booking/{bookingId}")
    public Payment getPaymentByBookingId(@PathVariable int bookingId) {
        return paymentService.findByBookingId(bookingId);
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
                                  @RequestParam PaymentMethod paymentMethod,
                                  @RequestParam PaymentSource paymentSource) {
        return paymentService.confirmPayment(paymentId, paymentMethod, paymentSource);
    }

    @PostMapping("/payments/create-payment")
    public PaymentResponse createPayment(@RequestParam int bookingId) {
        return paymentService.createPayment(bookingId);
    }

    @PutMapping("/payments/cancel-payment-by-booking-id")
    public Payment cancelPaymentByBookingId(@RequestParam int bookingId) {
        return paymentService.cancelPaymentByBookingId(bookingId);
    }

}
