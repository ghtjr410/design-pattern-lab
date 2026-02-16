package s01_creational.builder;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
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
                if (name == null) throw new IllegalStateException("name required");
                return new User(this);
            }
        }
    }

    @Test
    void 체이닝으로_가독성_확보() {
        User user = new User.Builder()
                .name("John")
                .email("john@example.com")
                .age(30)
                .build();
    }
}
