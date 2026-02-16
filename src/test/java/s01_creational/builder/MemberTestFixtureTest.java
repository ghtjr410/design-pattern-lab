package s01_creational.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test Data Builder 패턴
 *
 * 테스트 코드에서 객체 생성할 때:
 * - 테스트에 필요한 속성만 명시
 * - 나머지는 합리적인 기본값
 * - 시나리오별 팩토리 메서드로 의도 명확하게
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MemberTestFixtureTest {

    // ===== 도메인 객체 =====
    enum MemberGrade {
        BRONZE,
        SILVER,
        GOLD,
        VIP
    }

    static class Member {
        private final Long id;
        private final String email;
        private final String name;
        private final MemberGrade grade;
        private final LocalDate joinedAt;
        private final boolean active;

        Member(Long id, String email, String name, MemberGrade grade, LocalDate joinedAt, boolean active) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.grade = grade;
            this.joinedAt = joinedAt;
            this.active = active;
        }

        Long getId() {
            return id;
        }

        String getEmail() {
            return email;
        }

        String getName() {
            return name;
        }

        MemberGrade getGrade() {
            return grade;
        }

        LocalDate getJoinedAt() {
            return joinedAt;
        }

        boolean isActive() {
            return active;
        }

        boolean isVip() {
            return grade == MemberGrade.VIP;
        }
    }

    // ===== Test Data Builder =====
    static class MemberTestBuilder {
        private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

        private Long id = ID_GENERATOR.getAndIncrement();
        private String email;
        private String name = "테스트회원";
        private MemberGrade grade = MemberGrade.BRONZE;
        private LocalDate joinedAt = LocalDate.now();
        private boolean active = true;

        // 기본 팩토리 메서드
        static MemberTestBuilder aMember() {
            MemberTestBuilder builder = new MemberTestBuilder();
            builder.email = "test" + builder.id + "@example.com";
            return builder;
        }

        // 시나리오별 팩토리 메서드
        static MemberTestBuilder aVipMember() {
            return aMember().withGrade(MemberGrade.VIP);
        }

        static MemberTestBuilder anInactiveMember() {
            return aMember().withActive(false);
        }

        // with 메서드들
        MemberTestBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        MemberTestBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        MemberTestBuilder withName(String name) {
            this.name = name;
            return this;
        }

        MemberTestBuilder withGrade(MemberGrade grade) {
            this.grade = grade;
            return this;
        }

        MemberTestBuilder withJoinedAt(LocalDate joinedAt) {
            this.joinedAt = joinedAt;
            return this;
        }

        MemberTestBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        Member build() {
            return new Member(id, email, name, grade, joinedAt, active);
        }
    }

    // ===== 테스트 =====

    @Nested
    class Before_After_비교 {

        @Test
        void Before_생성자로_직접_생성하면_의도가_안보인다() {
            // 어떤 값이 VIP를 의미하는지?
            Member member = new Member(
                    1L,
                    "test@example.com",
                    "테스트",
                    MemberGrade.VIP, // 이게 VIP인 건 알겠는데...
                    LocalDate.now(), // 이건 왜 필요하지?
                    true // 이건 뭐지?
                    );

            assertThat(member.isVip()).isTrue();
        }

        @Test
        void After_TestBuilder로_생성하면_의도가_명확하다() {
            // "VIP 회원"이 필요하다는 의도가 바로 보임
            Member member = MemberTestBuilder.aVipMember().build();

            assertThat(member.isVip()).isTrue();
        }

        @Test
        void After_필요한_속성만_명시하면_된다() {
            // 이 테스트는 "비활성 회원" 검증이 목적
            // → active만 명시, 나머지는 기본값
            Member member = MemberTestBuilder.aMember().withActive(false).build();

            assertThat(member.isActive()).isFalse();
            // id, email, name 등은 테스트와 무관 → 기본값으로 숨김
        }
    }
}
