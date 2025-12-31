package s01_creational.factory_method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Factory Method 패턴 - Spring 연관성 학습 테스트
 *
 * Spring의 BeanFactory가 Factory Method 패턴을 활용하는 방식을 이해한다.
 *
 * 핵심 포인트:
 * 1. BeanFactory는 getBean()이라는 Factory Method를 제공
 * 2. 실제 Bean 생성 방식은 구현체(ApplicationContext 등)가 결정
 * 3. @Bean 메서드 자체가 Factory Method의 역할
 * 4. FactoryBean<T>를 통한 복잡한 객체 생성 위임
 *
 * 이 테스트에서는 Spring 의존성 없이 핵심 개념을 시뮬레이션한다.
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FactoryMethodSpringTest {

    // ===== Spring BeanFactory 시뮬레이션 =====

    /**
     * BeanFactory 인터페이스 시뮬레이션
     * Spring의 핵심: getBean()이 Factory Method 역할
     */
    interface BeanFactory {
        Object getBean(String name);

        <T> T getBean(String name, Class<T> requiredType);

        <T> T getBean(Class<T> requiredType);

        boolean containsBean(String name);
    }

    /**
     * BeanDefinition 시뮬레이션
     * Bean을 어떻게 생성할지에 대한 메타데이터
     */
    record BeanDefinition(Class<?> beanClass, Scope scope, Supplier<?> factoryMethod) {
        enum Scope {
            SINGLETON,
            PROTOTYPE
        }

        static BeanDefinition singleton(Class<?> beanClass) {
            return new BeanDefinition(beanClass, Scope.SINGLETON, null);
        }

        static BeanDefinition prototype(Class<?> beanClass) {
            return new BeanDefinition(beanClass, Scope.PROTOTYPE, null);
        }

        static <T> BeanDefinition withFactoryMethod(Class<T> beanClass, Supplier<T> factory) {
            return new BeanDefinition(beanClass, Scope.SINGLETON, factory);
        }
    }

    /**
     * DefaultListableBeanFactory 시뮬레이션
     * BeanFactory의 구체적인 구현
     */
    static class SimpleBeanFactory implements BeanFactory {
        private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        private final Map<String, Object> singletons = new ConcurrentHashMap<>();

        public void registerBeanDefinition(String name, BeanDefinition definition) {
            beanDefinitions.put(name, definition);
        }

        @Override
        public Object getBean(String name) {
            BeanDefinition definition = beanDefinitions.get(name);
            if (definition == null) {
                throw new IllegalArgumentException("No bean named '" + name + "' is defined");
            }
            return createBean(name, definition);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getBean(String name, Class<T> requiredType) {
            Object bean = getBean(name);
            if (!requiredType.isInstance(bean)) {
                throw new IllegalArgumentException(
                        "Bean '" + name + "' is not of required type " + requiredType.getName());
            }
            return (T) bean;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getBean(Class<T> requiredType) {
            return beanDefinitions.entrySet().stream()
                    .filter(e -> requiredType.isAssignableFrom(e.getValue().beanClass()))
                    .map(e -> (T) getBean(e.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No bean of type " + requiredType.getName()));
        }

        @Override
        public boolean containsBean(String name) {
            return beanDefinitions.containsKey(name);
        }

        /**
         * 실제 Bean 생성 로직 - Factory Method의 핵심
         */
        private Object createBean(String name, BeanDefinition definition) {
            // Singleton인 경우 캐시 확인
            if (definition.scope() == BeanDefinition.Scope.SINGLETON) {
                return singletons.computeIfAbsent(name, k -> doCreateBean(definition));
            }
            // Prototype인 경우 항상 새로 생성
            return doCreateBean(definition);
        }

        private Object doCreateBean(BeanDefinition definition) {
            // Factory Method가 정의되어 있으면 사용
            if (definition.factoryMethod() != null) {
                return definition.factoryMethod().get();
            }

            // 기본 생성: 리플렉션으로 인스턴스 생성
            try {
                Constructor<?> constructor = definition.beanClass().getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create bean", e);
            }
        }
    }

    // ===== 테스트용 Bean 클래스들 =====

    interface UserRepository {
        String findById(Long id);
    }

    static class JpaUserRepository implements UserRepository {
        @Override
        public String findById(Long id) {
            return "User from JPA: " + id;
        }
    }

    @Nested
    class BeanFactory의_getBean은_Factory_Method다 {

        @Test
        void getBean으로_객체를_생성한다() {
            SimpleBeanFactory beanFactory = new SimpleBeanFactory();
            beanFactory.registerBeanDefinition("userRepository", BeanDefinition.singleton(JpaUserRepository.class));

            // getBean()이 Factory Method 역할
            UserRepository repository = beanFactory.getBean("userRepository", UserRepository.class);

            assertThat(repository).isInstanceOf(JpaUserRepository.class);
            assertThat(repository.findById(1L)).isEqualTo("User from JPA: 1");
        }

        @Test
        void 타입으로_Bean을_조회할_수_있다() {
            SimpleBeanFactory beanFactory = new SimpleBeanFactory();
            beanFactory.registerBeanDefinition("jpaRepository", BeanDefinition.singleton(JpaUserRepository.class));

            // 타입 기반 조회도 내부적으로 Factory Method 사용
            UserRepository repository = beanFactory.getBean(UserRepository.class);

            assertThat(repository).isNotNull();
        }

        @Test
        void 등록되지_않은_Bean_조회시_예외가_발생한다() {
            SimpleBeanFactory beanFactory = new SimpleBeanFactory();

            assertThatThrownBy(() -> beanFactory.getBean("notExists"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No bean named");
        }
    }
}
