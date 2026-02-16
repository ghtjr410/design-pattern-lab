package s01_creational.singleton;

import static org.assertj.core.api.Assertions.assertThat;

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
}
