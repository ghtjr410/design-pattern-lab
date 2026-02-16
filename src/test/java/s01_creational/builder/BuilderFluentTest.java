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

    // ===== 필수/선택 파라미터 예제 =====
    static class Member {
        private final String id; // 필수
        private final String name; // 필수
        private final String email; // 선택
        private final int age; // 선택 (기본값: 0)

        private Member(Builder builder) {
            this.id = builder.id;
            this.name = builder.name;
            this.email = builder.email;
            this.age = builder.age;
        }

        String getId() {
            return id;
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
            // 필수 - final로 생성자에서 강제
            private final String id;
            private final String name;

            // 선택 - 기본값 설정
            private String email = "";
            private int age = 0;

            // 필수값은 생성자로 강제
            Builder(String id, String name) {
                this.id = id;
                this.name = name;
            }

            Builder email(String email) {
                this.email = email;
                return this;
            }

            Builder age(int age) {
                this.age = age;
                return this;
            }

            Member build() {
                return new Member(this);
            }
        }
    }

    @Nested
    class 필수_선택_파라미터 {

        @Test
        void 필수값은_Builder_생성자로_강제한다() {
            Member member = new Member.Builder("M001", "John") // 필수
                    .email("john@example.com") // 선택
                    .age(30) // 선택
                    .build();

            assertThat(member.getId()).isEqualTo("M001");
            assertThat(member.getName()).isEqualTo("John");
        }

        @Test
        void 선택값은_생략_가능하다() {
            Member member = new Member.Builder("M002", "Jane")
                    // email, age 생략
                    .build();

            assertThat(member.getName()).isEqualTo("Jane");
            assertThat(member.getEmail()).isEmpty(); // 기본값
            assertThat(member.getAge()).isZero(); // 기본값
        }

        @Test
        void 선택값_일부만_설정_가능하다() {
            Member member = new Member.Builder("M003", "Tom")
                    .age(25) // email은 생략
                    .build();

            assertThat(member.getAge()).isEqualTo(25);
            assertThat(member.getEmail()).isEmpty();
        }
    }

    // ===== Telescoping Constructor 비교 =====
    @Nested
    class Telescoping_Constructor_비교 {

        // 안티패턴: 생성자 파라미터 지옥
        static class TelescopingUser {
            private final String name;
            private final String email;
            private final int age;
            private final String phone;
            private final String address;

            TelescopingUser(String name) {
                this(name, null, 0, null, null);
            }

            TelescopingUser(String name, String email) {
                this(name, email, 0, null, null);
            }

            TelescopingUser(String name, String email, int age) {
                this(name, email, age, null, null);
            }

            TelescopingUser(String name, String email, int age, String phone) {
                this(name, email, age, phone, null);
            }

            TelescopingUser(String name, String email, int age, String phone, String address) {
                this.name = name;
                this.email = email;
                this.age = age;
                this.phone = phone;
                this.address = address;
            }

            String getName() {
                return name;
            }
        }

        @Test
        void Before_파라미터가_뭔지_모름() {
            TelescopingUser user = new TelescopingUser(
                    "John", // name?
                    "john@mail.com", // email?
                    30, // age?
                    "010-1234-5678", // phone? address?
                    "Seoul" // ???
                    );

            assertThat(user.getName()).isEqualTo("John");
        }

        @Test
        void After_Builder로_명확하게() {
            // 각 값의 의미가 명확
            User user = new User.Builder()
                    .name("John")
                    .email("john@mail.com")
                    .age(30)
                    .build();

            assertThat(user.getName()).isEqualTo("John");
        }
    }
}
