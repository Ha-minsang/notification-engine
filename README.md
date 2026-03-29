# notification-engine

Spring Boot 기반 알림 발송 라이브러리입니다.
SSE, Email, Slack 채널을 단일 인터페이스로 통합하여 사용할 수 있습니다.

---

## 지원 채널

- SSE (Server-Sent Events)
- Email (SMTP)
- Slack (Webhook)

---

## 요구 사항

- Java 17
- Spring Boot 4.0.x
- Redis (SSE 채널 사용 시)

---

## 시작하기

### 의존성 추가

`settings.gradle`에 JitPack 레포지토리를 추가합니다.

```groovy
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

필요한 모듈만 선택하여 추가합니다.

```groovy
dependencies {
    // 필수
    implementation 'com.github.Ha-minsang.notification-engine:engine-core:v1.0.0'

    // 사용할 채널에 따라 선택
    implementation 'com.github.Ha-minsang.notification-engine:engine-sse:v1.0.0'
    implementation 'com.github.Ha-minsang.notification-engine:engine-email:v1.0.0'
    implementation 'com.github.Ha-minsang.notification-engine:engine-slack:v1.0.0'
}
```

---

## 설정

### SSE

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### Email

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true

notification:
  email:
    from: your-email@gmail.com
```

---

## 사용법

`NotificationEngine` 빈을 주입받아 사용합니다.

```java
@RequiredArgsConstructor
public class OrderService {

    private final NotificationEngine notificationEngine;

    public void notifyOrderComplete(String userId) {
        notificationEngine.send(
            NotificationRequest.builder()
                .targetId(userId)
                .channel(NotificationChannel.SSE)
                .payload(NotificationPayload.builder()
                    .title("주문 완료")
                    .body("주문이 정상 접수되었습니다.")
                    .build())
                .build()
        );
    }
}
```

### 다건 발송

```java
List<NotificationRequest> requests = users.stream()
    .map(user -> NotificationRequest.builder()
        .targetId(user.getId())
        .channel(NotificationChannel.EMAIL)
        .payload(payload)
        .build())
    .toList();

notificationEngine.sendBulk(requests);
```

---

## 구조

```
engine-core       인터페이스 및 공통 객체 정의
engine-sse        SSE 구현체 (Redis Pub/Sub 기반)
engine-email      Email 구현체 (SMTP)
engine-slack      Slack 구현체 (Webhook)
```

### 채널 라우팅

`NotificationDispatcher`가 요청의 채널 정보를 보고 적절한 구현체로 라우팅합니다.
새로운 채널을 추가할 때 `NotificationSender` 인터페이스를 구현하면 Dispatcher 수정 없이 자동으로 등록됩니다.

```
NotificationEngine
        |
NotificationDispatcher
        |
        +-- SseNotificationSender
        +-- EmailNotificationSender
        +-- SlackNotificationSender
```

---

## 멀티 서버 환경

SSE 채널은 Redis Pub/Sub을 사용하여 멀티 서버 환경을 지원합니다.
서버 A에 연결된 클라이언트에게 서버 B에서도 알림을 발송할 수 있습니다.

```
서버 B에서 알림 발송 요청
        |
Redis Pub/Sub 발행
        |
서버 A가 메시지 수신
        |
서버 A의 SseEmitter로 클라이언트에 전달
```

---

## 발송 결과 처리

발송 결과는 `CompletableFuture<NotificationResult>`로 반환됩니다.
예외를 던지는 대신 결과 객체에 성공/실패 여부를 담아 반환하므로 알림 실패가 비즈니스 로직에 영향을 주지 않습니다.

```java
notificationEngine.send(request)
    .thenAccept(result -> {
        if (!result.isSuccess()) {
            log.warn("알림 발송 실패: {}", result.getErrorMessage());
        }
    });
```