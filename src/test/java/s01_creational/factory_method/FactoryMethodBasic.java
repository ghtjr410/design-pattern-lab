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

    @Nested
    class OCP_준수_검증 {

        // 새로운 Product 추가
        static class MarkdownDocument implements Document {
            private final String content;

            MarkdownDocument(String content) {
                this.content = content;
            }

            @Override
            public String getType() {
                return "Markdown";
            }

            @Override
            public String render() {
                return "# " + content;
            }
        }

        // 새로운 Creator 추가 - 기존 코드 수정 없음
        static class MarkdownDocumentCreator extends DocumentCreator {
            @Override
            protected Document createDocument(String content) {
                return new MarkdownDocument(content);
            }
        }

        @Test
        void 새로운_타입_추가시_기존_코드를_수정하지_않는다() {
            // 기존 Creator들은 전혀 수정되지 않음
            DocumentCreator markdownCreator = new MarkdownDocumentCreator();

            String result = markdownCreator.createAndRender("New Feature");

            assertThat(result).isEqualTo("# New Feature");
        }

        @Test
        void 새로운_Creator도_기존_템플릿_로직을_그대로_사용한다() {
            DocumentCreator markdownCreator = new MarkdownDocumentCreator();

            // createWithMetadata는 DocumentCreator에 정의된 메서드
            // 새로운 MarkdownDocumentCreator도 이 로직을 그대로 사용
            Document doc = markdownCreator.createWithMetadata("Content", "Author");

            assertThat(doc.getType()).isEqualTo("Markdown");
        }
    }

    @Nested
    class 단순_팩토리와의_차이 {

        /**
         * Simple Factory (단순 팩토리) - 패턴이 아닌 관용구
         * 조건문으로 객체 생성을 분기 처리
         */
        enum DocumentType {
            PDF,
            WORD,
            HTML
        }

        static class SimpleDocumentFactory {
            public static Document create(DocumentType type, String content) {
                return switch (type) {
                    case PDF -> new PdfDocument(content);
                    case WORD -> new WordDocument(content);
                    case HTML -> new HtmlDocument(content);
                };
            }
        }

        @Test
        void 단순_팩토리는_조건문으로_분기한다() {
            Document pdf = SimpleDocumentFactory.create(DocumentType.PDF, "Content");
            Document word = SimpleDocumentFactory.create(DocumentType.WORD, "Content");

            assertThat(pdf.getType()).isEqualTo("PDF");
            assertThat(word.getType()).isEqualTo("Word");
        }

        @Test
        void 단순_팩토리는_새_타입_추가시_기존_코드를_수정해야_한다() {
            /*
             * 단순 팩토리의 한계:
             * - Markdown 타입을 추가하려면 SimpleDocumentFactory의 switch문을 수정해야 함
             * - OCP 위반: 확장을 위해 기존 코드를 수정
             *
             * Factory Method 패턴:
             * - 새로운 Creator 클래스만 추가하면 됨
             * - 기존 코드 수정 없음 (OCP 준수)
             */

            // 단순 팩토리: switch문 수정 필요
            // Factory Method: 새 클래스 추가만 필요

            DocumentCreator creator = new PdfDocumentCreator();
            assertThat(creator.createDocument("test")).isInstanceOf(PdfDocument.class);
        }

        @Test
        void 팩토리_메서드는_Creator_레벨에서_추가_로직을_가질_수_있다() {
            /*
             * Factory Method의 장점:
             * Creator가 단순히 객체를 생성하는 것을 넘어서
             * 생성 전후의 추가 로직을 가질 수 있다
             */
            DocumentCreator creator = new PdfDocumentCreator();

            // createAndRender는 생성 + 사용을 조합한 로직
            // 단순 팩토리로는 이런 조합이 어렵다
            String result = creator.createAndRender("Content");

            assertThat(result).contains("[PDF]");
        }
    }

    @Nested
    class 다형성_활용 {

        @Test
        void Creator를_다형적으로_사용할_수_있다() {
            DocumentCreator[] creators = {new PdfDocumentCreator(), new WordDocumentCreator(), new HtmlDocumentCreator()
            };

            // 클라이언트 코드는 구체적인 Creator 타입을 몰라도 됨
            for (DocumentCreator creator : creators) {
                Document doc = creator.createDocument("Test");
                assertThat(doc.render()).isNotEmpty();
            }
        }

        @Test
        void 런타임에_Creator를_교체할_수_있다() {
            DocumentCreator creator = new PdfDocumentCreator();
            assertThat(creator.createDocument("A").getType()).isEqualTo("PDF");

            // 런타임에 다른 Creator로 교체
            creator = new WordDocumentCreator();
            assertThat(creator.createDocument("A").getType()).isEqualTo("Word");
        }
    }

    @Nested
    class Parameterized_Factory_Method_안티패턴 {

        /**
         * 안티패턴 예시
         *
         * 문제점:
         * 1. 타입 추가 시 switch문 수정 필요
         * 2. 타입 추가 시 추상 메서드 추가 필요
         * 3. 타입 추가 시 모든 서브클래스 수정 필요
         * → OCP 3중 위반
         */
        abstract static class FlexibleDocumentCreator {

            public Document createDocument(String type, String content) {
                return switch (type) {
                    case "PDF" -> createPdfDocument(content);
                    case "Word" -> createWordDocument(content);
                    default -> createDefaultDocument(content);
                };
            }

            // 서브클래스가 각 타입별 생성 방식을 커스터마이징 가능
            protected abstract Document createPdfDocument(String content);

            protected abstract Document createWordDocument(String content);

            protected abstract Document createDefaultDocument(String content);
        }

        static class StandardDocumentCreator extends FlexibleDocumentCreator {
            @Override
            protected Document createPdfDocument(String content) {
                return new PdfDocument(content);
            }

            @Override
            protected Document createWordDocument(String content) {
                return new WordDocument(content);
            }

            @Override
            protected Document createDefaultDocument(String content) {
                return new HtmlDocument(content);
            }
        }

        @Test
        void 매개변수로_생성할_타입을_결정할_수_있다() {
            FlexibleDocumentCreator creator = new StandardDocumentCreator();

            Document pdf = creator.createDocument("PDF", "Content");
            Document word = creator.createDocument("Word", "Content");
            Document defaultDoc = creator.createDocument("Unknown", "Content");

            assertThat(pdf.getType()).isEqualTo("PDF");
            assertThat(word.getType()).isEqualTo("Word");
            assertThat(defaultDoc.getType()).isEqualTo("HTML");
        }
    }
}
