package com.nawasenahost.adminpanel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record PaymentReport(LocalDate from,
                            LocalDate to,
                            BigDecimal totalRevenue,
                            BigDecimal refundedAmount,
                            long successCount,
                            long refundedCount,
                            long pendingCount,
                            BigDecimal averagePayment,
                            Map<String, BigDecimal> revenueByMethod,
                            Map<String, BigDecimal> revenueBySource,
                            List<DailyAmount> dailyRevenue) {
}
