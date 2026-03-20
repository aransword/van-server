package dev.fisa.domain.payment.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CardRequest(
    String transactionId,
    String cardNumber,
    BigDecimal amount,
    String merchantId,
    String terminalId
) {
    public static CardRequest from(VanRequest request) {
        return CardRequest.builder()
                          .transactionId(request.transactionId())
                          .cardNumber(request.cardNumber())
                          .amount(request.amount())
                          .merchantId(request.merchantId())
                          .terminalId(request.terminalId())
                          .build();
    }
}
