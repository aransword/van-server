package dev.fisa.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record VanRequest(
    byte[] rawIsoMessage
) {

}
