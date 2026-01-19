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
}
