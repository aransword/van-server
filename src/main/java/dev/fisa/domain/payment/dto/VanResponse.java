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

}
