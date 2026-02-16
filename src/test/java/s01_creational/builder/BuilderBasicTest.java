package s01_creational.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Builder 패턴 - 기본 구조 학습 테스트
 *
 * 핵심 의도: 복잡한 객체의 생성 과정과 표현 방법을 분리하여,
 *          동일한 생성 과정에서 서로 다른 표현 결과를 만들 수 있게 한다.
 *
 * 구조:
 * - Builder: 객체의 각 부분을 생성하는 인터페이스
 * - ConcreteBuilder: Builder 인터페이스 구현, 부품을 조립하여 Product 생성
 * - Director: Builder를 사용하여 객체 생성 순서를 제어 (선택적)
 * - Product: 생성될 복잡한 객체
 *
 * 핵심 포인트:
 * 1. 생성 과정을 단계별로 분리 (step-by-step construction)
 * 2. 같은 생성 과정으로 다른 표현(Product)을 만들 수 있음
 * 3. 불변 객체 생성에 유용
 * 4. Telescoping Constructor 안티패턴 해결
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BuilderBasicTest {

    // ===== Product: 생성될 복잡한 객체 =====

    // ===== Builder 인터페이스 =====

    interface CarBuilder {
        CarBuilder setEngine(String engine);

        CarBuilder setTransmission(String transmission);

        CarBuilder setSeats(int seats);

        CarBuilder setGPS(boolean hasGPS);

        CarBuilder setSunroof(boolean hasSunroof);

        CarBuilder setColor(String color);

        void reset();

        Car build();
    }

    // ===== Concrete Builders =====
    static class Car {
        private final String engine;
        private final String transmission;
        private final int seats;
        private final boolean hasGPS;
        private final boolean hasSunroof;
        private final String color;

        Car(String engine, String transmission, int seats, boolean hasGPS, boolean hasSunroof, String color) {
            this.engine = engine;
            this.transmission = transmission;
            this.seats = seats;
            this.hasGPS = hasGPS;
            this.hasSunroof = hasSunroof;
            this.color = color;
        }

        public String getEngine() {
            return engine;
        }

        public String getTransmission() {
            return transmission;
        }

        public int getSeats() {
            return seats;
        }

        public boolean hasGPS() {
            return hasGPS;
        }

        public boolean hasSunroof() {
            return hasSunroof;
        }

        public String getColor() {
            return color;
        }

        @Override
        public String toString() {
            return String.format(
                    "Car[engine=%s, transmission=%s, seats=%d, GPS=%s, sunroof=%s, color=%s]",
                    engine, transmission, seats, hasGPS, hasSunroof, color);
        }
    }

    static class CarBuilderImpl implements CarBuilder {
        private String engine;
        private String transmission;
        private int seats;
        private boolean hasGPS;
        private boolean hasSunroof;
        private String color;

        @Override
        public CarBuilder setEngine(String engine) {
            this.engine = engine;
            return this;
        }

        @Override
        public CarBuilder setTransmission(String transmission) {
            this.transmission = transmission;
            return this;
        }

        @Override
        public CarBuilder setSeats(int seats) {
            this.seats = seats;
            return this;
        }

        @Override
        public CarBuilder setGPS(boolean hasGPS) {
            this.hasGPS = hasGPS;
            return this;
        }

        @Override
        public CarBuilder setSunroof(boolean hasSunroof) {
            this.hasSunroof = hasSunroof;
            return this;
        }

        @Override
        public CarBuilder setColor(String color) {
            this.color = color;
            return this;
        }

        @Override
        public void reset() {
            this.engine = null;
            this.transmission = null;
            this.seats = 0;
            this.hasGPS = false;
            this.hasSunroof = false;
            this.color = null;
        }

        @Override
        public Car build() {
            return new Car(engine, transmission, seats, hasGPS, hasSunroof, color);
        }
    }

    @Nested
    class Builder_핵심_구조 {

        @Test
        void Builder로_복잡한_객체를_단계별로_생성한다() {
            CarBuilderImpl builder = new CarBuilderImpl();

            // 단계별 설정
            builder.setEngine("V6")
                    .setTransmission("Automatic")
                    .setSeats(5)
                    .setGPS(true)
                    .setSunroof(true)
                    .setColor("Blue");

            Car car = builder.build();

            assertThat(car.getEngine()).isEqualTo("V6");
            assertThat(car.getSeats()).isEqualTo(5);
            assertThat(car.hasGPS()).isTrue();
            assertThat(car.getColor()).isEqualTo("Blue");
        }

        @Test
        void Builder는_Fluent_API_스타일을_제공한다() {
            // Method Chaining으로 가독성 향상
            Car car = new CarBuilderImpl()
                    .setEngine("V8")
                    .setTransmission("Automatic")
                    .setSeats(4)
                    .setGPS(true)
                    .setSunroof(true)
                    .setColor("Black")
                    .build();

            assertThat(car.getEngine()).isEqualTo("V8");
        }

        @Test
        void 필요한_부분만_설정할_수_있다() {
            // 모든 필드를 설정하지 않아도 됨
            Car car = new CarBuilderImpl().setEngine("V4").setSeats(4).build();

            assertThat(car.getEngine()).isEqualTo("V4");
            assertThat(car.getSeats()).isEqualTo(4);
            assertThat(car.getTransmission()).isNull(); // 미설정
            assertThat(car.hasGPS()).isFalse(); // 기본값
        }
    }
}
