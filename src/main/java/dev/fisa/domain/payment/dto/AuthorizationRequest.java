package dev.fisa.domain.payment.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AuthorizationRequest(
    String cardNumber,
    BigDecimal amount,
    String transactionId,
    String terminalId,
    String merchantId
) {
}
