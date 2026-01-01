package s01_creational.abstract_factory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Abstract Factory 패턴 - 기본 구조 학습 테스트
 *
 * 핵심 의도: 관련된 객체들의 군(family)을 생성하는 인터페이스를 제공한다.
 *          구체적인 클래스를 지정하지 않고도 관련 객체들을 일관성 있게 생성할 수 있다.
 *
 * 구조:
 * - AbstractFactory: 제품군을 생성하는 인터페이스 (createProductA(), createProductB())
 * - ConcreteFactory: 특정 제품군을 생성하는 구현체
 * - AbstractProduct: 제품의 인터페이스 (ProductA, ProductB)
 * - ConcreteProduct: 특정 제품군에 속하는 구체적인 제품
 *
 * 핵심 포인트:
 * 1. "제품군(Product Family)"이라는 개념이 핵심
 * 2. 같은 제품군의 객체들은 함께 사용되도록 설계됨
 * 3. 제품군 간의 일관성을 컴파일 타임에 보장
 * 4. 새로운 제품군 추가는 쉽지만, 새로운 제품 타입 추가는 어려움
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AbstractFactoryBasicTest {

    // ===== Abstract Products: 제품 인터페이스들 =====

    /**
     * 가구 제품군 예시: Chair, Table, Sofa
     * 각 제품은 스타일(Victorian, Modern, ArtDeco)에 따라 다른 구현을 가짐
     */
    interface Chair {
        String getStyle();

        boolean hasLegs();

        void sitOn();
    }

    interface Table {
        String getStyle();

        int getNumberOfLegs();

        void putItem(String item);
    }

    interface Sofa {
        String getStyle();

        int getSeats();

        void lieOn();
    }

    // ===== Concrete Products: Victorian 스타일 제품군 =====

    static class VictorianChair implements Chair {
        @Override
        public String getStyle() {
            return "Victorian";
        }

        @Override
        public boolean hasLegs() {
            return true; // Victorian 의자는 다리가 있음
        }

        @Override
        public void sitOn() {
            System.out.println("Sitting on an elegant Victorian chair");
        }
    }

    static class VictorianTable implements Table {
        @Override
        public String getStyle() {
            return "Victorian";
        }

        @Override
        public int getNumberOfLegs() {
            return 4; // Victorian 테이블은 4개 다리
        }

        @Override
        public void putItem(String item) {
            System.out.println("Putting " + item + " on Victorian table with ornate carvings");
        }
    }

    static class VictorianSofa implements Sofa {
        @Override
        public String getStyle() {
            return "Victorian";
        }

        @Override
        public int getSeats() {
            return 3; // Victorian 소파는 보통 3인용
        }

        @Override
        public void lieOn() {
            System.out.println("Lying on a luxurious Victorian sofa");
        }
    }

    // ===== Concrete Products: Modern 스타일 제품군 =====

    static class ModernChair implements Chair {
        @Override
        public String getStyle() {
            return "Modern";
        }

        @Override
        public boolean hasLegs() {
            return false; // Modern 의자는 다리 없는 디자인도 있음
        }

        @Override
        public void sitOn() {
            System.out.println("Sitting on a minimalist Modern chair");
        }
    }

    static class ModernTable implements Table {
        @Override
        public String getStyle() {
            return "Modern";
        }

        @Override
        public int getNumberOfLegs() {
            return 1; // Modern 테이블은 중앙 기둥 하나
        }

        @Override
        public void putItem(String item) {
            System.out.println("Putting " + item + " on sleek Modern table");
        }
    }

    static class ModernSofa implements Sofa {
        @Override
        public String getStyle() {
            return "Modern";
        }

        @Override
        public int getSeats() {
            return 2; // Modern 소파는 컴팩트한 2인용
        }

        @Override
        public void lieOn() {
            System.out.println("Lying on a compact Modern sofa");
        }
    }

    // ===== Abstract Factory: 가구 공장 인터페이스 =====

    interface FurnitureFactory {
        Chair createChair();

        Table createTable();

        Sofa createSofa();
    }

    // ===== Concrete Factories =====

    static class VictorianFurnitureFactory implements FurnitureFactory {
        @Override
        public Chair createChair() {
            return new VictorianChair();
        }

        @Override
        public Table createTable() {
            return new VictorianTable();
        }

        @Override
        public Sofa createSofa() {
            return new VictorianSofa();
        }
    }

    static class ModernFurnitureFactory implements FurnitureFactory {
        @Override
        public Chair createChair() {
            return new ModernChair();
        }

        @Override
        public Table createTable() {
            return new ModernTable();
        }

        @Override
        public Sofa createSofa() {
            return new ModernSofa();
        }
    }

    /**
     * 클라이언트 코드: 팩토리를 통해 제품군을 사용
     * 구체적인 제품 클래스를 알 필요 없음
     */
    static class FurnitureShop {
        private final Chair chair;
        private final Table table;
        private final Sofa sofa;

        FurnitureShop(FurnitureFactory factory) {
            // 팩토리에서 제품군을 받아옴
            this.chair = factory.createChair();
            this.table = factory.createTable();
            this.sofa = factory.createSofa();
        }

        String getSetStyle() {
            // 세트의 일관성 확인
            if (chair.getStyle().equals(table.getStyle()) && table.getStyle().equals(sofa.getStyle())) {
                return chair.getStyle();
            }
            return "Mismatched";
        }

        String describe() {
            return String.format(
                    "%s furniture set: chair(legs=%s), table(%d legs), sofa(%d seats)",
                    getSetStyle(), chair.hasLegs(), table.getNumberOfLegs(), sofa.getSeats());
        }
    }

    @Nested
    class Abstract_Factory_핵심_구조 {

        @Test
        void 팩토리는_관련된_제품군을_생성한다() {
            FurnitureFactory victorianFactory = new VictorianFurnitureFactory();

            Chair chair = victorianFactory.createChair();
            Table table = victorianFactory.createTable();
            Sofa sofa = victorianFactory.createSofa();

            // 같은 팩토리에서 생성된 제품들은 같은 스타일
            assertThat(chair.getStyle()).isEqualTo("Victorian");
            assertThat(table.getStyle()).isEqualTo("Victorian");
            assertThat(sofa.getStyle()).isEqualTo("Victorian");
        }

        @Test
        void 다른_팩토리는_다른_제품군을_생성한다() {
            FurnitureFactory modernFactory = new ModernFurnitureFactory();

            Chair chair = modernFactory.createChair();
            Table table = modernFactory.createTable();
            Sofa sofa = modernFactory.createSofa();

            assertThat(chair.getStyle()).isEqualTo("Modern");
            assertThat(table.getStyle()).isEqualTo("Modern");
            assertThat(sofa.getStyle()).isEqualTo("Modern");
        }

        @Test
        void 제품군_내_제품들은_서로_호환된다() {
            FurnitureFactory victorianFactory = new VictorianFurnitureFactory();

            Chair chair = victorianFactory.createChair();
            Table table = victorianFactory.createTable();

            // Victorian 제품들은 서로 어울림 (스타일 일관성)
            assertThat(chair.getStyle()).isEqualTo(table.getStyle());

            // 제품별 특성도 스타일에 맞게 설계됨
            assertThat(chair.hasLegs()).isTrue(); // Victorian은 전통적인 다리 있음
            assertThat(table.getNumberOfLegs()).isEqualTo(4); // 4개 다리
        }
    }

    @Nested
    class 제품군_일관성_보장 {

        @Test
        void 클라이언트는_팩토리만_알면_된다() {
            // 클라이언트는 구체적인 제품 클래스를 모름
            // FurnitureFactory 인터페이스만 의존
            FurnitureShop victorianShop = new FurnitureShop(new VictorianFurnitureFactory());
            FurnitureShop modernShop = new FurnitureShop(new ModernFurnitureFactory());

            assertThat(victorianShop.getSetStyle()).isEqualTo("Victorian");
            assertThat(modernShop.getSetStyle()).isEqualTo("Modern");
        }

        @Test
        void 팩토리_교체만으로_전체_제품군이_바뀐다() {
            FurnitureFactory factory = new VictorianFurnitureFactory();
            FurnitureShop shop = new FurnitureShop(factory);

            assertThat(shop.getSetStyle()).isEqualTo("Victorian");
            assertThat(shop.describe()).contains("Victorian");

            // 팩토리만 교체하면 전체 제품군이 바뀜
            factory = new ModernFurnitureFactory();
            shop = new FurnitureShop(factory);

            assertThat(shop.getSetStyle()).isEqualTo("Modern");
            assertThat(shop.describe()).contains("Modern");
        }

        @Test
        void 제품군_일관성은_컴파일_타임에_보장된다() {
            /*
             * Abstract Factory의 핵심 장점:
             * - Victorian 팩토리는 Victorian 제품만 생성 가능
             * - Modern 팩토리는 Modern 제품만 생성 가능
             * - 잘못된 조합(Victorian Chair + Modern Table)이 불가능
             *
             * 컴파일 타임에 타입 시스템으로 보장됨
             */
            FurnitureFactory victorianFactory = new VictorianFurnitureFactory();

            Chair chair = victorianFactory.createChair();
            Table table = victorianFactory.createTable();

            // 같은 팩토리에서 나온 제품은 항상 같은 스타일
            // 런타임 체크 불필요
            assertThat(chair.getStyle()).isEqualTo(table.getStyle());
        }
    }

    @Nested
    class 새로운_제품군_추가_OCP {

        // ===== 새로운 제품군: ArtDeco 스타일 =====

        static class ArtDecoChair implements Chair {
            @Override
            public String getStyle() {
                return "ArtDeco";
            }

            @Override
            public boolean hasLegs() {
                return true;
            }

            @Override
            public void sitOn() {
                System.out.println("Sitting on a geometric ArtDeco chair");
            }
        }

        static class ArtDecoTable implements Table {
            @Override
            public String getStyle() {
                return "ArtDeco";
            }

            @Override
            public int getNumberOfLegs() {
                return 3; // ArtDeco는 삼각형 베이스
            }

            @Override
            public void putItem(String item) {
                System.out.println("Putting " + item + " on geometric ArtDeco table");
            }
        }

        static class ArtDecoSofa implements Sofa {
            @Override
            public String getStyle() {
                return "ArtDeco";
            }

            @Override
            public int getSeats() {
                return 4;
            }

            @Override
            public void lieOn() {
                System.out.println("Lying on a glamorous ArtDeco sofa");
            }
        }

        // 새로운 팩토리 추가 - 기존 코드 수정 없음
        static class ArtDecoFurnitureFactory implements FurnitureFactory {
            @Override
            public Chair createChair() {
                return new ArtDecoChair();
            }

            @Override
            public Table createTable() {
                return new ArtDecoTable();
            }

            @Override
            public Sofa createSofa() {
                return new ArtDecoSofa();
            }
        }

        @Test
        void 새로운_제품군을_기존_코드_수정_없이_추가할_수_있다() {
            // 기존 Victorian, Modern 팩토리는 전혀 수정하지 않음
            FurnitureFactory artDecoFactory = new ArtDecoFurnitureFactory();

            Chair chair = artDecoFactory.createChair();
            Table table = artDecoFactory.createTable();
            Sofa sofa = artDecoFactory.createSofa();

            assertThat(chair.getStyle()).isEqualTo("ArtDeco");
            assertThat(table.getStyle()).isEqualTo("ArtDeco");
            assertThat(sofa.getStyle()).isEqualTo("ArtDeco");
        }

        @Test
        void 새로운_제품군도_기존_클라이언트_코드와_호환된다() {
            // FurnitureShop은 FurnitureFactory 인터페이스만 의존
            // ArtDecoFurnitureFactory도 같은 인터페이스 구현
            FurnitureShop artDecoShop = new FurnitureShop(new ArtDecoFurnitureFactory());

            assertThat(artDecoShop.getSetStyle()).isEqualTo("ArtDeco");
        }
    }

    @Nested
    class 새로운_제품_타입_추가의_어려움 {

        @Test
        void 새로운_제품_타입_추가는_모든_팩토리_수정이_필요하다() {

            // 현재 구조에서 Lamp를 추가하려면...
            // FurnitureFactory 인터페이스 수정 필요
            // 모든 ConcreteFactory 수정 필요

            /*
             * Abstract Factory의 단점:
             *
             * 만약 "Lamp"라는 새로운 제품을 추가하려면:
             *
             * 1. Lamp 인터페이스 정의
             * 2. VictorianLamp, ModernLamp, ArtDecoLamp 구현
             * 3. FurnitureFactory에 createLamp() 메서드 추가
             * 4. 모든 ConcreteFactory(Victorian, Modern, ArtDeco)에 createLamp() 구현
             *
             * → 기존 팩토리 코드를 모두 수정해야 함 (OCP 위반)
             *
             * 이것이 Abstract Factory의 트레이드오프:
             * - 새 제품군 추가: 쉬움 (OCP 준수)
             * - 새 제품 타입 추가: 어려움 (기존 코드 수정 필요)
             *
             * ---
             *
             * 고찰: 이것은 Abstract Factory만의 단점이 아니다.
             *
             * 인터페이스에 메서드를 추가하면 모든 구현체를 수정해야 하는 것은
             * 인터페이스 자체의 특성이다. (Expression Problem)
             *
             * 해결 방법:
             * - 인터페이스 분리 (ISP): LampFactory를 별도 인터페이스로 분리
             * - 하이브리드 접근: sealed + switch 조합으로 확장 방향 유연화
             *
             * 설계 시 고려할 점:
             * - 제품군이 자주 추가될 것 같다 → Abstract Factory 적합
             * - 제품 타입이 자주 추가될 것 같다 → 다른 패턴 또는 인터페이스 분리 고려
             */
        }
    }

    @Nested
    class 다형성을_활용한_팩토리_선택 {

        /*
         * 런타임에 팩토리를 선택하는 방법을 보여주는 테스트.
         *
         * 주의: 아래 createFactory()의 switch + new 방식은 학습용 예시다.
         *
         * 문제점:
         * - 새로운 스타일 추가 시 switch문 수정 필요 (OCP 위반)
         * - 구체 클래스를 직접 new하므로 결합도가 높음
         *
         * 실무에서 더 나은 방법:
         * 1. Factory가 자신의 스타일을 반환하게 하고 List + findFirst로 탐색
         * 2. Map<Style, Factory>에 미리 등록해두고 조회
         * 3. Spring이면 List<Factory> 자동 주입 + Map으로 변환
         *
         * 이렇게 하면 새 Factory 추가 시 기존 코드 수정 없이 확장 가능.
         */

        enum FurnitureStyle {
            VICTORIAN,
            MODERN
        }

        static FurnitureFactory createFactory(FurnitureStyle style) {
            return switch (style) {
                case VICTORIAN -> new VictorianFurnitureFactory();
                case MODERN -> new ModernFurnitureFactory();
            };
        }

        @Test
        void 런타임에_팩토리를_선택할_수_있다() {
            FurnitureStyle userChoice = FurnitureStyle.MODERN;

            FurnitureFactory factory = createFactory(userChoice);
            Chair chair = factory.createChair();

            assertThat(chair.getStyle()).isEqualTo("Modern");
        }

        @Test
        void 설정에_따라_다른_제품군을_사용할_수_있다() {
            // 환경 설정이나 사용자 선호도에 따라 팩토리 선택
            String config = "VICTORIAN"; // 예: 설정 파일에서 읽어옴

            FurnitureStyle style = FurnitureStyle.valueOf(config);
            FurnitureFactory factory = createFactory(style);

            FurnitureShop shop = new FurnitureShop(factory);

            assertThat(shop.getSetStyle()).isEqualTo("Victorian");
        }
    }
}
