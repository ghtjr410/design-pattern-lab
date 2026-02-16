package s01_creational.singleton;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Singleton 패턴 - 스레드 안전 구현 방법들
 *
 * Lazy 초기화의 스레드 문제 해결 방법:
 * 1. synchronized 메서드 - 간단하지만 느림
 * 2. DCL (Double-Checked Locking) - 복잡하지만 빠름
 * 3. Holder 패턴 - 권장 (Lazy + Thread-safe)
 * 4. Enum - 가장 안전 (Joshua Bloch 권장)
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SingletonThreadSageTest {

    // ===== 방법 1: synchronized =====
    static class SynchronizedSingleton {
        private static SynchronizedSingleton instance;
        private final String id = "sync-" + System.nanoTime();

        private SynchronizedSingleton() {}

        // 전체 메서드 동기화 - 간단하지만 매번 lock
        public static synchronized SynchronizedSingleton getInstance() {
            if (instance == null) {
                instance = new SynchronizedSingleton();
            }
            return instance;
        }

        public String getId() {
            return id;
        }
    }

    // ===== 방법 2: DCL (Double-Checked Locking) =====
    static class DclSingleton {
        // volatile: 스레드 간 가시성 보장
        private static volatile DclSingleton instance;
        private final String id = "dcl-" + System.nanoTime();

        private DclSingleton() {}

        public static DclSingleton getInstance() {
            if (instance == null) { // 1차 체크 (lock 없이)
                synchronized (DclSingleton.class) {
                    if (instance == null) { // 2차 체크 (lock 안에서)
                        instance = new DclSingleton();
                    }
                }
            }
            return instance;
        }

        public String getId() {
            return id;
        }
    }

    // ===== 방법 3: Holder 패턴 (권장) =====
    static class HolderSingleton {
        private final String id = "holder-" + System.nanoTime();

        private HolderSingleton() {}

        // 내부 클래스는 getInstance() 호출 시점에 로딩됨
        private static class Holder {
            private static final HolderSingleton INSTANCE = new HolderSingleton();
        }

        public static HolderSingleton getInstance() {
            return Holder.INSTANCE;
        }

        public String getId() {
            return id;
        }
    }

    // ===== 방법 4: Enum (가장 안전) =====
    enum EnumSingleton {
        INSTANCE;

        private final String id = "enum-" + System.nanoTime();

        public String getId() {
            return id;
        }

        public void doSomething() {
            // 비즈니스 로직
        }
    }

    @Nested
    class Synchronized {

        @Test
        void synchronized로_스레드_안전_확보() {
            /*
             * synchronized 방식:
             * - 메서드 전체에 lock
             * - 장점: 구현 간단
             * - 단점: 매 호출마다 동기화 비용
             */
            SynchronizedSingleton instance = SynchronizedSingleton.getInstance();
            assertThat(instance).isNotNull();
        }

        @Test
        void 멀티스레드에서_안전하다() throws InterruptedException {
            Set<String> ids =
                    runConcurrentTest(() -> SynchronizedSingleton.getInstance().getId());

            assertThat(ids).hasSize(1); // 인스턴스 1개만 생성됨
        }
    }

    @Nested
    class DCL_Double_Checked_Locking {

        @Test
        void DCL로_성능과_안전성_확보() {
            /*
             * DCL 방식:
             * - 1차 체크: lock 없이 빠르게
             * - 2차 체크: lock 안에서 확실하게
             * - volatile: 스레드 간 가시성 보장
             *
             * 장점: 인스턴스 생성 후에는 lock 안 걸림
             * 단점: 구현 복잡, volatile 필수
             */
            DclSingleton instance = DclSingleton.getInstance();
            assertThat(instance).isNotNull();
        }

        @Test
        void 멀티스레드에서_안전하다() throws InterruptedException {
            Set<String> ids = runConcurrentTest(() -> DclSingleton.getInstance().getId());

            assertThat(ids).hasSize(1);
        }

        @Test
        void volatile_없으면_문제가_생길_수_있다() {
            /*
             * volatile이 필요한 이유:
             *
             * instance = new DclSingleton() 은 실제로 3단계:
             * 1. 메모리 할당
             * 2. 생성자 실행
             * 3. instance 변수에 참조 할당
             *
             * JVM이 재정렬하면 1 → 3 → 2 순서가 될 수 있음
             * 다른 스레드가 3 이후, 2 이전에 접근하면 미완성 객체 사용!
             *
             * volatile은 이 재정렬을 막음
             */
            assertThat(true).isTrue();
        }
    }

    @Nested
    class Holder_패턴_권장 {

        @Test
        void Holder_패턴으로_Lazy하면서_안전하게() {
            /*
             * Holder 패턴:
             * - 내부 클래스(Holder)는 getInstance() 호출 전까지 로딩 안 됨
             * - 클래스 로딩은 JVM이 스레드 안전하게 처리
             *
             * 장점:
             * - Lazy 초기화
             * - 스레드 안전 (JVM이 보장)
             * - synchronized, volatile 불필요
             * - 구현 간단
             */
            HolderSingleton instance = HolderSingleton.getInstance();
            assertThat(instance).isNotNull();
        }

        @Test
        void 멀티스레드에서_안전하다() throws InterruptedException {
            Set<String> ids =
                    runConcurrentTest(() -> HolderSingleton.getInstance().getId());

            assertThat(ids).hasSize(1);
        }
    }

    @Nested
    class Enum_가장_안전 {

        @Test
        void Enum으로_가장_안전하게() {
            /*
             * Enum Singleton (Joshua Bloch - Effective Java):
             *
             * 장점:
             * - 직렬화 안전 (자동)
             * - 리플렉션 공격 방어 (JVM이 차단)
             * - 스레드 안전 (JVM이 보장)
             * - 구현 가장 간단
             *
             * 단점:
             * - Lazy 초기화 불가
             * - 상속 불가
             */
            EnumSingleton instance = EnumSingleton.INSTANCE;
            instance.doSomething();

            assertThat(instance.getId()).isNotNull();
        }

        @Test
        void 멀티스레드에서_안전하다() throws InterruptedException {
            Set<String> ids = runConcurrentTest(() -> EnumSingleton.INSTANCE.getId());

            assertThat(ids).hasSize(1);
        }
    }

    // ===== Helper =====
    private Set<String> runConcurrentTest(Supplier<String> idSupplier) throws InterruptedException {
        int threadCount = 100;
        Set<String> ids = new HashSet<>();
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    ready.countDown();
                    start.await();
                    String id = idSupplier.get();
                    synchronized (ids) {
                        ids.add(id);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();

        return ids;
    }
}
