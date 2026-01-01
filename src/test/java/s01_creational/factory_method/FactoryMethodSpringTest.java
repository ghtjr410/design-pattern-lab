package s01_creational.factory_method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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

    static class InMemoryUserRepository implements UserRepository {
        private final Map<Long, String> store = new HashMap<>();

        @Override
        public String findById(Long id) {
            return store.getOrDefault(id, "Not found: " + id);
        }

        public void save(Long id, String name) {
            store.put(id, name);
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

    @Nested
    class FactoryBean_패턴 {

        /**
         * Spring의 FactoryBean<T> 시뮬레이션
         * 복잡한 객체 생성 로직을 캡슐화
         */
        interface FactoryBean<T> {
            T getObject();

            Class<?> getObjectType();

            default boolean isSingleton() {
                return true;
            }
        }

        /**
         * 복잡한 설정이 필요한 객체를 생성하는 FactoryBean
         */
        static class DataSourceFactoryBean implements FactoryBean<DataSource> {
            private String url;
            private String username;
            private String password;
            private int poolSize = 10;

            @Override
            public DataSource getObject() {
                // 복잡한 DataSource 생성 로직
                DataSource dataSource = new DataSource();
                dataSource.setUrl(url);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                dataSource.setPoolSize(poolSize);
                dataSource.initialize();
                return dataSource;
            }

            @Override
            public Class<?> getObjectType() {
                return DataSource.class;
            }

            // Builder 스타일 설정
            public DataSourceFactoryBean url(String url) {
                this.url = url;
                return this;
            }

            public DataSourceFactoryBean username(String username) {
                this.username = username;
                return this;
            }

            public DataSourceFactoryBean password(String password) {
                this.password = password;
                return this;
            }

            public DataSourceFactoryBean poolSize(int poolSize) {
                this.poolSize = poolSize;
                return this;
            }
        }

        static class DataSource {
            private String url;
            private String username;
            private String password;
            private int poolSize;
            private boolean initialized;

            void setUrl(String url) {
                this.url = url;
            }

            void setUsername(String username) {
                this.username = username;
            }

            void setPassword(String password) {
                this.password = password;
            }

            void setPoolSize(int poolSize) {
                this.poolSize = poolSize;
            }

            void initialize() {
                this.initialized = true;
            }

            String getUrl() {
                return url;
            }

            int getPoolSize() {
                return poolSize;
            }

            boolean isInitialized() {
                return initialized;
            }
        }

        @Test
        void FactoryBean은_복잡한_생성_로직을_캡슐화한다() {
            DataSourceFactoryBean factoryBean = new DataSourceFactoryBean()
                    .url("jdbc:mysql://localhost:3306/test")
                    .username("root")
                    .password("password")
                    .poolSize(20);

            // FactoryBean의 getObject()가 Factory Method
            DataSource dataSource = factoryBean.getObject();

            assertThat(dataSource.getUrl()).isEqualTo("jdbc:mysql://localhost:3306/test");
            assertThat(dataSource.getPoolSize()).isEqualTo(20);
            assertThat(dataSource.isInitialized()).isTrue();
        }

        /**
         * FactoryBean을 지원하는 확장된 BeanFactory
         */
        static class FactoryBeanAwareBeanFactory extends SimpleBeanFactory {
            private final Map<String, FactoryBean<?>> factoryBeans = new HashMap<>();
            private final Map<String, Object> factoryBeanProducts = new ConcurrentHashMap<>();

            public void registerFactoryBean(String name, FactoryBean<?> factoryBean) {
                factoryBeans.put(name, factoryBean);
            }

            @Override
            public Object getBean(String name) {
                // FactoryBean이 등록되어 있으면 getObject() 결과 반환
                if (factoryBeans.containsKey(name)) {
                    FactoryBean<?> factoryBean = factoryBeans.get(name);
                    if (factoryBean.isSingleton()) {
                        return factoryBeanProducts.computeIfAbsent(name, k -> factoryBean.getObject());
                    }
                    return factoryBean.getObject();
                }
                return super.getBean(name);
            }

            // &name으로 FactoryBean 자체를 가져옴 (Spring 규약)
            public FactoryBean<?> getFactoryBean(String name) {
                return factoryBeans.get(name);
            }
        }

        @Test
        void BeanFactory는_FactoryBean의_getObject_결과를_반환한다() {
            FactoryBeanAwareBeanFactory beanFactory = new FactoryBeanAwareBeanFactory();

            DataSourceFactoryBean factoryBean = new DataSourceFactoryBean()
                    .url("jdbc:mysql://localhost/db")
                    .username("user")
                    .password("pass");

            beanFactory.registerFactoryBean("dataSource", factoryBean);

            // getBean("dataSource")는 DataSource를 반환 (FactoryBean이 아님)
            Object bean = beanFactory.getBean("dataSource");

            assertThat(bean).isInstanceOf(DataSource.class);
            assertThat(((DataSource) bean).getUrl()).isEqualTo("jdbc:mysql://localhost/db");
        }

        @Test
        void FactoryBean_자체도_조회할_수_있다() {
            FactoryBeanAwareBeanFactory beanFactory = new FactoryBeanAwareBeanFactory();
            DataSourceFactoryBean factoryBean = new DataSourceFactoryBean();
            beanFactory.registerFactoryBean("dataSource", factoryBean);

            // Spring에서는 &dataSource로 FactoryBean 자체를 가져옴
            FactoryBean<?> retrieved = beanFactory.getFactoryBean("dataSource");

            assertThat(retrieved).isSameAs(factoryBean);
        }
    }

    @Nested
    class Profile에_따른_Bean_선택 {

        /**
         * @Profile 어노테이션 시뮬레이션
         */
        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        @interface Profile {
            String value();
        }

        @Profile("production")
        static class ProductionUserRepository implements UserRepository {
            @Override
            public String findById(Long id) {
                return "Production User: " + id;
            }
        }

        @Profile("test")
        static class TestUserRepository implements UserRepository {
            @Override
            public String findById(Long id) {
                return "Test User: " + id;
            }
        }

        /**
         * Profile 인식 BeanFactory
         */
        static class ProfileAwareBeanFactory extends SimpleBeanFactory {
            private final String activeProfile;
            private final Map<String, Map<String, BeanDefinition>> profileDefinitions = new HashMap<>();

            ProfileAwareBeanFactory(String activeProfile) {
                this.activeProfile = activeProfile;
            }

            public void registerBeanDefinitionForProfile(String beanName, String profile, BeanDefinition definition) {
                profileDefinitions
                        .computeIfAbsent(beanName, k -> new HashMap<>())
                        .put(profile, definition);
            }

            @Override
            public Object getBean(String name) {
                Map<String, BeanDefinition> profiles = profileDefinitions.get(name);
                if (profiles != null && profiles.containsKey(activeProfile)) {
                    BeanDefinition definition = profiles.get(activeProfile);
                    return createBeanFromDefinition(definition);
                }
                return super.getBean(name);
            }

            private Object createBeanFromDefinition(BeanDefinition definition) {
                try {
                    return definition.beanClass().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Test
        void Production_프로파일에서는_Production_Bean이_생성된다() {
            ProfileAwareBeanFactory beanFactory = new ProfileAwareBeanFactory("production");

            beanFactory.registerBeanDefinitionForProfile(
                    "userRepository", "production", BeanDefinition.singleton(ProductionUserRepository.class));
            beanFactory.registerBeanDefinitionForProfile(
                    "userRepository", "test", BeanDefinition.singleton(TestUserRepository.class));

            UserRepository repository = (UserRepository) beanFactory.getBean("userRepository");

            assertThat(repository).isInstanceOf(ProductionUserRepository.class);
            assertThat(repository.findById(1L)).startsWith("Production");
        }

        @Test
        void Test_프로파일에서는_Test_Bean이_생성된다() {
            ProfileAwareBeanFactory beanFactory = new ProfileAwareBeanFactory("test");

            beanFactory.registerBeanDefinitionForProfile(
                    "userRepository", "production", BeanDefinition.singleton(ProductionUserRepository.class));
            beanFactory.registerBeanDefinitionForProfile(
                    "userRepository", "test", BeanDefinition.singleton(TestUserRepository.class));

            UserRepository repository = (UserRepository) beanFactory.getBean("userRepository");

            assertThat(repository).isInstanceOf(TestUserRepository.class);
            assertThat(repository.findById(1L)).startsWith("Test");
        }
    }

    @Nested
    class Factory_Method_패턴이_Spring에서_중요한_이유 {

        @Test
        void 객체_생성_로직을_중앙에서_관리할_수_있다() {
            /*
             * Factory Method 패턴의 Spring 적용 이점:
             *
             * 1. 생성 로직 캡슐화
             *    - new 키워드가 코드 전체에 흩어지지 않음
             *    - BeanFactory/ApplicationContext가 모든 생성을 담당
             *
             * 2. 의존성 관리
             *    - Bean 간 의존성을 Container가 관리
             *    - 개발자는 "무엇"이 필요한지만 선언
             *
             * 3. 생명주기 관리
             *    - 객체 생성/소멸을 Container가 관리
             *    - @PostConstruct, @PreDestroy 등 Hook 제공
             *
             * 4. AOP 적용 용이
             *    - Factory에서 생성 시점에 Proxy 적용 가능
             *    - 트랜잭션, 로깅 등을 투명하게 적용
             */

            SimpleBeanFactory beanFactory = new SimpleBeanFactory();
            beanFactory.registerBeanDefinition("userRepository", BeanDefinition.singleton(JpaUserRepository.class));

            // 클라이언트는 생성 방법을 몰라도 됨
            UserRepository repository = beanFactory.getBean(UserRepository.class);

            assertThat(repository).isNotNull();
        }

        @Test
        void 테스트시_쉽게_Mock으로_교체할_수_있다() {
            /*
             * Factory Method를 통한 테스트 용이성:
             *
             * Production: JpaUserRepository 등록
             * Test: InMemoryUserRepository 등록
             *
             * 클라이언트 코드 변경 없이 구현체 교체 가능
             */

            // Production 설정
            SimpleBeanFactory prodFactory = new SimpleBeanFactory();
            prodFactory.registerBeanDefinition("userRepository", BeanDefinition.singleton(JpaUserRepository.class));

            // Test 설정
            SimpleBeanFactory testFactory = new SimpleBeanFactory();
            testFactory.registerBeanDefinition(
                    "userRepository", BeanDefinition.singleton(InMemoryUserRepository.class));

            UserRepository prodRepo = prodFactory.getBean(UserRepository.class);
            UserRepository testRepo = testFactory.getBean(UserRepository.class);

            assertThat(prodRepo).isInstanceOf(JpaUserRepository.class);
            assertThat(testRepo).isInstanceOf(InMemoryUserRepository.class);
        }
    }
}
