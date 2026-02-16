package s01_creational.singleton;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Singleton 패턴 - 기본 개념
 *
 * 핵심 의도: 인스턴스가 하나만 존재하도록 보장하고, 전역 접근점을 제공한다.
 *
 * 학습 순서:
 * 1. 왜 필요한가? (문제 상황)
 * 2. GoF 원형 구현
 * 3. Lazy 초기화와 스레드 안전 문제
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SingletonBasicTest {

    // ===== 문제 상황: 매번 새로 만들면? =====
    static class ExpensiveConnection {
        private static int createCount = 0;

        ExpensiveConnection() {
            createCount++;
            // 실제로는 네트워크 연결, 리소스 할당 등 비용이 큼
            try {
                Thread.sleep(10); // 생성 비용 시뮬레이션
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        static int getCreateCount() {
            return createCount;
        }

        static void resetCount() {
            createCount = 0;
        }
    }

    // ===== GoF 원형: Eager Initialization =====
    static class EagerSingleton {
        private static final EagerSingleton INSTANCE = new EagerSingleton();

        private EagerSingleton() {}

        public static EagerSingleton getInstance() {
            return INSTANCE;
        }
    }

    // ===== Lazy Initialization (스레드 안전하지 않음) =====
    static class LazySingletonUnsafe {
        private static LazySingletonUnsafe instance;
        private final String id;

        private LazySingletonUnsafe() {
            this.id = "instance-" + System.nanoTime();
        }

        public static LazySingletonUnsafe getInstance() {
            if (instance == null) { // ← 여러 스레드가 동시에 통과 가능!
                instance = new LazySingletonUnsafe();
            }
            return instance;
        }

        public String getId() {
            return id;
        }

        static void reset() {
            instance = null;
        }
    }

    // ===== 테스트 =====

    @Nested
    class 왜_필요한가 {

        @Test
        void Before_매번_새로_만들면_비용이_크다() {
            ExpensiveConnection.resetCount();

            // 여러 곳에서 커넥션 생성
            new ExpensiveConnection();
            new ExpensiveConnection();
            new ExpensiveConnection();

            // 3번 생성 = 3배 비용
            assertThat(ExpensiveConnection.getCreateCount()).isEqualTo(3);
        }

        @Test
        void After_하나만_만들어_공유하면_효율적이다() {
            // Singleton은 한 번만 생성
            EagerSingleton instance1 = EagerSingleton.getInstance();
            EagerSingleton instance2 = EagerSingleton.getInstance();
            EagerSingleton instance3 = EagerSingleton.getInstance();

            // 모두 같은 인스턴스
            assertThat(instance1).isSameAs(instance2);
            assertThat(instance2).isSameAs(instance3);
        }
    }

    @Nested
    class GoF_원형 {

        @Test
        void Eager_Singleton_구조() {
            /*
             * GoF Singleton 핵심 3가지:
             * 1. private static final INSTANCE - 유일한 인스턴스
             * 2. private 생성자 - 외부 생성 차단
             * 3. public static getInstance() - 접근점
             */
            EagerSingleton instance = EagerSingleton.getInstance();

            assertThat(instance).isNotNull();
        }

        @Test
        void 몇_번을_호출해도_같은_인스턴스다() {
            EagerSingleton first = EagerSingleton.getInstance();
            EagerSingleton second = EagerSingleton.getInstance();

            assertThat(first).isSameAs(second);
        }

        @Test
        void Eager는_클래스_로딩_시점에_생성된다() {
            /*
             * Eager Initialization:
             * - 클래스 로딩 시 바로 생성
             * - 장점: 간단, 스레드 안전
             * - 단점: 안 써도 생성됨
             */
            EagerSingleton instance = EagerSingleton.getInstance();
            assertThat(instance).isNotNull();
        }
    }

    @Nested
    class Lazy_초기화와_스레드_문제 {

        @Test
        void Lazy_초기화는_첫_호출_시_생성된다() {
            LazySingletonUnsafe.reset();

            /*
             * Lazy Initialization:
             * - 첫 getInstance() 호출 시 생성
             * - 장점: 안 쓰면 안 만듦
             * - 단점: 스레드 안전 문제!
             */
            LazySingletonUnsafe instance = LazySingletonUnsafe.getInstance();
            assertThat(instance).isNotNull();
        }

        @Test
        void 단일_스레드에서는_문제없다() {
            LazySingletonUnsafe.reset();

            LazySingletonUnsafe instance1 = LazySingletonUnsafe.getInstance();
            LazySingletonUnsafe instance2 = LazySingletonUnsafe.getInstance();

            assertThat(instance1).isSameAs(instance2);
        }

        @Test
        void 멀티스레드에서_여러_인스턴스가_생성될_수_있다() throws InterruptedException {
            LazySingletonUnsafe.reset();

            int threadCount = 100;
            Set<String> instanceIds = new HashSet<>();
            CountDownLatch ready = new CountDownLatch(threadCount);
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(threadCount);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        ready.countDown();
                        start.await(); // 모든 스레드가 동시에 시작

                        LazySingletonUnsafe instance = LazySingletonUnsafe.getInstance();
                        synchronized (instanceIds) {
                            instanceIds.add(instance.getId());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }

            ready.await(); // 모든 스레드 준비 대기
            start.countDown(); // 동시 시작
            done.await(); // 완료 대기
            executor.shutdown();

            /*
             * Race Condition:
             * 스레드 A: if (instance == null) ← true, 아직 안 만들어짐
             * 스레드 B: if (instance == null) ← true, 아직 안 만들어짐 (동시 통과!)
             * 스레드 A: instance = new ... ← 인스턴스 1 생성
             * 스레드 B: instance = new ... ← 인스턴스 2 생성 (덮어씀!)
             */
            System.out.println("생성된 인스턴스 수: " + instanceIds.size());

            // 1개여야 정상이지만, Race Condition으로 여러 개 생성될 수 있음
            // (항상 실패하진 않음 - 비결정적)
            if (instanceIds.size() > 1) {
                System.out.println("Race Condition 발생! 인스턴스가 여러 개 생성됨");
            }

            assertThat(instanceIds).isNotEmpty();
        }
    }
}
