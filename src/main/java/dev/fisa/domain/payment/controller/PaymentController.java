package dev.fisa.domain.payment.controller;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import dev.fisa.domain.payment.dto.VanRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

@RestController
public class PaymentController {

    private MessageFactory<IsoMessage> messageFactory;

    @PostConstruct
    public void init() throws Exception {
        // 앞서 만든 j8583.xml 파일이 src/main/resources 에 있어야 합니다.
        messageFactory = ConfigParser.createFromClasspathConfig("j8583.xml");
        messageFactory.setCharacterEncoding(StandardCharsets.UTF_8.name());
        messageFactory.setUseBinaryMessages(false);
        System.out.println("✅ J8583 MessageFactory 초기화 완료!");
    }

    @PostMapping("/van")
    public ResponseEntity<?> postVANInfo(@RequestBody VanRequest vanRequest) {
        try {

            byte[] messageBytes = vanRequest.rawIsoMessage();

            IsoMessage parsedMsg = messageFactory.parseMessage(messageBytes, 0);

            if(parsedMsg == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파싱 실패: 전문 형식이 잘못되었거나 비트맵이 일치하지 않습니다.");
            }

            String cardNumber = parsedMsg.hasField(2) ? parsedMsg.getObjectValue(2).toString() : null;
            BigDecimal amount = parsedMsg.hasField(4) ? new BigDecimal(parsedMsg.getObjectValue(4).toString()) : BigDecimal.ZERO;;
            String transactionId = parsedMsg.hasField(37) ? parsedMsg.getObjectValue(37).toString() : null;
            String terminalId = parsedMsg.hasField(41) ? parsedMsg.getObjectValue(41).toString().trim() : null;
            String merchantId = parsedMsg.hasField(42) ? parsedMsg.getObjectValue(42).toString().trim() : null;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("인증 요청이 전송되었습니다.");
    }
}
