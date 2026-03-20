package dev.fisa.domain.payment.service;

import dev.fisa.domain.payment.dto.CardRequest;
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

    public VanResponse requestAuthorization(CardRequest cardRequest) {

        log.info("카드사 승인 요청 - 거래 ID: {}, 금액: {}",
            cardRequest.transactionId(), cardRequest.amount());

        try {
            VanResponse response = cardAuthRestClient.post()
                                                     .uri("/api/authorization/request")
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .body(cardRequest)
                                                     .retrieve()
                                                     .body(VanResponse.class);

            log.info("카드사 승인 응답 - 거래 ID: {}, 응답코드: {}, 승인여부: {}",
                response.transactionId(), response.responseCode(), response.approved());

            return response;
        } catch (Exception e) {
            log.error("카드사 통신 실패 - 거래 ID: {}", cardRequest.transactionId(), e);

            return VanResponse.builder()
                              .transactionId(cardRequest.transactionId())
                              .responseCode("96")
                              .message("VAN-카드사 통신 오류: " + e.getMessage())
                              .amount(cardRequest.amount())
                              .approved(false)
                              .build();
        }
    }
}