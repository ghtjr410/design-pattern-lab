package s01_creational.abstract_factory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Abstract Factory vs Factory Method 비교 학습 테스트
 *
 * 핵심 차이:
 * - Factory Method: 하나의 객체 생성을 서브클래스에 위임
 * - Abstract Factory: 관련된 객체 군(family)을 생성하는 인터페이스 제공
 *
 * 선택 기준:
 * - 단일 객체 생성 + 생성 후 처리 로직 → Factory Method
 * - 여러 관련 객체의 일관성 있는 생성 → Abstract Factory
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AbstractFactoryCompareTest {

    // Factory Method 방식

    /**
     * Factory Method: 하나의 Product 생성에 집중
     * Creator가 생성 후 처리 로직(template)을 가짐
     */
    interface Document {
        String getFormat();

        String render(String content);
    }

    static class PdfDocument implements Document {
        @Override
        public String getFormat() {
            return "PDF";
        }

        @Override
        public String render(String content) {
            return "[PDF] " + content;
        }
    }

    static class HtmlDocument implements Document {
        @Override
        public String getFormat() {
            return "HTML";
        }

        @Override
        public String render(String content) {
            return "<html>" + content + "</html>";
        }
    }

    /**
     * Factory Method 패턴의 Creator
     * - 단일 Product(Document) 생성
     * - 생성 후 처리 로직을 포함 (export 메서드)
     */
    abstract static class DocumentExporter {

        // Factory Method
        protected abstract Document createDocument();

        // Template Method - 생성 후 처리 로직
        public String export(String content) {
            Document doc = createDocument();
            String rendered = doc.render(content);
            return addHeader(doc.getFormat()) + rendered + addFooter();
        }

        private String addHeader(String format) {
            return "=== " + format + " Export ===\n";
        }

        private String addFooter() {
            return "\n=== End ===";
        }
    }

    static class PdfExporter extends DocumentExporter {
        @Override
        protected Document createDocument() {
            return new PdfDocument();
        }
    }

    static class HtmlExporter extends DocumentExporter {
        @Override
        protected Document createDocument() {
            return new HtmlDocument();
        }
    }

    // Abstract Factory 방식

    /**
     * Abstract Factory: 관련 객체 군 생성
     * 문서 스위트: Document + Editor + Viewer
     */
    interface DocSuiteDocument {
        String getFormat();

        String getContent();

        void setContent(String content);
    }

    interface DocSuiteEditor {
        String getFormat();

        void edit(DocSuiteDocument document, String newContent);

        String getEditorName();
    }

    interface DocSuiteViewer {
        String getFormat();

        String view(DocSuiteDocument document);

        String getViewerName();
    }

    // PDF 제품군
    static class PdfSuiteDocument implements DocSuiteDocument {
        private String content = "";

        @Override
        public String getFormat() {
            return "PDF";
        }

        @Override
        public String getContent() {
            return content;
        }

        @Override
        public void setContent(String content) {
            this.content = content;
        }
    }

    static class PdfSuiteEditor implements DocSuiteEditor {
        @Override
        public String getFormat() {
            return "PDF";
        }

        @Override
        public void edit(DocSuiteDocument document, String newContent) {
            if (!"PDF".equals(document.getFormat())) {
                throw new IllegalArgumentException("PDF Editor can only edit PDF documents");
            }
            document.setContent(newContent);
        }

        @Override
        public String getEditorName() {
            return "Adobe Acrobat";
        }
    }

    static class PdfSuiteViewer implements DocSuiteViewer {
        @Override
        public String getFormat() {
            return "PDF";
        }

        @Override
        public String view(DocSuiteDocument document) {
            return "[PDF Viewer] " + document.getContent();
        }

        @Override
        public String getViewerName() {
            return "PDF Reader";
        }
    }

    // HTML 제품군
    static class HtmlSuiteDocument implements DocSuiteDocument {
        private String content = "";

        @Override
        public String getFormat() {
            return "HTML";
        }

        @Override
        public String getContent() {
            return content;
        }

        @Override
        public void setContent(String content) {
            this.content = content;
        }
    }

    static class HtmlSuiteEditor implements DocSuiteEditor {
        @Override
        public String getFormat() {
            return "HTML";
        }

        @Override
        public void edit(DocSuiteDocument document, String newContent) {
            if (!"HTML".equals(document.getFormat())) {
                throw new IllegalArgumentException("HTML Editor can only edit HTML documents");
            }
            document.setContent("<p>" + newContent + "</p>");
        }

        @Override
        public String getEditorName() {
            return "VS Code";
        }
    }

    static class HtmlSuiteViewer implements DocSuiteViewer {
        @Override
        public String getFormat() {
            return "HTML";
        }

        @Override
        public String view(DocSuiteDocument document) {
            return "<browser>" + document.getContent() + "</browser>";
        }

        @Override
        public String getViewerName() {
            return "Chrome Browser";
        }
    }

    /**
     * Abstract Factory: 제품군 전체를 생성
     */
    interface DocumentSuiteFactory {
        DocSuiteDocument createDocument();

        DocSuiteEditor createEditor();

        DocSuiteViewer createViewer();
    }

    static class PdfSuiteFactory implements DocumentSuiteFactory {
        @Override
        public DocSuiteDocument createDocument() {
            return new PdfSuiteDocument();
        }

        @Override
        public DocSuiteEditor createEditor() {
            return new PdfSuiteEditor();
        }

        @Override
        public DocSuiteViewer createViewer() {
            return new PdfSuiteViewer();
        }
    }

    static class HtmlSuiteFactory implements DocumentSuiteFactory {
        @Override
        public DocSuiteDocument createDocument() {
            return new HtmlSuiteDocument();
        }

        @Override
        public DocSuiteEditor createEditor() {
            return new HtmlSuiteEditor();
        }

        @Override
        public DocSuiteViewer createViewer() {
            return new HtmlSuiteViewer();
        }
    }

    @Nested
    class 생성_대상의_차이 {

        @Test
        void Factory_Method는_단일_객체를_생성한다() {
            DocumentExporter exporter = new PdfExporter();

            // 하나의 Document만 생성
            String result = exporter.export("Hello");

            assertThat(result).contains("[PDF]");
        }

        @Test
        void Abstract_Factory는_관련_객체_군을_생성한다() {
            DocumentSuiteFactory factory = new PdfSuiteFactory();

            // 관련된 여러 객체를 생성
            DocSuiteDocument document = factory.createDocument();
            DocSuiteEditor editor = factory.createEditor();
            DocSuiteViewer viewer = factory.createViewer();

            // 모두 같은 제품군(PDF)
            assertThat(document.getFormat()).isEqualTo("PDF");
            assertThat(editor.getFormat()).isEqualTo("PDF");
            assertThat(viewer.getFormat()).isEqualTo("PDF");
        }
    }

    @Nested
    class 구조적_차이 {

        @Test
        void Factory_Method는_상속_기반이다() {
            /*
             * Factory Method 구조:
             *
             * DocumentExporter (abstract)
             *   └── createDocument() : abstract  ← Factory Method
             *   └── export()         : concrete  ← Template Method (생성 후 로직)
             *         │
             *         ├── PdfExporter
             *         │     └── createDocument() → new PdfDocument()
             *         │
             *         └── HtmlExporter
             *               └── createDocument() → new HtmlDocument()
             *
             * 특징:
             * - Creator(DocumentExporter)가 추상 클래스
             * - 서브클래스가 생성 방식을 결정
             * - Creator가 생성 후 처리 로직(export)을 가짐
             */

            // 서브클래스 타입으로 인스턴스화
            DocumentExporter pdfExporter = new PdfExporter();
            DocumentExporter htmlExporter = new HtmlExporter();

            // 같은 export() 메서드지만 다른 Document 생성
            assertThat(pdfExporter.export("test")).contains("[PDF]");
            assertThat(htmlExporter.export("test")).contains("<html>");
        }

        @Test
        void Abstract_Factory는_조합_기반이다() {
            /*
             * Abstract Factory 구조:
             *
             * DocumentSuiteFactory (interface)
             *   └── createDocument()
             *   └── createEditor()
             *   └── createViewer()
             *         │
             *         ├── PdfSuiteFactory → PdfDocument, PdfEditor, PdfViewer
             *         │
             *         └── HtmlSuiteFactory → HtmlDocument, HtmlEditor, HtmlViewer
             *
             * 특징:
             * - Factory가 인터페이스 (또는 추상 클래스)
             * - 제품군 전체를 생성하는 메서드들
             * - 클라이언트가 Factory를 조합하여 사용
             */

            // 인터페이스 타입으로 사용
            DocumentSuiteFactory factory = new PdfSuiteFactory();

            // Factory를 클라이언트에 주입하여 사용
            DocSuiteDocument doc = factory.createDocument();
            DocSuiteEditor editor = factory.createEditor();

            editor.edit(doc, "Hello");
            assertThat(doc.getContent()).isEqualTo("Hello");
        }
    }

    @Nested
    class 확장_방식의_차이 {

        @Test
        void Factory_Method에서_새_제품_추가는_새_서브클래스_생성() {
            /*
             * Markdown Document 추가:
             *
             * 1. MarkdownDocument implements Document
             * 2. MarkdownExporter extends DocumentExporter
             *    └── createDocument() → new MarkdownDocument()
             *
             * 기존 코드 수정 없음 (OCP 준수)
             */

            // 새 제품 타입
            class MarkdownDocument implements Document {
                @Override
                public String getFormat() {
                    return "MD";
                }

                @Override
                public String render(String content) {
                    return "# " + content;
                }
            }

            // 새 Creator
            class MarkdownExporter extends DocumentExporter {
                @Override
                protected Document createDocument() {
                    return new MarkdownDocument();
                }
            }

            DocumentExporter mdExporter = new MarkdownExporter();
            assertThat(mdExporter.export("Title")).contains("# Title");
        }

        @Test
        void Abstract_Factory에서_새_제품군_추가는_새_Factory_생성() {
            /*
             * Markdown Suite 추가:
             *
             * 1. MarkdownDocument, MarkdownEditor, MarkdownViewer 구현
             * 2. MarkdownSuiteFactory implements DocumentSuiteFactory
             *
             * 기존 코드 수정 없음 (OCP 준수)
             */

            // 새 제품군 (간단히 구현)
            class MarkdownSuiteFactory implements DocumentSuiteFactory {
                @Override
                public DocSuiteDocument createDocument() {
                    return new DocSuiteDocument() {
                        private String content = "";

                        @Override
                        public String getFormat() {
                            return "Markdown";
                        }

                        @Override
                        public String getContent() {
                            return content;
                        }

                        @Override
                        public void setContent(String content) {
                            this.content = content;
                        }
                    };
                }

                @Override
                public DocSuiteEditor createEditor() {
                    return new DocSuiteEditor() {
                        @Override
                        public String getFormat() {
                            return "Markdown";
                        }

                        @Override
                        public void edit(DocSuiteDocument document, String newContent) {
                            document.setContent("# " + newContent);
                        }

                        @Override
                        public String getEditorName() {
                            return "Typora";
                        }
                    };
                }

                @Override
                public DocSuiteViewer createViewer() {
                    return new DocSuiteViewer() {
                        @Override
                        public String getFormat() {
                            return "Markdown";
                        }

                        @Override
                        public String view(DocSuiteDocument document) {
                            return "[MD Preview] " + document.getContent();
                        }

                        @Override
                        public String getViewerName() {
                            return "Markdown Preview";
                        }
                    };
                }
            }

            DocumentSuiteFactory mdFactory = new MarkdownSuiteFactory();
            assertThat(mdFactory.createDocument().getFormat()).isEqualTo("Markdown");
            assertThat(mdFactory.createEditor().getEditorName()).isEqualTo("Typora");
        }

        @Test
        void Abstract_Factory에서_새_제품_타입_추가는_모든_Factory_수정_필요() {
            /*
             * Abstract Factory의 단점:
             *
             * DocumentSuiteFactory에 createPrinter() 추가하려면:
             *
             * 1. DocSuitePrinter 인터페이스 정의
             * 2. DocumentSuiteFactory에 createPrinter() 메서드 추가 ← 인터페이스 수정!
             * 3. PdfSuiteFactory에 createPrinter() 구현 ← 구현체 수정!
             * 4. HtmlSuiteFactory에 createPrinter() 구현 ← 구현체 수정!
             *
             * → 모든 Factory 수정 필요 (OCP 위반)
             */

            // 이것이 Abstract Factory의 트레이드오프
        }
    }

    @Nested
    class 클라이언트_코드_차이 {

        @Test
        void Factory_Method_클라이언트는_Creator를_사용한다() {
            // 클라이언트는 Creator(Exporter)의 메서드를 호출
            DocumentExporter exporter = new PdfExporter();

            // Creator가 제공하는 비즈니스 메서드 사용
            String result = exporter.export("Content");

            assertThat(result).contains("PDF Export");
        }

        @Test
        void Abstract_Factory_클라이언트는_Factory에서_제품을_받아_사용한다() {
            // 클라이언트가 직접 제품을 받아서 조합
            DocumentSuiteFactory factory = new HtmlSuiteFactory();

            DocSuiteDocument doc = factory.createDocument();
            DocSuiteEditor editor = factory.createEditor();
            DocSuiteViewer viewer = factory.createViewer();

            // 클라이언트가 제품들을 조합하여 사용
            editor.edit(doc, "Hello World");
            String result = viewer.view(doc);

            assertThat(result).contains("Hello World");
        }
    }

    @Nested
    class 실무_선택_가이드 {

        @Test
        void 단일_객체_생성_후_처리_로직이_있으면_Factory_Method() {
            /*
             * Factory Method 선택 상황:
             *
             * 1. 생성할 객체가 하나뿐
             * 2. 객체 생성 후 공통 처리 로직이 있음 (Template Method와 결합)
             * 3. Creator가 비즈니스 로직의 주체
             *
             * 예시:
             * - DocumentExporter: 문서 생성 + 내보내기
             * - NotificationService: 알림 생성 + 발송
             * - ReportGenerator: 리포트 생성 + 포맷팅
             */

            DocumentExporter exporter = new PdfExporter();
            String result = exporter.export("Report");

            // Creator가 생성 + 처리를 모두 담당
            assertThat(result).contains("=== PDF Export ===");
            assertThat(result).contains("[PDF]");
            assertThat(result).contains("=== End ===");
        }

        @Test
        void 관련_객체들의_일관성이_중요하면_Abstract_Factory() {
            /*
             * Abstract Factory 선택 상황:
             *
             * 1. 여러 관련 객체가 함께 사용됨
             * 2. 객체들 간의 일관성이 중요 (같은 테마, 같은 포맷 등)
             * 3. 제품군 전환이 자주 발생
             *
             * 예시:
             * - UI 테마: Button + Checkbox + TextField (같은 테마)
             * - 문서 스위트: Document + Editor + Viewer (같은 포맷)
             * - DB 연결: Connection + Statement + ResultSet (같은 벤더)
             */

            DocumentSuiteFactory factory = new PdfSuiteFactory();

            DocSuiteDocument doc = factory.createDocument();
            DocSuiteEditor editor = factory.createEditor();
            DocSuiteViewer viewer = factory.createViewer();

            // 제품군 일관성 보장
            assertThat(doc.getFormat()).isEqualTo(editor.getFormat());
            assertThat(editor.getFormat()).isEqualTo(viewer.getFormat());
        }

        @Test
        void 두_패턴을_함께_사용할_수도_있다() {
            /*
             * 복합 사용 예시:
             *
             * Abstract Factory로 제품군 생성
             * + Factory Method로 각 제품의 생성 로직 캡슐화
             *
             * AbstractFactory
             *   └── createProductA() ← 내부적으로 Factory Method 사용 가능
             *   └── createProductB()
             */

            // Abstract Factory 내부에서 Factory Method 사용
            abstract class ConfigurableFactory implements DocumentSuiteFactory {
                // Factory Method - 서브클래스가 구현
                protected abstract DocSuiteDocument doCreateDocument();

                @Override
                public DocSuiteDocument createDocument() {
                    DocSuiteDocument doc = doCreateDocument();
                    // 공통 초기화 로직
                    doc.setContent("");
                    return doc;
                }
            }
        }
    }
}
