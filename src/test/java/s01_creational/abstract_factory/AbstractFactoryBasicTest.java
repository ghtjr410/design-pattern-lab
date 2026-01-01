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
}
