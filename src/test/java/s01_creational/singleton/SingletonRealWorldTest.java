package s01_creational.singleton;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Singleton 패턴 - 실무 관점
 *
 * 1. Singleton의 문제점
 * 2. Spring에서의 Singleton
 * 3. 언제 써야 하나?
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SingletonRealWorldTest {

    // ===== 문제점 1: 전역 상태 =====
    static class GlobalCounter {
        private static final GlobalCounter INSTANCE = new GlobalCounter();
        private int count = 0;

        private GlobalCounter() {}

        public static GlobalCounter getInstance() {
            return INSTANCE;
        }

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }

        public void reset() {
            count = 0;
        }
    }

    // ===== 문제점 2: 의존성 숨김 =====
    static class BadOrderService {
        // 의존성이 숨겨짐
        public void createOrder() {
            GlobalCounter.getInstance().increment(); // 어디서 의존하는지 안 보임
        }
    }

    // ===== 해결: 의존성 주입 =====
    interface Counter {
        void increment();

        int getCount();
    }

    static class InjectableCounter implements Counter {
        private int count = 0;

        @Override
        public void increment() {
            count++;
        }

        @Override
        public int getCount() {
            return count;
        }
    }

    static class GoodOrderService {
        private final Counter counter;

        // 의존성이 명시적
        public GoodOrderService(Counter counter) {
            this.counter = counter;
        }

        public void createOrder() {
            counter.increment();
        }
    }

    @Nested
    class Singleton의_문제점 {

        @Test
        void 전역_상태로_테스트가_오염된다() {
            GlobalCounter.getInstance().reset();

            // 테스트 1
            GlobalCounter.getInstance().increment();
            assertThat(GlobalCounter.getInstance().getCount()).isEqualTo(1);

            // 테스트 2 - 이전 상태가 남아있음
            // 다른 테스트에서 increment() 했으면 실패할 수 있음
            GlobalCounter.getInstance().increment();
            assertThat(GlobalCounter.getInstance().getCount()).isEqualTo(2);

            /*
             * 문제:
             * - 테스트 순서에 따라 결과가 달라짐
             * - 테스트 간 격리 안 됨
             * - 매번 reset() 해줘야 함
             */
        }

        @Test
        void 의존성이_숨겨져서_테스트하기_어렵다() {
            // BadOrderService가 GlobalCounter에 의존하는지 알 수 없음
            BadOrderService service = new BadOrderService();

            /*
             * 문제:
             * - Mock으로 대체 불가
             * - 의존성 파악이 어려움
             * - 단위 테스트 작성 어려움
             */
        }
    }

    @Nested
    class 의존성_주입으로_해결 {

        @Test
        void 의존성을_주입하면_테스트하기_쉽다() {
            // Mock 또는 테스트용 인스턴스 주입
            Counter mockCounter = new InjectableCounter();
            GoodOrderService service = new GoodOrderService(mockCounter);

            service.createOrder();
            service.createOrder();

            assertThat(mockCounter.getCount()).isEqualTo(2);
        }

        @Test
        void 테스트마다_새_인스턴스로_격리된다() {
            Counter counter1 = new InjectableCounter();
            Counter counter2 = new InjectableCounter();

            GoodOrderService service1 = new GoodOrderService(counter1);
            GoodOrderService service2 = new GoodOrderService(counter2);

            service1.createOrder();
            service1.createOrder();
            service2.createOrder();

            // 서로 영향 없음
            assertThat(counter1.getCount()).isEqualTo(2);
            assertThat(counter2.getCount()).isEqualTo(1);
        }
    }
}
