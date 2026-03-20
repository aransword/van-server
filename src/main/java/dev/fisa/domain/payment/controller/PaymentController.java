package dev.fisa.domain.payment.controller;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import dev.fisa.domain.payment.dto.AuthorizationRequest;
import dev.fisa.domain.payment.dto.VanRequest;
import dev.fisa.domain.payment.dto.VanResponse;
import dev.fisa.domain.payment.service.CardClientService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final CardClientService cardClientService;
    private MessageFactory<IsoMessage> messageFactory;

    @PostConstruct
    public void init() throws Exception {
        messageFactory = ConfigParser.createFromClasspathConfig("j8583.xml");
        messageFactory.setCharacterEncoding(StandardCharsets.UTF_8.name());
        messageFactory.setUseBinaryMessages(false);
        log.info("J8583 MessageFactory 초기화 완료");
    }

    @PostMapping("/van")
    public ResponseEntity<?> postVANInfo(@RequestBody VanRequest vanRequest) {
        try {
            byte[] messageBytes = vanRequest.rawIsoMessage();
            IsoMessage parsedMsg = messageFactory.parseMessage(messageBytes, 0);

            if (parsedMsg == null) {
                return ResponseEntity.badRequest()
                                     .body("파싱 실패: 전문 형식이 잘못되었거나 비트맵이 일치하지 않습니다.");
            }

            String cardNumber = parsedMsg.hasField(2) ? parsedMsg.getObjectValue(2).toString() : null;
            BigDecimal amount = parsedMsg.hasField(4) ? new BigDecimal(parsedMsg.getObjectValue(4).toString()) : BigDecimal.ZERO;
            String transactionId = parsedMsg.hasField(37) ? parsedMsg.getObjectValue(37).toString() : null;
            String terminalId = parsedMsg.hasField(41) ? parsedMsg.getObjectValue(41).toString().trim() : null;
            String merchantId = parsedMsg.hasField(42) ? parsedMsg.getObjectValue(42).toString().trim() : null;

            AuthorizationRequest authRequest = AuthorizationRequest.from(
                cardNumber, amount, transactionId, terminalId, merchantId);

            VanResponse response = cardClientService.requestAuthorization(authRequest);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("VAN 결제 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("시스템 오류: " + e.getMessage());
        }
    }
}