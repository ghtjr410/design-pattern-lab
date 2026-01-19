package s01_creational.abstract_factory;

import static org.assertj.core.api.Assertions.assertThat;
import static s01_creational.abstract_factory.AbstractFactoryRealWorldTest.로그인_폼_구성_시나리오.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Abstract Factory 패턴 - 실무 적용 예제
 *
 * 시나리오: UI 테마 시스템
 * - Light/Dark 테마에 따라 다른 UI 컴포넌트 생성
 * - 버튼, 체크박스, 텍스트필드 등의 컴포넌트가 테마별로 다른 스타일
 * - 테마 내 컴포넌트들은 일관된 룩앤필 유지
 *
 * 실무 포인트:
 * 1. 테마 변경 시 모든 컴포넌트가 일관되게 변경
 * 2. 새로운 테마 추가가 용이 (OCP 준수)
 * 3. 컴포넌트 간 스타일 일관성 보장
 *
 * 팩토리가 클라이언트에게 하는 약속:
 * "나한테서 나온 것들은 무조건 세트야.
 *  니가 신경 안 써도 돼. 내가 보장할게."
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AbstractFactoryRealWorldTest {

    // ===== Abstract Products: UI 컴포넌트 인터페이스들 =====

    interface Button {
        String getTheme();

        String render();

        void onClick(Runnable action);
    }

    interface Checkbox {
        String getTheme();

        String render();

        boolean isChecked();

        void setChecked(boolean checked);
    }

    interface TextField {
        String getTheme();

        String render();

        String getValue();

        void setValue(String value);
    }

    interface Panel {
        String getTheme();

        String render();

        void addComponent(String component);

        List<String> getComponents();
    }

    // ===== Concrete Products: Light 테마 컴포넌트 =====

    static class LightButton implements Button {
        private final String label;
        private Runnable clickAction;

        LightButton(String label) {
            this.label = label;
        }

        @Override
        public String getTheme() {
            return "Light";
        }

        @Override
        public String render() {
            return String.format(
                    "<button style=\"background: #ffffff; color: #333333; border: 1px solid #cccccc\">%s</button>",
                    label);
        }

        @Override
        public void onClick(Runnable action) {
            this.clickAction = action;
            if (action != null) action.run();
        }
    }

    static class LightCheckbox implements Checkbox {
        private final String label;
        private boolean checked;

        LightCheckbox(String label) {
            this.label = label;
        }

        @Override
        public String getTheme() {
            return "Light";
        }

        @Override
        public String render() {
            String checkmark = checked ? "☑" : "☐";
            return String.format("<checkbox style=\"accent-color: #007bff\">%s %s</checkbox>", checkmark, label);
        }

        @Override
        public boolean isChecked() {
            return checked;
        }

        @Override
        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }

    static class LightTextField implements TextField {
        private final String placeholder;
        private String value = "";

        LightTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public String getTheme() {
            return "Light";
        }

        @Override
        public String render() {
            return String.format(
                    "<input style=\"background: #ffffff; border: 1px solid #cccccc\" placeholder=\"%s\" value=\"%s\"/>",
                    placeholder, value);
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }
    }

    static class LightPanel implements Panel {
        private final List<String> components = new ArrayList<>();

        @Override
        public String getTheme() {
            return "Light";
        }

        @Override
        public String render() {
            return String.format(
                    "<div style=\"background: #f5f5f5; padding: 20px\">%s</div>", String.join("\n", components));
        }

        @Override
        public void addComponent(String component) {
            components.add(component);
        }

        @Override
        public List<String> getComponents() {
            return List.copyOf(components);
        }
    }

    // ===== Concrete Products: Dark 테마 컴포넌트 =====

    static class DarkButton implements Button {
        private final String label;
        private Runnable clickAction;

        DarkButton(String label) {
            this.label = label;
        }

        @Override
        public String getTheme() {
            return "Dark";
        }

        @Override
        public String render() {
            return String.format(
                    "<button style=\"background: #333333; color: #ffffff; border: 1px solid #555555\">%s</button>",
                    label);
        }

        @Override
        public void onClick(Runnable action) {
            this.clickAction = action;
            if (action != null) action.run();
        }
    }

    static class DarkCheckbox implements Checkbox {
        private final String label;
        private boolean checked;

        DarkCheckbox(String label) {
            this.label = label;
        }

        @Override
        public String getTheme() {
            return "Dark";
        }

        @Override
        public String render() {
            String checkmark = checked ? "☑" : "☐";
            return String.format("<checkbox style=\"accent-color: #66b3ff\">%s %s</checkbox>", checkmark, label);
        }

        @Override
        public boolean isChecked() {
            return checked;
        }

        @Override
        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }

    static class DarkTextField implements TextField {
        private final String placeholder;
        private String value = "";

        DarkTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public String getTheme() {
            return "Dark";
        }

        @Override
        public String render() {
            return String.format(
                    "<input style=\"background: #2d2d2d; color: #ffffff; border: 1px solid #555555\" placeholder=\"%s\" value=\"%s\"/>",
                    placeholder, value);
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }
    }

    static class DarkPanel implements Panel {
        private final List<String> components = new ArrayList<>();

        @Override
        public String getTheme() {
            return "Dark";
        }

        @Override
        public String render() {
            return String.format(
                    "<div style=\"background: #1a1a1a; padding: 20px\">%s</div>", String.join("\n", components));
        }

        @Override
        public void addComponent(String component) {
            components.add(component);
        }

        @Override
        public List<String> getComponents() {
            return List.copyOf(components);
        }
    }

    // ===== Abstract Factory: UI 팩토리 인터페이스 =====

    interface UIFactory {
        Button createButton(String label);

        Checkbox createCheckbox(String label);

        TextField createTextField(String placeholder);

        Panel createPanel();

        default String getThemeName() {
            return createButton("").getTheme();
        }
    }

    // ===== Concrete Factories =====

    static class LightThemeFactory implements UIFactory {
        @Override
        public Button createButton(String label) {
            return new LightButton(label);
        }

        @Override
        public Checkbox createCheckbox(String label) {
            return new LightCheckbox(label);
        }

        @Override
        public TextField createTextField(String placeholder) {
            return new LightTextField(placeholder);
        }

        @Override
        public Panel createPanel() {
            return new LightPanel();
        }
    }

    static class DarkThemeFactory implements UIFactory {
        @Override
        public Button createButton(String label) {
            return new DarkButton(label);
        }

        @Override
        public Checkbox createCheckbox(String label) {
            return new DarkCheckbox(label);
        }

        @Override
        public TextField createTextField(String placeholder) {
            return new DarkTextField(placeholder);
        }

        @Override
        public Panel createPanel() {
            return new DarkPanel();
        }
    }

    @Nested
    class 테마별_UI_컴포넌트_생성 {

        @Test
        void Light_테마_컴포넌트를_생성한다() {
            UIFactory factory = new LightThemeFactory();

            Button button = factory.createButton("Submit");
            Checkbox checkbox = factory.createCheckbox("Remember me");
            TextField textField = factory.createTextField("Enter name");

            assertThat(button.getTheme()).isEqualTo("Light");
            assertThat(checkbox.getTheme()).isEqualTo("Light");
            assertThat(textField.getTheme()).isEqualTo("Light");
        }

        @Test
        void Dark_테마_컴포넌트를_생성한다() {
            UIFactory factory = new DarkThemeFactory();

            Button button = factory.createButton("Submit");
            Checkbox checkbox = factory.createCheckbox("Remember me");
            TextField textField = factory.createTextField("Enter name");

            assertThat(button.getTheme()).isEqualTo("Dark");
            assertThat(checkbox.getTheme()).isEqualTo("Dark");
            assertThat(textField.getTheme()).isEqualTo("Dark");
        }

        @Test
        void 테마별로_다른_스타일이_적용된다() {
            UIFactory lightFactory = new LightThemeFactory();
            UIFactory darkFactory = new DarkThemeFactory();

            String lightButtonHtml = lightFactory.createButton("Click").render();
            String darkButtonHtml = darkFactory.createButton("Click").render();

            // Light 테마는 밝은 배경
            assertThat(lightButtonHtml).contains("background: #ffffff");
            assertThat(lightButtonHtml).contains("color: #333333");

            // Dark 테마는 어두운 배경
            assertThat(darkButtonHtml).contains("background: #333333");
            assertThat(darkButtonHtml).contains("color: #ffffff");
        }
    }

    @Nested
    class 로그인_폼_구성_시나리오 {

        /**
         * 실무 시나리오: 로그인 폼을 테마에 맞게 구성
         */
        static class LoginForm {
            private final TextField usernameField;
            private final TextField passwordField;
            private final Checkbox rememberMeCheckbox;
            private final Button loginButton;
            private final Panel panel;

            LoginForm(UIFactory factory) {
                this.panel = factory.createPanel();
                this.usernameField = factory.createTextField("Username");
                this.passwordField = factory.createTextField("Password");
                this.rememberMeCheckbox = factory.createCheckbox("Remember me");
                this.loginButton = factory.createButton("Login");

                // 패널에 컴포넌트 추가
                panel.addComponent(usernameField.render());
                panel.addComponent(passwordField.render());
                panel.addComponent(rememberMeCheckbox.render());
                panel.addComponent(loginButton.render());
            }

            String getTheme() {
                return panel.getTheme();
            }

            String render() {
                return panel.render();
            }

            void setUsername(String username) {
                usernameField.setValue(username);
            }

            void setRememberMe(boolean remember) {
                rememberMeCheckbox.setChecked(remember);
            }
        }

        @Test
        void 팩토리만_교체하면_전체_폼의_테마가_바뀐다() {
            LoginForm lightForm = new LoginForm(new LightThemeFactory());
            LoginForm darkForm = new LoginForm(new DarkThemeFactory());

            assertThat(lightForm.getTheme()).isEqualTo("Light");
            assertThat(darkForm.getTheme()).isEqualTo("Dark");

            // Light 폼은 밝은 배경
            assertThat(lightForm.render()).contains("background: #f5f5f5");

            // Dark 폼은 어두운 배경
            assertThat(darkForm.render()).contains("background: #1a1a1a");
        }

        @Test
        void 폼_내_모든_컴포넌트가_일관된_테마를_가진다() {
            LoginForm form = new LoginForm(new DarkThemeFactory());

            String rendered = form.render();

            // 모든 컴포넌트가 Dark 테마 스타일
            assertThat(rendered).contains("background: #1a1a1a"); // Panel
            assertThat(rendered).contains("background: #2d2d2d"); // TextField
            assertThat(rendered).contains("accent-color: #66b3ff"); // Checkbox
        }
    }

    @Nested
    class 새로운_테마_추가_OCP {

        // 새로운 테마: High Contrast

        static class HighContrastButton implements Button {
            private final String label;

            HighContrastButton(String label) {
                this.label = label;
            }

            @Override
            public String getTheme() {
                return "HighContrast";
            }

            @Override
            public String render() {
                return String.format(
                        "<button style=\"background: #000000; color: #ffff00; border: 3px solid #ffff00; font-weight: bold\">%s</button>",
                        label);
            }

            @Override
            public void onClick(Runnable action) {
                if (action != null) action.run();
            }
        }

        static class HighContrastCheckbox implements Checkbox {
            private final String label;
            private boolean checked;

            HighContrastCheckbox(String label) {
                this.label = label;
            }

            @Override
            public String getTheme() {
                return "HighContrast";
            }

            @Override
            public String render() {
                String checkmark = checked ? "✓" : "○";
                return String.format(
                        "<checkbox style=\"color: #ffff00; font-size: 24px\">[%s] %s</checkbox>", checkmark, label);
            }

            @Override
            public boolean isChecked() {
                return checked;
            }

            @Override
            public void setChecked(boolean checked) {
                this.checked = checked;
            }
        }

        static class HighContrastTextField implements TextField {
            private final String placeholder;
            private String value = "";

            HighContrastTextField(String placeholder) {
                this.placeholder = placeholder;
            }

            @Override
            public String getTheme() {
                return "HighContrast";
            }

            @Override
            public String render() {
                return String.format(
                        "<input style=\"background: #000000; color: #ffff00; border: 3px solid #ffff00\" placeholder=\"%s\" value=\"%s\"/>",
                        placeholder, value);
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public void setValue(String value) {
                this.value = value;
            }
        }

        static class HighContrastPanel implements Panel {
            private final List<String> components = new ArrayList<>();

            @Override
            public String getTheme() {
                return "HighContrast";
            }

            @Override
            public String render() {
                return String.format(
                        "<div style=\"background: #000000; padding: 20px; border: 3px solid #ffffff\">%s</div>",
                        String.join("\n", components));
            }

            @Override
            public void addComponent(String component) {
                components.add(component);
            }

            @Override
            public List<String> getComponents() {
                return List.copyOf(components);
            }
        }

        static class HighContrastThemeFactory implements UIFactory {
            @Override
            public Button createButton(String label) {
                return new HighContrastButton(label);
            }

            @Override
            public Checkbox createCheckbox(String label) {
                return new HighContrastCheckbox(label);
            }

            @Override
            public TextField createTextField(String placeholder) {
                return new HighContrastTextField(placeholder);
            }

            @Override
            public Panel createPanel() {
                return new HighContrastPanel();
            }
        }

        @Test
        void 새로운_테마를_기존_코드_수정_없이_추가할_수_있다() {
            // 기존 Light, Dark 팩토리는 전혀 수정하지 않음
            UIFactory highContrastFactory = new HighContrastThemeFactory();

            Button button = highContrastFactory.createButton("Accept");
            Checkbox checkbox = highContrastFactory.createCheckbox("I agree");

            assertThat(button.getTheme()).isEqualTo("HighContrast");
            assertThat(checkbox.getTheme()).isEqualTo("HighContrast");

            // High Contrast 스타일 적용 확인
            assertThat(button.render()).contains("background: #000000");
            assertThat(button.render()).contains("color: #ffff00");
        }

        @Test
        void 새_테마도_기존_클라이언트_코드와_호환된다() {
            // LoginForm은 UIFactory만 의존
            LoginForm accessibleForm = new LoginForm(new HighContrastThemeFactory());

            assertThat(accessibleForm.getTheme()).isEqualTo("HighContrast");
            assertThat(accessibleForm.render()).contains("border: 3px solid");
        }
    }

    @Nested
    class 테마_전환_시나리오 {
        // 런타임에 테마 전환
        static class ThemeManager {
            private UIFactory currentFactory;

            ThemeManager(UIFactory initialFactory) {
                this.currentFactory = initialFactory;
            }

            void switchTheme(UIFactory newFactory) {
                this.currentFactory = newFactory;
            }

            UIFactory getCurrentFactory() {
                return currentFactory;
            }

            String getCurrentTheme() {
                return currentFactory.getThemeName();
            }
        }

        @Test
        void 런타임에_테마를_전환할_수_있다() {
            ThemeManager manager = new ThemeManager(new LightThemeFactory());
            assertThat(manager.getCurrentTheme()).isEqualTo("Light");

            // 테마 전환
            manager.switchTheme(new DarkThemeFactory());
            assertThat(manager.getCurrentTheme()).isEqualTo("Dark");

            // 새 팩토리로 컴포넌트 생성
            Button button = manager.getCurrentFactory().createButton("Test");
            assertThat(button.getTheme()).isEqualTo("Dark");
        }

        @Test
        void 시스템_설정에_따라_테마를_선택할_수_있다() {
            // 상황 설정: 시스템 시간에 따라 테마 선택
            boolean isDarkModeTime = java.time.LocalTime.now().getHour() >= 18;

            UIFactory factory = isDarkModeTime ? new DarkThemeFactory() : new LightThemeFactory();

            Button button = factory.createButton("Auto Theme");

            // 시간에 따라 적절한 테마 적용
            assertThat(button.getTheme()).isIn("Light", "Dark");
        }
    }

    @Nested
    class 크로스_플랫폼_시나리오 {

        /**
         * 확장: 플랫폼별 + 테마별 제품군
         * Web Light, Web Dark, Mobile Light, Mobile Dark...
         *
         * 이 경우 Abstract Factory를 계층화할 수 있음
         */
        interface PlatformUIFactory {
            UIFactory createLightTheme();

            UIFactory createDarkTheme();
        }

        static class WebPlatformFactory implements PlatformUIFactory {
            @Override
            public UIFactory createLightTheme() {
                return new LightThemeFactory();
            }

            @Override
            public UIFactory createDarkTheme() {
                return new DarkThemeFactory();
            }
        }

        // Mobile은 다른 스타일의 컴포넌트를 생성할 수 있음
        static class MobilePlatformFactory implements PlatformUIFactory {
            @Override
            public UIFactory createLightTheme() {
                // 실제로는 MobileLightThemeFactory 반환
                return new LightThemeFactory(); // 간단히 재사용
            }

            @Override
            public UIFactory createDarkTheme() {
                return new DarkThemeFactory();
            }
        }

        @Test
        void 플랫폼과_테마_조합으로_팩토리를_선택할_수_있다() {
            PlatformUIFactory platformFactory = new WebPlatformFactory();

            UIFactory lightFactory = platformFactory.createLightTheme();
            UIFactory darkFactory = platformFactory.createDarkTheme();

            assertThat(lightFactory.getThemeName()).isEqualTo("Light");
            assertThat(darkFactory.getThemeName()).isEqualTo("Dark");
        }
    }

    @Nested
    class 테스트_용이성 {

        /**
         * 테스트를 위한 Mock 팩토리
         */
        static class TestUIFactory implements UIFactory {
            private int buttonCount = 0;
            private int checkboxCount = 0;
            private int textFieldCount = 0;

            @Override
            public Button createButton(String label) {
                buttonCount++;
                return new LightButton(label); // 간단히 Light 재사용
            }

            @Override
            public Checkbox createCheckbox(String label) {
                checkboxCount++;
                return new LightCheckbox(label);
            }

            @Override
            public TextField createTextField(String placeholder) {
                textFieldCount++;
                return new LightTextField(placeholder);
            }

            @Override
            public Panel createPanel() {
                return new LightPanel();
            }

            // 테스트 검증용 메서드
            int getButtonCount() {
                return buttonCount;
            }

            int getCheckboxCount() {
                return checkboxCount;
            }

            int getTextFieldCount() {
                return textFieldCount;
            }
        }

        @Test
        void Mock_팩토리로_컴포넌트_생성_횟수를_검증할_수_있다() {
            TestUIFactory testFactory = new TestUIFactory();

            // LoginForm이 어떤 컴포넌트를 몇 개 생성하는지 검증
            LoginForm form = new LoginForm(testFactory);

            assertThat(testFactory.getButtonCount()).isEqualTo(1);
            assertThat(testFactory.getCheckboxCount()).isEqualTo(1);
            assertThat(testFactory.getTextFieldCount()).isEqualTo(2);
        }
    }
}
