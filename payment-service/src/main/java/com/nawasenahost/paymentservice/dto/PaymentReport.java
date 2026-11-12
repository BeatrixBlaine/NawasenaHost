package com.nawasenahost.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PaymentReport {

    private LocalDate from;
    private LocalDate to;
    private BigDecimal totalRevenue;
    private BigDecimal refundedAmount;
    private long successCount;
    private long refundedCount;
    private long pendingCount;
    private BigDecimal averagePayment;
    private Map<String, BigDecimal> revenueByMethod;
    private Map<String, BigDecimal> revenueBySource;
    private List<DailyAmount> dailyRevenue;

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public long getRefundedCount() {
        return refundedCount;
    }

    public void setRefundedCount(long refundedCount) {
        this.refundedCount = refundedCount;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public BigDecimal getAveragePayment() {
        return averagePayment;
    }

    public void setAveragePayment(BigDecimal averagePayment) {
        this.averagePayment = averagePayment;
    }

    public Map<String, BigDecimal> getRevenueByMethod() {
        return revenueByMethod;
    }

    public void setRevenueByMethod(Map<String, BigDecimal> revenueByMethod) {
        this.revenueByMethod = revenueByMethod;
    }

    public Map<String, BigDecimal> getRevenueBySource() {
        return revenueBySource;
    }

    public void setRevenueBySource(Map<String, BigDecimal> revenueBySource) {
        this.revenueBySource = revenueBySource;
    }

    public List<DailyAmount> getDailyRevenue() {
        return dailyRevenue;
    }

    public void setDailyRevenue(List<DailyAmount> dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }
}
