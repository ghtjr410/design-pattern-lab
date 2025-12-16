package s01_creational.factory_method;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Factory Method 패턴 - 기본 구조 학습 테스트
 *
 * 핵심 의도: 객체 생성을 서브클래스에 위임하여, 어떤 클래스의 인스턴스를 생성할지를
 *          서브클래스가 결정하게 한다.
 *
 * 구조:
 * - Product: 생성될 객체의 인터페이스
 * - ConcreteProduct: Product 인터페이스의 구현체
 * - Creator: factoryMethod()를 선언하는 추상 클래스
 * - ConcreteCreator: factoryMethod()를 구현하여 ConcreteProduct를 생성
 *
 * 핵심 포인트:
 * 1. Creator는 Product를 "사용"하는 코드를 가지고 있다
 * 2. Creator는 Product를 "생성"하는 방법을 모른다 (서브클래스에 위임)
 * 3. 이를 통해 생성과 사용의 관심사를 분리한다
 */
public class FactoryMethodBasic {

    // ===== Product 인터페이스 =====
    interface Document {
        String getType();

        String render();
    }

    // ===== Concrete Products =====
    static class PdfDocument implements Document {
        private final String content;

        PdfDocument(String content) {
            this.content = content;
        }

        @Override
        public String getType() {
            return "PDF";
        }

        @Override
        public String render() {
            return "[PDF] " + content;
        }
    }

    static class WordDocument implements Document {
        private final String content;

        WordDocument(String content) {
            this.content = content;
        }

        @Override
        public String getType() {
            return "Word";
        }

        @Override
        public String render() {
            return "[Word] " + content;
        }
    }

    static class HtmlDocument implements Document {
        private final String content;

        HtmlDocument(String content) {
            this.content = content;
        }

        @Override
        public String getType() {
            return "HTML";
        }

        @Override
        public String render() {
            return "<html>" + content + "</html>";
        }
    }

    // ===== Creator (추상 클래스) =====
    abstract static class DocumentCreator {

        /**
         * Factory Method - 서브클래스가 구현
         * 어떤 Document를 생성할지는 서브클래스가 결정
         */
        protected abstract Document createDocument(String content);

        /**
         * Template Method 패턴과 결합된 형태
         * Document를 생성하고 "사용"하는 로직은 여기에 있다
         */
        public String createAndRender(String content) {
            Document document = createDocument(content); // Factory Method 호출
            return document.render();
        }

        /**
         * 생성된 Document에 대한 추가 작업
         */
        public Document createWithMetadata(String content, String author) {
            Document document = createDocument(content);
            // 실제로는 메타데이터를 설정하는 로직이 들어갈 수 있음
            return document;
        }
    }

    // ===== Concrete Creators =====
    static class PdfDocumentCreator extends DocumentCreator {
        @Override
        protected Document createDocument(String content) {
            return new PdfDocument(content);
        }
    }

    static class WordDocumentCreator extends DocumentCreator {
        @Override
        protected Document createDocument(String content) {
            return new WordDocument(content);
        }
    }

    static class HtmlDocumentCreator extends DocumentCreator {
        @Override
        protected Document createDocument(String content) {
            return new HtmlDocument(content);
        }
    }

    @Nested
    class 팩토리_메서드_핵심_구조 {

        @Test
        void Creator는_Product_생성을_서브클래스에_위임한다() {
            DocumentCreator pdfCreator = new PdfDocumentCreator();
            DocumentCreator wordCreator = new WordDocumentCreator();

            Document pdfDoc = pdfCreator.createDocument("Hello");
            Document wordDoc = wordCreator.createDocument("Hello");

            assertThat(pdfDoc.getType()).isEqualTo("PDF");
            assertThat(wordDoc.getType()).isEqualTo("Word");
        }

        @Test
        void Creator는_Product를_사용하는_로직을_가진다() {
            DocumentCreator pdfCreator = new PdfDocumentCreator();

            // Creator는 createDocument()로 어떤 객체가 생성되는지 모르지만
            // 생성된 객체를 "사용"하는 방법은 알고 있다
            String result = pdfCreator.createAndRender("Report Content");

            assertThat(result).isEqualTo("[PDF] Report Content");
        }

        @Test
        void 동일한_Creator_로직이_다른_Product에_적용된다() {
            DocumentCreator pdfCreator = new PdfDocumentCreator();
            DocumentCreator htmlCreator = new HtmlDocumentCreator();
            String content = "Same Content";

            // 동일한 createAndRender() 메서드지만, 결과가 다름
            String pdfResult = pdfCreator.createAndRender(content);
            String htmlResult = htmlCreator.createAndRender(content);

            assertThat(pdfResult).isEqualTo("[PDF] Same Content");
            assertThat(htmlResult).isEqualTo("<html>Same Content</html>");
        }
    }
}
