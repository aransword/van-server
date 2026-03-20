package dev.fisa.domain.payment.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record AuthorizationRequest(
    String cardNumber,
    BigDecimal amount,
    String transactionId,
    String terminalId,
    String merchantId
) {

    public static AuthorizationRequest from(String cardNumber, BigDecimal amount,
                                            String transactionId, String terminalId,
                                            String merchantId) {
        return AuthorizationRequest.builder()
                                   .cardNumber(cardNumber)
                                   .amount(amount)
                                   .transactionId(transactionId)
                                   .terminalId(terminalId)
                                   .merchantId(merchantId)
                                   .build();
    }
}
