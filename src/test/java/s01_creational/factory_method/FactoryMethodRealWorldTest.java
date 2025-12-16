package s01_creational.factory_method;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Factory Method 패턴 - 실무 적용 예제
 *
 * 시나리오: 알림 시스템
 * - 다양한 채널 (SMS, Email, Push, Slack)로 알림을 발송
 * - 각 채널별로 발송 로직이 다름
 * - 새로운 채널 추가가 자주 발생
 *
 * 실무 포인트:
 * 1. 새로운 알림 채널 추가 시 OCP 준수
 * 2. 각 채널별 설정/검증 로직 캡슐화
 * 3. 테스트 용이성 (Mock Creator 주입 가능)
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FactoryMethodRealWorldTest {

    // ===== Product: 알림 인터페이스 =====
    interface Notification {
        void send(String recipient, String message);

        String getChannel();

        boolean isDelivered();

        NotificationResult getResult();
    }

    record NotificationResult(
            boolean success, String channel, String recipient, LocalDateTime sentAt, String errorMessage) {
        static NotificationResult success(String channel, String recipient) {
            return new NotificationResult(true, channel, recipient, LocalDateTime.now(), null);
        }

        static NotificationResult failure(String channel, String recipient, String error) {
            return new NotificationResult(false, channel, recipient, LocalDateTime.now(), error);
        }
    }

    // ===== Concrete Products =====
    static class SmsNotification implements Notification {
        private final String senderNumber;
        private NotificationResult result;

        SmsNotification(String senderNumber) {
            this.senderNumber = senderNumber;
        }

        @Override
        public void send(String recipient, String message) {
            // 실제로는 SMS API 호출
            if (!isValidPhoneNumber(recipient)) {
                result = NotificationResult.failure("SMS", recipient, "Invalid phone number");
                return;
            }

            // SMS 발송 로직 (시뮬레이션)
            System.out.printf(
                    "[SMS] From: %s, To: %s, Message: %s%n", senderNumber, recipient, truncateForSms(message));
            result = NotificationResult.success("SMS", recipient);
        }

        @Override
        public String getChannel() {
            return "SMS";
        }

        @Override
        public boolean isDelivered() {
            return result != null && result.success();
        }

        @Override
        public NotificationResult getResult() {
            return result;
        }

        private boolean isValidPhoneNumber(String phone) {
            return phone != null && phone.matches("^010-\\d{4}-\\d{4}$");
        }

        private String truncateForSms(String message) {
            return message.length() > 90 ? message.substring(0, 87) + "..." : message;
        }
    }

    static class EmailNotification implements Notification {
        private final String fromAddress;
        private final String smtpServer;
        private NotificationResult result;

        EmailNotification(String fromAddress, String smtpServer) {
            this.fromAddress = fromAddress;
            this.smtpServer = smtpServer;
        }

        @Override
        public void send(String recipient, String message) {
            if (!isValidEmail(recipient)) {
                result = NotificationResult.failure("Email", recipient, "Invalid email address");
                return;
            }

            // 이메일 발송 로직 (시뮬레이션)
            System.out.printf(
                    "[Email] Server: %s, From: %s, To: %s, Body: %s%n", smtpServer, fromAddress, recipient, message);
            result = NotificationResult.success("Email", recipient);
        }

        @Override
        public String getChannel() {
            return "Email";
        }

        @Override
        public boolean isDelivered() {
            return result != null && result.success();
        }

        @Override
        public NotificationResult getResult() {
            return result;
        }

        private boolean isValidEmail(String email) {
            return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
        }
    }

    static class PushNotification implements Notification {
        private final String appId;
        private final String apiKey;
        private NotificationResult result;

        PushNotification(String appId, String apiKey) {
            this.appId = appId;
            this.apiKey = apiKey;
        }

        @Override
        public void send(String recipient, String message) {
            if (!isValidDeviceToken(recipient)) {
                result = NotificationResult.failure("Push", recipient, "Invalid device token");
                return;
            }

            // 푸시 발송 로직 (시뮬레이션)
            System.out.printf("[Push] App: %s, Device: %s, Message: %s%n", appId, recipient, message);
            result = NotificationResult.success("Push", recipient);
        }

        @Override
        public String getChannel() {
            return "Push";
        }

        @Override
        public boolean isDelivered() {
            return result != null && result.success();
        }

        @Override
        public NotificationResult getResult() {
            return result;
        }

        private boolean isValidDeviceToken(String token) {
            return token != null && token.length() >= 32;
        }
    }

    // ===== Creator: 알림 발송 서비스 =====
    abstract static class NotificationService {

        private final List<NotificationResult> history = new ArrayList<>();

        /**
         * Factory Method - 서브클래스가 구현
         */
        protected abstract Notification createNotification();

        /**
         * Template Method - 알림 발송의 공통 흐름
         */
        public NotificationResult sendNotification(String recipient, String message) {
            // 1. 발송 전 로깅
            logBefore(recipient, message);

            // 2. 알림 객체 생성 (Factory Method 호출)
            Notification notification = createNotification();

            // 3. 발송
            notification.send(recipient, message);

            // 4. 결과 기록
            NotificationResult result = notification.getResult();
            history.add(result);

            // 5. 발송 후 로깅
            logAfter(result);

            return result;
        }

        /**
         * 대량 발송
         */
        public List<NotificationResult> sendBulk(List<String> recipients, String message) {
            return recipients.stream()
                    .map(recipient -> sendNotification(recipient, message))
                    .toList();
        }

        public List<NotificationResult> getHistory() {
            return List.copyOf(history);
        }

        protected void logBefore(String recipient, String message) {
            System.out.printf("Sending to %s: %s%n", recipient, message);
        }

        protected void logAfter(NotificationResult result) {
            if (result.success()) {
                System.out.printf("Success: %s -> %s%n", result.channel(), result.recipient());
            } else {
                System.out.printf(
                        "Failed: %s -> %s (%s)%n", result.channel(), result.recipient(), result.errorMessage());
            }
        }
    }

    // ===== Concrete Creators =====
    static class SmsNotificationService extends NotificationService {
        private final String senderNumber;

        SmsNotificationService(String senderNumber) {
            this.senderNumber = senderNumber;
        }

        @Override
        protected Notification createNotification() {
            return new SmsNotification(senderNumber);
        }
    }

    static class EmailNotificationService extends NotificationService {
        private final String fromAddress;
        private final String smtpServer;

        EmailNotificationService(String fromAddress, String smtpServer) {
            this.fromAddress = fromAddress;
            this.smtpServer = smtpServer;
        }

        @Override
        protected Notification createNotification() {
            return new EmailNotification(fromAddress, smtpServer);
        }
    }

    static class PushNotificationService extends NotificationService {
        private final String appId;
        private final String apiKey;

        PushNotificationService(String appId, String apiKey) {
            this.appId = appId;
            this.apiKey = apiKey;
        }

        @Override
        protected Notification createNotification() {
            return new PushNotification(appId, apiKey);
        }
    }

    @Nested
    class 알림_발송_기본_시나리오 {

        @Test
        void SMS_알림을_발송할_수_있다() {
            NotificationService smsService = new SmsNotificationService("010-1234-5678");

            NotificationResult result = smsService.sendNotification("010-9999-8888", "주문이 완료되었습니다.");

            assertThat(result.success()).isTrue();
            assertThat(result.channel()).isEqualTo("SMS");
        }

        @Test
        void Email_알림을_발송할_수_있다() {
            NotificationService emailService = new EmailNotificationService("noreply@shop.com", "smtp.shop.com");

            NotificationResult result = emailService.sendNotification("customer@gmail.com", "주문이 완료되었습니다.");

            assertThat(result.success()).isTrue();
            assertThat(result.channel()).isEqualTo("Email");
        }

        @Test
        void Push_알림을_발송할_수_있다() {
            NotificationService pushService = new PushNotificationService("com.shop.app", "api-key-12345");

            NotificationResult result =
                    pushService.sendNotification("device-token-32chars-or-more-xxxxx", "주문이 완료되었습니다.");

            assertThat(result.success()).isTrue();
            assertThat(result.channel()).isEqualTo("Push");
        }
    }
}
