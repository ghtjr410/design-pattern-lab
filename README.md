# Design Pattern Lab

> GOF 디자인 패턴 23개 + 실무 패턴을 학습 테스트로 마스터하는 저장소

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![JUnit Version](https://img.shields.io/badge/JUnit-5.10-green.svg)](https://junit.org/junit5/)

## 소개

이 저장소는 **디자인 패턴의 본질**을 학습 테스트를 통해 이해합니다.

단순 암기가 아닌, **왜 이 패턴이 필요한지**, **언제 써야 하는지**, **Spring에서 어떻게 활용되는지**를 코드로 검증합니다.

```
"패턴을 아는 것과 패턴을 쓸 줄 아는 것은 다르다"
```

## 학습 목표

- GOF 23개 패턴의 **핵심 의도**와 **구조** 이해
- 각 패턴의 **실무 적용 사례** 체득
- **안티패턴 → 패턴 적용**의 리팩토링 과정 경험
- Spring 프레임워크에서 **패턴이 어떻게 활용되는지** 파악
- 유사한 패턴들의 **차이점** 명확히 구분

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 21 |
| Test Framework | JUnit 5 |
| Assertion | AssertJ |
| Build Tool | Gradle |
| Framework | Spring Boot 3.x (일부 테스트) |

## 프로젝트 구조

```
src/test/java/
├── s01_creational/      # 생성 패턴 (5개)
├── s02_structural/      # 구조 패턴 (7개)
├── s03_behavioral/      # 행위 패턴 (11개)
└── s04_practical/       # 실무 패턴 (8개)
```

## 학습 내용

### Part 1: 생성 패턴 (Creational Patterns)

> 객체 생성 메커니즘을 다루는 패턴. 상황에 맞는 방식으로 객체를 생성한다.

<details>
<summary><b>Factory Method</b> - 객체 생성을 서브클래스에 위임</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `FactoryMethodBasicTest` | 패턴의 핵심 구조, Creator와 Product |
| `FactoryMethodRealWorldTest` | 알림 생성 (SMS, Email, Push) |
| `FactoryMethodSpringTest` | BeanFactory 동작 이해 |

**핵심 질문**
- Factory Method와 단순 팩토리의 차이는?
- 새로운 제품 타입 추가 시 OCP를 어떻게 지키는가?
- Spring의 BeanFactory가 Factory Method 패턴인 이유는?

</details>

<details>
<summary><b>Abstract Factory</b> - 관련 객체 군을 생성</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `AbstractFactoryBasicTest` | 패턴의 핵심 구조, 제품군 개념 |
| `AbstractFactoryRealWorldTest` | UI 테마 (Light/Dark 컴포넌트 군) |
| `AbstractFactoryCompareTest` | Factory Method와 비교 |

**핵심 질문**
- Abstract Factory와 Factory Method의 차이는?
- 제품군 간의 일관성을 어떻게 보장하는가?
- 새로운 제품군 추가 vs 새로운 제품 타입 추가의 차이는?

</details>

<details>
<summary><b>Builder</b> - 복잡한 객체의 생성 과정을 분리</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `BuilderGofTest` | GOF 원형 구조, Director 역할 |
| `BuilderFluentTest` | 모던 Fluent API, 생성자 오버로딩 비교 |
| `BuilderLombokTest` | @Builder 활용 |
| `BuilderTestFixtureTest` | 테스트 데이터 생성 |

**핵심 질문**
- Builder 패턴이 생성자 오버로딩보다 나은 점은?
- Lombok @Builder와 GOF Builder의 차이는?
- 테스트 Fixture에 Builder를 활용하면 좋은 이유는?

</details>

<details>
<summary><b>Singleton</b> - 인스턴스를 하나로 제한</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `SingletonBasicTest` | 패턴의 핵심 구조 |
| `SingletonThreadSafeTest` | DCL, Holder, Enum 방식 |
| `SingletonRealWorldTest` | 실무 문제점, DI 대안, 실무 권장사항 |

**핵심 질문**
- 왜 Enum이 가장 안전한 Singleton 구현인가?
- Double-Checked Locking의 문제점은?
- Spring의 싱글톤 빈과 GOF Singleton의 차이는?

</details>

<details>
<summary><b>Prototype</b> - 기존 객체를 복제하여 생성</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `PrototypeBasicTest` | 패턴의 핵심 구조, Cloneable |
| `PrototypeShallowDeepTest` | 얕은 복사 vs 깊은 복사 |
| `PrototypeRegistryTest` | 프로토타입 레지스트리 |

**핵심 질문**
- Prototype 패턴이 new 연산자보다 나은 경우는?
- 얕은 복사의 위험성은?
- Java의 clone()이 권장되지 않는 이유는?

</details>

---

### Part 2: 구조 패턴 (Structural Patterns)

> 클래스나 객체를 조합하여 더 큰 구조를 만드는 패턴.

<details>
<summary><b>Adapter</b> - 호환되지 않는 인터페이스를 연결</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `AdapterBasicTest` | 패턴의 핵심 구조 |
| `AdapterClassVsObjectTest` | 클래스 어댑터 vs 객체 어댑터 |
| `AdapterRealWorldTest` | 레거시 API 통합, 외부 라이브러리 래핑 |

**핵심 질문**
- 클래스 어댑터와 객체 어댑터의 차이는?
- 레거시 시스템 통합 시 Adapter를 사용하는 이유는?
- Adapter와 Facade의 차이는?

</details>

<details>
<summary><b>Bridge</b> - 추상화와 구현을 분리</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `BridgeBasicTest` | 패턴의 핵심 구조, 분리의 의미 |
| `BridgeRealWorldTest` | 결제(Visa/Master) × 기기(Mobile/Web) |
| `BridgeVsStrategyTest` | Strategy 패턴과 비교 |

**핵심 질문**
- Bridge가 해결하는 "다차원 확장" 문제란?
- Bridge와 Strategy의 차이는?
- 언제 Bridge 패턴을 고려해야 하는가?

</details>

<details>
<summary><b>Composite</b> - 트리 구조로 부분-전체 계층을 표현</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `CompositeBasicTest` | 패턴의 핵심 구조, Component 인터페이스 |
| `CompositeRealWorldTest` | 조직도, 메뉴, 카테고리 트리 |
| `CompositeTraversalTest` | 순회 방법들 |

**핵심 질문**
- Composite 패턴이 재귀적 구조에 적합한 이유는?
- Leaf와 Composite를 동일하게 다루는 것의 장점은?
- 파일 시스템이 Composite 패턴인 이유는?

</details>

<details>
<summary><b>Decorator</b> - 객체에 동적으로 책임을 추가</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `DecoratorBasicTest` | 패턴의 핵심 구조, 래핑 개념 |
| `DecoratorRealWorldTest` | 음료 옵션 추가, 기능 래핑 |
| `DecoratorJavaIOTest` | InputStream 체인 분석 |
| `DecoratorVsProxyTest` | Proxy 패턴과 비교 |

**핵심 질문**
- Decorator가 상속보다 유연한 이유는?
- Java I/O가 Decorator 패턴인 이유는?
- Decorator와 Proxy의 차이는?

</details>

<details>
<summary><b>Facade</b> - 복잡한 서브시스템에 단순한 인터페이스 제공</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `FacadeBasicTest` | 패턴의 핵심 구조 |
| `FacadeRealWorldTest` | 주문 처리 (재고, 결제, 배송 통합) |
| `FacadeRefactoringTest` | 복잡한 코드 → Facade 리팩토링 |

**핵심 질문**
- Facade가 "최소 지식 원칙"과 연관되는 이유는?
- Service Layer가 Facade인 이유는?
- Facade와 Adapter의 차이는?

</details>

<details>
<summary><b>Flyweight</b> - 공유를 통해 메모리 사용을 최적화</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `FlyweightBasicTest` | 패턴의 핵심 구조, 내재/외재 상태 |
| `FlyweightRealWorldTest` | 문자 렌더링, 게임 파티클 |
| `FlyweightStringPoolTest` | String Pool이 Flyweight인 이유 |
| `FlyweightMemoryTest` | 메모리 사용량 비교 |

**핵심 질문**
- 내재 상태(Intrinsic)와 외재 상태(Extrinsic)의 구분 기준은?
- String Pool이 Flyweight 패턴인 이유는?
- Flyweight 적용 시 고려할 트레이드오프는?

</details>

<details>
<summary><b>Proxy</b> - 다른 객체에 대한 접근을 제어</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `ProxyBasicTest` | 패턴의 핵심 구조 |
| `ProxyVirtualTest` | 지연 로딩 (Lazy Loading) |
| `ProxyProtectionTest` | 접근 제어 |
| `ProxyCachingTest` | 캐싱 프록시 |
| `ProxyJdkDynamicTest` | JDK Dynamic Proxy |
| `ProxyCglibTest` | CGLIB Proxy |
| `ProxySpringAopTest` | Spring AOP가 Proxy인 이유 |

**핵심 질문**
- Proxy의 종류(Virtual, Protection, Remote)와 각각의 용도는?
- JDK Dynamic Proxy와 CGLIB의 차이는?
- Spring AOP가 Proxy 기반인 이유와 한계는?

</details>

---

### Part 3: 행위 패턴 (Behavioral Patterns)

> 객체 간의 상호작용과 책임 분배를 다루는 패턴.

<details>
<summary><b>Strategy</b> - 알고리즘을 캡슐화하여 교체 가능하게</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `StrategyBasicTest` | 패턴의 핵심 구조 |
| `StrategyRealWorldTest` | 결제 방식, 할인 정책 |
| `StrategyLambdaTest` | 람다로 간결하게 |
| `StrategySpringTest` | Spring DI와 결합 |
| `StrategyAntiPatternTest` | if-else 지옥 vs Strategy |

**핵심 질문**
- Strategy 패턴이 if-else 분기보다 나은 이유는?
- 람다를 활용한 Strategy 구현의 장단점은?
- Spring에서 Strategy를 DI하는 방법은?

</details>

<details>
<summary><b>Template Method</b> - 알고리즘 골격을 정의하고 일부를 서브클래스에서 구현</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `TemplateMethodBasicTest` | 패턴의 핵심 구조 |
| `TemplateMethodRealWorldTest` | 데이터 파싱, 주문 처리 흐름 |
| `TemplateMethodHookTest` | Hook 메서드 활용 |
| `TemplateMethodVsStrategyTest` | Strategy와 비교 (상속 vs 조합) |

**핵심 질문**
- Template Method가 "헐리우드 원칙"과 연관되는 이유는?
- Hook 메서드의 역할은?
- Template Method와 Strategy의 선택 기준은?

</details>

<details>
<summary><b>Observer</b> - 상태 변화를 다른 객체에 통지</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `ObserverBasicTest` | 패턴의 핵심 구조, Subject/Observer |
| `ObserverRealWorldTest` | 주문 상태 변경 알림 |
| `ObserverJavaBuiltInTest` | PropertyChangeListener |
| `ObserverSpringEventTest` | ApplicationEvent 활용 |

**핵심 질문**
- Observer 패턴이 느슨한 결합을 만드는 방법은?
- Push 방식과 Pull 방식의 차이는?
- Spring Event가 Observer 패턴인 이유는?

</details>

<details>
<summary><b>State</b> - 상태에 따라 행동을 변경</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `StateBasicTest` | 패턴의 핵심 구조 |
| `StateRealWorldTest` | 주문 상태 머신 |
| `StateTransitionTest` | 상태 전이 규칙 |
| `StateVsStrategyTest` | Strategy와 비교 |

**핵심 질문**
- State 패턴이 복잡한 조건문을 제거하는 방법은?
- 상태 전이 로직은 어디에 두어야 하는가?
- State와 Strategy의 차이는?

</details>

<details>
<summary><b>Chain of Responsibility</b> - 요청을 처리할 객체를 체인으로 연결</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `ChainBasicTest` | 패턴의 핵심 구조 |
| `ChainRealWorldTest` | 승인 프로세스, 할인 체인 |
| `ChainServletFilterTest` | Servlet Filter가 Chain인 이유 |
| `ChainSpringInterceptorTest` | Spring Interceptor |

**핵심 질문**
- Chain of Responsibility가 요청 발신자와 수신자를 분리하는 방법은?
- Servlet Filter가 이 패턴인 이유는?
- 체인의 순서가 중요한 이유는?

</details>

<details>
<summary><b>Command</b> - 요청을 객체로 캡슐화</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `CommandBasicTest` | 패턴의 핵심 구조 |
| `CommandRealWorldTest` | 주문 실행/취소 |
| `CommandUndoRedoTest` | Undo/Redo 구현 |
| `CommandQueueTest` | 명령 큐잉 |
| `CommandTransactionTest` | 트랜잭션 스크립트 |

**핵심 질문**
- Command가 요청을 객체화하면 얻는 이점은?
- Undo/Redo를 구현하는 방법은?
- Command와 Strategy의 차이는?

</details>

<details>
<summary><b>Iterator</b> - 컬렉션 요소를 순차적으로 접근</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `IteratorBasicTest` | 패턴의 핵심 구조 |
| `IteratorCustomTest` | 커스텀 Iterator 구현 |
| `IteratorJavaBuiltInTest` | Iterable, Iterator 인터페이스 |
| `IteratorVsStreamTest` | Stream과 비교 |

**핵심 질문**
- Iterator가 컬렉션의 내부 구조를 숨기는 방법은?
- for-each 문이 Iterator를 사용하는 원리는?
- Iterator와 Stream의 차이는?

</details>

<details>
<summary><b>Mediator</b> - 객체 간 통신을 캡슐화</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `MediatorBasicTest` | 패턴의 핵심 구조 |
| `MediatorRealWorldTest` | 채팅방, 항공 관제탑 |
| `MediatorVsObserverTest` | Observer와 비교 |

**핵심 질문**
- Mediator가 복잡한 의존 관계를 단순화하는 방법은?
- Mediator와 Observer의 차이는?
- Mediator가 God Object가 되지 않으려면?

</details>

<details>
<summary><b>Memento</b> - 객체 상태를 저장하고 복원</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `MementoBasicTest` | 패턴의 핵심 구조, Originator/Memento/Caretaker |
| `MementoRealWorldTest` | 에디터 상태 저장 |
| `MementoUndoStackTest` | Undo 스택 구현 |

**핵심 질문**
- Memento가 캡슐화를 위반하지 않고 상태를 저장하는 방법은?
- Memento와 Command 패턴의 Undo 구현 차이는?
- 메모리 사용량 관리 방법은?

</details>

<details>
<summary><b>Visitor</b> - 객체 구조와 연산을 분리</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `VisitorBasicTest` | 패턴의 핵심 구조 |
| `VisitorRealWorldTest` | 파일 시스템 순회, 보고서 생성 |
| `VisitorDoubleDispatchTest` | 더블 디스패치 원리 |

**핵심 질문**
- Visitor가 OCP를 지키면서 연산을 추가하는 방법은?
- 더블 디스패치(Double Dispatch)란?
- Visitor 패턴의 단점과 적용 시 고려사항은?

</details>

<details>
<summary><b>Interpreter</b> - 언어의 문법을 정의하고 해석</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `InterpreterBasicTest` | 패턴의 핵심 구조, Expression 트리 |
| `InterpreterRealWorldTest` | 수식 계산기, 간단한 DSL |
| `InterpreterAstTest` | AST 구조 |

**핵심 질문**
- Interpreter 패턴이 적합한 상황은?
- AST(Abstract Syntax Tree)의 역할은?
- 복잡한 언어에 Interpreter 패턴이 적합하지 않은 이유는?

</details>

---

### Part 4: 실무 패턴 (Practical Patterns)

> GOF 외 실무에서 자주 사용되는 패턴들.

<details>
<summary><b>Repository</b> - 데이터 접근을 추상화</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `RepositoryBasicTest` | 패턴의 핵심 구조 |
| `RepositoryVsDaoTest` | DAO와 차이점 |
| `RepositorySpringDataTest` | Spring Data JPA |

**핵심 질문**
- Repository와 DAO의 차이는?
- Repository가 도메인 레이어에 속하는 이유는?
- Spring Data JPA가 Repository 패턴을 구현하는 방법은?

</details>

<details>
<summary><b>Specification</b> - 비즈니스 규칙을 캡슐화</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `SpecificationBasicTest` | 패턴의 핵심 구조 |
| `SpecificationCompositeTest` | AND, OR, NOT 조합 |
| `SpecificationJpaTest` | JPA Specification 활용 |

**핵심 질문**
- Specification이 복잡한 쿼리 조건을 단순화하는 방법은?
- Specification 조합의 장점은?
- JPA Specification과 QueryDSL 비교?

</details>

<details>
<summary><b>Null Object</b> - null 대신 기본 행동을 하는 객체</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `NullObjectBasicTest` | 패턴의 핵심 구조 |
| `NullObjectRealWorldTest` | 기본 정책, 빈 컬렉션 |
| `NullObjectVsOptionalTest` | Optional과 비교 |

**핵심 질문**
- Null Object가 null 체크를 제거하는 방법은?
- Null Object와 Optional의 사용 시점 차이는?
- Collections.emptyList()가 Null Object인 이유는?

</details>

<details>
<summary><b>Circuit Breaker</b> - 장애를 격리하여 시스템 보호</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `CircuitBreakerBasicTest` | 패턴의 핵심 구조 |
| `CircuitBreakerStateTest` | Closed → Open → Half-Open |
| `CircuitBreakerResilience4jTest` | Resilience4j 활용 |

**핵심 질문**
- Circuit Breaker의 3가지 상태와 전이 조건은?
- 장애 격리가 시스템 안정성에 미치는 영향은?
- Resilience4j에서 Circuit Breaker 설정 방법은?

</details>

<details>
<summary><b>Retry</b> - 실패한 작업을 재시도</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `RetryBasicTest` | 패턴의 핵심 구조 |
| `RetryBackoffTest` | Exponential Backoff |
| `RetrySpringTest` | @Retryable 활용 |

**핵심 질문**
- 재시도가 적합한 실패 유형은?
- Exponential Backoff가 필요한 이유는?
- 재시도 횟수와 간격 설정 기준은?

</details>

<details>
<summary><b>Domain Event</b> - 도메인에서 발생한 사건을 표현</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `DomainEventBasicTest` | 패턴의 핵심 구조 |
| `DomainEventPublishTest` | 이벤트 발행 시점 |
| `DomainEventSpringTest` | ApplicationEventPublisher |

**핵심 질문**
- Domain Event가 도메인 간 결합을 줄이는 방법은?
- 이벤트 발행 시점 (트랜잭션 내 vs 외)의 차이는?
- Spring Event와 메시지 큐의 차이는?

</details>

<details>
<summary><b>Value Object</b> - 불변 값 객체</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `ValueObjectBasicTest` | 패턴의 핵심 구조 |
| `ValueObjectEqualsTest` | 동등성 비교 |
| `ValueObjectImmutabilityTest` | 불변성 보장 |
| `ValueObjectRecordTest` | Java Record 활용 |

**핵심 질문**
- Value Object와 Entity의 차이는?
- Value Object가 불변이어야 하는 이유는?
- Java Record가 Value Object에 적합한 이유는?

</details>

<details>
<summary><b>Object Pool</b> - 객체를 재사용하여 생성 비용 절감</summary>

| 테스트 | 학습 내용 |
|--------|-----------|
| `ObjectPoolBasicTest` | 패턴의 핵심 구조 |
| `ObjectPoolRealWorldTest` | DB 커넥션 풀, 스레드 풀 |
| `ObjectPoolHikariTest` | HikariCP가 Object Pool인 이유 |

**핵심 질문**
- Object Pool이 효과적인 상황은?
- 커넥션 풀 크기 설정의 기준은?
- HikariCP가 빠른 이유는?

</details>

---

## 학습 테스트 작성 원칙

### 1. 테스트 구조

```java
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StrategyPatternTest {

    @Nested
    class 결제_전략_패턴 {

        @Test
        void 카드_결제_전략() {
            PaymentStrategy strategy = new CardPaymentStrategy("1234-5678");
            
            int result = strategy.pay(10000);
            
            assertThat(result).isEqualTo(10000);
        }

        @Test
        void 전략은_런타임에_교체_가능하다() {
            PaymentContext context = new PaymentContext();
            
            context.setStrategy(new CardPaymentStrategy("1234-5678"));
            assertThat(context.executePayment(10000)).isEqualTo(10000);
            
            context.setStrategy(new CashPaymentStrategy());
            assertThat(context.executePayment(10000)).isEqualTo(9500); // 현금 할인
        }
    }
}
```

### 2. 원칙

| 원칙 | 설명 |
|------|------|
| **@DisplayNameGeneration** | 언더스코어를 공백으로 자동 변환 |
| **한글 메서드명** | `전략은_런타임에_교체_가능하다()` |
| **@Nested** | 패턴별, 시나리오별 그룹핑 |
| **given/when/then** | 구조는 유지하되 주석 생략 |

### 3. 각 패턴별 테스트 구성

```
XxxPattern/
├── XxxBasicTest.java           # 패턴의 핵심 구조 이해
├── XxxRealWorldTest.java       # 실무 적용 예제
├── XxxSpringTest.java          # Spring에서의 활용 (해당 시)
├── XxxVsYyyTest.java           # 유사 패턴과 비교 (해당 시)
└── XxxAntiPatternTest.java     # 안티패턴 → 패턴 적용 (해당 시)
```

---

## 실행 방법

```bash
# 전체 테스트 실행
./gradlew test

# 특정 패턴 분류만 실행
./gradlew test --tests "*.s01_creational.*"
./gradlew test --tests "*.s02_structural.*"
./gradlew test --tests "*.s03_behavioral.*"
./gradlew test --tests "*.s04_practical.*"

# 특정 패턴만 실행
./gradlew test --tests "*.strategy.*"

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

---

## 참고 자료

- [Design Patterns: Elements of Reusable Object-Oriented Software (GOF)](https://www.oreilly.com/library/view/design-patterns-elements/0201633612/)
- [Head First Design Patterns](https://www.oreilly.com/library/view/head-first-design/9781492077992/)
- [Refactoring Guru - Design Patterns](https://refactoring.guru/design-patterns)
- [Patterns of Enterprise Application Architecture](https://www.martinfowler.com/books/eaa.html)

---

<div align="center">

**"패턴은 목적이 아니라 도구다"**

*— 패턴을 위한 패턴이 아닌, 문제 해결을 위한 패턴을 익히자*

</div>