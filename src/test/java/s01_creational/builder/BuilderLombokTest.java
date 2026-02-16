package s01_creational.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Lombok @Builder 학습 테스트
 *
 * Lombok @Builder가 생성하는 코드를 직접 구현하여 이해한다.
 * 실무에서는 @Builder 어노테이션만 붙이면 이 코드가 자동 생성됨.
 *
 * 다루는 기능:
 * - @Builder 기본 동작
 * - @Builder.Default (기본값 설정)
 * - @Singular (컬렉션 요소 추가)
 * - @Builder(toBuilder = true) (복사 후 수정)
 * - 검증 로직 추가 방법
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BuilderLombokTest {

    // ===== @Builder 기본 =====
    static class UserDto {
        private final String id;
        private final String name;
        private final String email;

        private UserDto(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public static UserDtoBuilder builder() {
            return new UserDtoBuilder();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public static class UserDtoBuilder {
            private String id;
            private String name;
            private String email;

            public UserDtoBuilder id(String id) {
                this.id = id;
                return this;
            }

            public UserDtoBuilder name(String name) {
                this.name = name;
                return this;
            }

            public UserDtoBuilder email(String email) {
                this.email = email;
                return this;
            }

            // Lombok @Builder는 검증 없이 그냥 생성
            public UserDto build() {
                return new UserDto(id, name, email);
            }
        }
    }

    @Nested
    class 기본 {

        @Test
        void Lombok_Builder는_검증_없이_객체를_생성한다() {
            UserDto user = UserDto.builder()
                    .id("user-001")
                    .name("John")
                    .email("john@example.com")
                    .build();

            assertThat(user.getName()).isEqualTo("John");
        }

        @Test
        void null_값도_허용한다() {
            // Lombok @Builder는 기본적으로 검증하지 않음
            UserDto user = UserDto.builder().id(null).name(null).build();

            assertThat(user.getId()).isNull();
            assertThat(user.getName()).isNull();
        }
    }

    // ===== @Builder.Default =====
    static class ConfigDto {
        private final String host;
        private final int port;
        private final int timeout;
        private final boolean ssl;

        private ConfigDto(String host, int port, int timeout, boolean ssl) {
            this.host = host;
            this.port = port;
            this.timeout = timeout;
            this.ssl = ssl;
        }

        public static ConfigDtoBuilder builder() {
            return new ConfigDtoBuilder();
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public int getTimeout() {
            return timeout;
        }

        public boolean isSsl() {
            return ssl;
        }

        public static class ConfigDtoBuilder {
            private String host;
            // @Builder.Default
            private int port = 8080;
            // @Builder.Default
            private int timeout = 30000;
            // @Builder.Default
            private boolean ssl = false;

            public ConfigDtoBuilder host(String host) {
                this.host = host;
                return this;
            }

            public ConfigDtoBuilder port(int port) {
                this.port = port;
                return this;
            }

            public ConfigDtoBuilder timeout(int timeout) {
                this.timeout = timeout;
                return this;
            }

            public ConfigDtoBuilder ssl(boolean ssl) {
                this.ssl = ssl;
                return this;
            }

            public ConfigDto build() {
                return new ConfigDto(host, port, timeout, ssl);
            }
        }
    }

    @Nested
    class Builder_Default {

        @Test
        void 기본값이_설정된다() {
            ConfigDto config = ConfigDto.builder()
                    .host("localhost")
                    // port, timeout, ssl 생략
                    .build();

            assertThat(config.getHost()).isEqualTo("localhost");
            assertThat(config.getPort()).isEqualTo(8080); // 기본값
            assertThat(config.getTimeout()).isEqualTo(30000); // 기본값
            assertThat(config.isSsl()).isFalse(); // 기본값
        }

        @Test
        void 기본값을_재정의할_수_있다() {
            ConfigDto config = ConfigDto.builder()
                    .host("api.example.com")
                    .port(443)
                    .ssl(true)
                    .build();

            assertThat(config.getPort()).isEqualTo(443);
            assertThat(config.isSsl()).isTrue();
        }
    }
}
