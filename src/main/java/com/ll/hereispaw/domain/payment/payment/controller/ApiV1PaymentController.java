package com.ll.hereispaw.domain.payment.payment.controller;

import com.ll.hereispaw.domain.payment.payment.dto.PaymentRequest;
import com.ll.hereispaw.domain.payment.point.entity.Point;
import com.ll.hereispaw.domain.payment.payment.service.PaymentService;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.webMvc.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

// TODO: response 객체 관련 수정해야 함. GlobalResponse
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class ApiV1PaymentController {
    private final PaymentService paymentService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JSONParser parser = new JSONParser();

    // 결제 승인
    @PostMapping("/confirm")
    public GlobalResponse<Object> confirmPayment(@LoginUser Point loginUser, @RequestBody PaymentRequest request) throws Exception {
        if (loginUser == null) {
            return GlobalResponse.error(ErrorCode.ACCESS_DENIED);
        }

        try {
            // request 로깅
            logger.info("Received payment request: {}", request);

            // 필수 파라미터(Id, Amount, PaymentKey)가 누락되었는지 확인
            if (request.getOrderId() == null || request.getAmount() == null || request.getPaymentKey() == null) {
                return GlobalResponse.error(ErrorCode.MISSING_INPUT_VALUE);
            }

            // 결제 승인 API 요청을 위한 JSON 객체 생성
            JSONObject obj = new JSONObject();
            obj.put("orderId", request.getOrderId());
            obj.put("amount", request.getAmount());
            obj.put("paymentKey", request.getPaymentKey());

            // TODO: 개발자센터에 로그인해서 내 결제위젯 연동 키 > 시크릿 키를 입력하세요. 시크릿 키는 외부에 공개되면 안돼요.
            // @docs https://docs.tosspayments.com/reference/using-api/api-keys
            String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

            // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
            // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
            // @docs https://docs.tosspayments.com/reference/using-api/authorization#%EC%9D%B8%EC%A6%9D
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
            String authorizations = "Basic " + new String(encodedBytes);

            // 결제 승인 API를 호출하세요.
            // 결제를 승인하면 결제수단에서 금액이 차감돼요.
            // @docs https://docs.tosspayments.com/guides/v2/payment-widget/integration#3-결제-승인하기
            URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", authorizations);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // request 전송
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = obj.toString().getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
                outputStream.flush();
            }

            // 응답 코드
            int code = connection.getResponseCode();

            // 응답 코드에 따른 세부 처리
            try (InputStream responseStream = code == 200 ? connection.getInputStream() : connection.getErrorStream();
                 Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
                JSONObject jsonObject = (JSONObject) parser.parse(reader);

                // 결제 실패
                if (code != 200) {
                    logger.error("Payment failed: {}", jsonObject);
                    return GlobalResponse.error(ErrorCode.PAYMENT_FAILED, jsonObject);
                }

                paymentService.savePaymentData(jsonObject, loginUser);
                System.out.println("ApiV1PaymentController points : " + loginUser.getPoints());

                // 결제 성공
                logger.info("Payment successful: {}", jsonObject);
                return GlobalResponse.success(jsonObject);
            }
        } catch (Exception e) {
            // 결제 오류
            logger.error("Payment processing error", e);
            return GlobalResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원 포인트 조회
    @GetMapping("/points")
    public GlobalResponse<Integer> getPoints(@LoginUser Point loginUser) {
        if (loginUser == null) {
            return GlobalResponse.error(ErrorCode.ACCESS_DENIED);
        }

        try {
            Integer points = loginUser.getPoints();
            return GlobalResponse.success(points);
        } catch (Exception e) {
            return GlobalResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
