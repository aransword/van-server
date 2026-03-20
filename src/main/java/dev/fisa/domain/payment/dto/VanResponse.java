package dev.fisa.domain.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VanResponse(
    String transactionId,
    String approvalNumber,
    String responseCode,
    String message,
    BigDecimal amount,
    LocalDateTime authorizationDate,
    boolean approved
) {

    public static VanResponse from(CardAuthResponse cardResponse) {
        return VanResponse.builder()
                          .transactionId(cardResponse.transactionId())
                          .approvalNumber(cardResponse.approvalNumber())
                          .responseCode(cardResponse.responseCode())
                          .message(cardResponse.message())
                          .amount(cardResponse.amount())
                          .authorizationDate(cardResponse.authorizationDate())
                          .approved(cardResponse.approved())
                          .build();
    }
}