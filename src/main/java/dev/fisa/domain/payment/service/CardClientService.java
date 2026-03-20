package dev.fisa.domain.payment.service;

import dev.fisa.domain.payment.dto.AuthorizationRequest;
import dev.fisa.domain.payment.dto.CardAuthResponse;
import dev.fisa.domain.payment.dto.VanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardClientService {

    private final RestClient cardAuthRestClient;

    public VanResponse requestAuthorization(AuthorizationRequest request) {

        log.info("카드사 승인 요청 - 거래 ID: {}, 금액: {}",
            request.transactionId(), request.amount());

        try {
            CardAuthResponse cardResponse = cardAuthRestClient.post()
                                                              .uri("/api/authorization/request")
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .body(request)
                                                              .retrieve()
                                                              .body(CardAuthResponse.class);

            log.info("카드사 승인 응답 - 거래 ID: {}, 응답코드: {}, 승인여부: {}",
                cardResponse.transactionId(), cardResponse.responseCode(), cardResponse.approved());

            return VanResponse.from(cardResponse);

        } catch (Exception e) {
            log.error("카드사 통신 실패 - 거래 ID: {}", request.transactionId(), e);

            return VanResponse.builder()
                              .transactionId(request.transactionId())
                              .responseCode("96")
                              .message("VAN-카드사 통신 오류: " + e.getMessage())
                              .amount(request.amount())
                              .approved(false)
                              .build();
        }
    }
}