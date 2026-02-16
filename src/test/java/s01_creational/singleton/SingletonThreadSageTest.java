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
