package com.nawasenahost.paymentservice.dto;

import com.nawasenahost.paymentservice.entity.PaymentMethod;
import com.nawasenahost.paymentservice.entity.PaymentSource;
import com.nawasenahost.paymentservice.entity.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PaymentRequest {

    @Min(value = 1, message = "Booking ID must be greater than 0")
    private int bookingId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private PaymentSource paymentSource;

    private String provider;

    private String transactionId;


    public @Min(value = 1, message = "Booking ID must be greater than 0") int getBookingId() {
        return bookingId;
    }

    public void setBookingId(@Min(value = 1, message = "Booking ID must be greater than 0") int bookingId) {
        this.bookingId = bookingId;
    }

    public @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than 0") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than 0") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotBlank String getCurrency() {
        return currency;
    }

    public void setCurrency(@NotBlank String currency) {
        this.currency = currency;
    }

    public @NotNull PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@NotNull PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public @NotNull PaymentSource getPaymentSource() {
        return paymentSource;
    }

    public void setPaymentSource(@NotNull PaymentSource paymentSource) {
        this.paymentSource = paymentSource;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
