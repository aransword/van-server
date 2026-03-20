package dev.fisa.domain.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CardAuthResponse(
    String transactionId,
    String approvalNumber,
    String responseCode,
    String message,
    BigDecimal amount,
    LocalDateTime authorizationDate,
    boolean approved
) {

}