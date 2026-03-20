package dev.fisa.domain.payment.service;

import dev.fisa.domain.payment.dto.AuthorizationRequest;
import dev.fisa.domain.payment.dto.VanResponse;
import dev.fisa.domain.payment.entity.Payment;
import dev.fisa.domain.payment.entity.Status;
import dev.fisa.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CardClientService cardClientService;

    @Transactional
    public VanResponse processPayment(AuthorizationRequest authRequest) {

        // 1. PENDING 상태로 저장
        Payment payment = Payment.builder()
                                 .transactionId(authRequest.transactionId())
                                 .cardNumber(authRequest.cardNumber())
                                 .amount(authRequest.amount())
                                 .merchantId(authRequest.merchantId())
                                 .terminalId(authRequest.terminalId())
                                 .status(Status.PENDING)
                                 .build();

        paymentRepository.save(payment);
        log.info("결제 저장 (PENDING) - 거래 ID: {}", authRequest.transactionId());

        // 2. 카드사 승인 요청
        VanResponse response = cardClientService.requestAuthorization(authRequest);

        // 3. 응답에 따라 상태 변경
        if (response.approved()) {
            payment.complete();
            log.info("결제 상태 변경 (COMPLETED) - 거래 ID: {}", authRequest.transactionId());
        } else {
            payment.fail();
            log.info("결제 상태 변경 (FAILED) - 거래 ID: {}, 사유: {}",
                authRequest.transactionId(), response.message());
        }

        return response;
    }
}