package s01_creational.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Builder 패턴 - 현대 Fluent API
 *
 * 목적: 가독성 좋은 객체 생성, Telescoping Constructor 해결
 * 실무: DTO, 설정 객체, 도메인 객체 생성
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BuilderFluentTest {

    static class User {
        private final String name;
        private final String email;
        private final int age;

        private User(Builder builder) {
            this.name = builder.name;
            this.email = builder.email;
            this.age = builder.age;
        }

        String getName() {
            return name;
        }

        String getEmail() {
            return email;
        }

        int getAge() {
            return age;
        }

        static class Builder {
            private String name;
            private String email;
            private int age;

            Builder name(String name) {
                this.name = name;
                return this; // 체이닝 핵심
            }

            Builder email(String email) {
                this.email = email;
                return this;
            }

            Builder age(int age) {
                this.age = age;
                return this;
            }

            User build() {
                return new User(this);
            }
        }
    }

    @Nested
    class 기본_체이닝 {

        @Test
        void 체이닝으로_가독성_확보() {
            User user = new User.Builder()
                    .name("John")
                    .email("john@example.com")
                    .age(30)
                    .build();

            assertThat(user.getName()).isEqualTo("John");
            assertThat(user.getEmail()).isEqualTo("john@example.com");
        }

        @Test
        void 순서_상관없이_설정_가능() {
            User user = new User.Builder()
                    .age(25) // 순서 바꿔도
                    .name("Jane") // 상관없음
                    .email("jane@example.com")
                    .build();

            assertThat(user.getName()).isEqualTo("Jane");
        }
    }

    // ===== 유효성 검증 예제 =====
    static class ValidatedUser {
        private final String name;
        private final String email;
        private final int age;

        private ValidatedUser(Builder builder) {
            this.name = builder.name;
            this.email = builder.email;
            this.age = builder.age;
        }

        String getName() {
            return name;
        }

        String getEmail() {
            return email;
        }

        int getAge() {
            return age;
        }

        static class Builder {
            private String name;
            private String email;
            private int age;

            Builder name(String name) {
                this.name = name;
                return this;
            }

            Builder email(String email) {
                this.email = email;
                return this;
            }

            Builder age(int age) {
                this.age = age;
                return this;
            }

            ValidatedUser build() {
                // 필수값 검증
                if (name == null || name.isBlank()) {
                    throw new IllegalStateException("name is required");
                }
                if (email == null || !email.contains("@")) {
                    throw new IllegalStateException("valid email is required");
                }
                // 범위 검증
                if (age < 0 || age > 150) {
                    throw new IllegalStateException("age must be 0-150");
                }
                return new ValidatedUser(this);
            }
        }
    }

    @Nested
    class 유효성_검증 {

        @Test
        void build_시점에_필수값을_검증한다() {
            assertThatThrownBy(() -> new ValidatedUser.Builder()
                            // name 누락
                            .email("john@example.com")
                            .age(30)
                            .build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("name is required");
        }

        @Test
        void build_시점에_형식을_검증한다() {
            assertThatThrownBy(() -> new ValidatedUser.Builder()
                            .name("John")
                            .email("invalid-email") // @ 없음
                            .age(30)
                            .build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("valid email is required");
        }

        @Test
        void build_시점에_범위를_검증한다() {
            assertThatThrownBy(() -> new ValidatedUser.Builder()
                            .name("John")
                            .email("john@example.com")
                            .age(-5) // 음수
                            .build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("age must be 0-150");
        }

        @Test
        void 검증_통과시_객체_생성() {
            ValidatedUser user = new ValidatedUser.Builder()
                    .name("John")
                    .email("john@example.com")
                    .age(30)
                    .build();

            assertThat(user.getName()).isEqualTo("John");
        }
    }
}
