package s01_creational.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Builder 패턴 - GoF 원형 (문서 변환 예제)
 *
 * 복잡한 입력을 파싱하면서 여러 출력 형식을 지원해야 할 때
 *
 * 문제 상황:
 * - RTF 문서를 HTML, Plain Text, Markdown 등으로 변환해야 함
 * - RTF 파싱 로직은 복잡함 (100줄+)
 * - 출력 형식마다 파싱 로직을 중복 작성하고 싶지 않음
 *
 * 해결:
 * - Director(RtfReader): 파싱 로직 담당 (한 번만 작성)
 * - Builder: 출력 생성 담당 (형식별로 작성)
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BuilderGofTest {

    // ===== Builder 인터페이스 =====
    interface DocumentBuilder {
        void reset();

        void addText(String text);

        void addBold(String text);

        void addItalic(String text);

        void addImage(String path);

        void addLineBreak();
    }

    // ===== ConcreteBuilder: HTML =====
    static class HtmlBuilder implements DocumentBuilder {
        private StringBuilder html;

        public HtmlBuilder() {
            reset();
        }

        @Override
        public void reset() {
            html = new StringBuilder();
            html.append("<html><body>");
        }

        @Override
        public void addText(String text) {
            html.append(text);
        }

        @Override
        public void addBold(String text) {
            html.append("<b>").append(text).append("</b>");
        }

        @Override
        public void addItalic(String text) {
            html.append("<i>").append(text).append("</i>");
        }

        @Override
        public void addImage(String path) {
            html.append("<img src=\"").append(path).append("\">");
        }

        @Override
        public void addLineBreak() {
            html.append("<br>");
        }

        public String getResult() {
            return html.toString() + "</body></html>";
        }
    }

    // ===== ConcreteBuilder: Plain Text =====
    static class PlainTextBuilder implements DocumentBuilder {
        private StringBuilder text;

        public PlainTextBuilder() {
            reset();
        }

        @Override
        public void reset() {
            text = new StringBuilder();
        }

        @Override
        public void addText(String content) {
            text.append(content);
        }

        @Override
        public void addBold(String content) {
            text.append(content); // 스타일 무시
        }

        @Override
        public void addItalic(String content) {
            text.append(content); // 스타일 무시
        }

        @Override
        public void addImage(String path) {
            text.append("[이미지: ").append(path).append("]");
        }

        @Override
        public void addLineBreak() {
            text.append("\n");
        }

        public String getResult() {
            return text.toString();
        }
    }

    // ===== ConcreteBuilder: Markdown =====
    static class MarkdownBuilder implements DocumentBuilder {
        private StringBuilder md;

        public MarkdownBuilder() {
            reset();
        }

        @Override
        public void reset() {
            md = new StringBuilder();
        }

        @Override
        public void addText(String text) {
            md.append(text);
        }

        @Override
        public void addBold(String text) {
            md.append("**").append(text).append("**");
        }

        @Override
        public void addItalic(String text) {
            md.append("_").append(text).append("_");
        }

        @Override
        public void addImage(String path) {
            md.append("![image](").append(path).append(")");
        }

        @Override
        public void addLineBreak() {
            md.append("\n\n");
        }

        public String getResult() {
            return md.toString();
        }
    }

    // ===== Director: RTF 파서 =====
    static class RtfReader {

        /**
         * RTF 파싱 로직 (실제로는 100줄+ 복잡한 로직)
         * 이 로직을 HTML용, Text용, Markdown용으로 3번 쓰기 싫다!
         */
        public void parse(String rtfContent, DocumentBuilder builder) {
            builder.reset();

            // ===== 복잡한 파싱 로직 (간략화) =====
            // 실제로는 RTF 토큰 분석, 스타일 스택 관리,
            // 이스케이프 처리, 인코딩 변환 등 복잡한 로직...

            // RTF에서 "Hello"를 발견
            builder.addText("Hello ");

            // RTF에서 볼드 스타일 발견: {\b World}
            builder.addBold("World");

            // RTF에서 줄바꿈 발견: \par
            builder.addLineBreak();

            // RTF에서 이탤릭 발견: {\i Welcome}
            builder.addItalic("Welcome");

            // RTF에서 이미지 발견: {\pict ...}
            builder.addImage("photo.png");

            // ===== 파싱 로직 끝 =====
        }
    }

    // ===== 테스트 =====

    @Nested
    class 핵심_같은_파싱_로직으로_다른_출력 {

        @Test
        void RTF를_HTML로_변환한다() {
            RtfReader reader = new RtfReader();
            HtmlBuilder builder = new HtmlBuilder();

            reader.parse("rtf content...", builder);

            String html = builder.getResult();
            assertThat(html).contains("<b>World</b>");
            assertThat(html).contains("<i>Welcome</i>");
            assertThat(html).contains("<img src=\"photo.png\">");
        }

        @Test
        void RTF를_PlainText로_변환한다() {
            RtfReader reader = new RtfReader();
            PlainTextBuilder builder = new PlainTextBuilder();

            reader.parse("rtf content...", builder);

            String text = builder.getResult();
            assertThat(text).contains("Hello World"); // 스타일 제거됨
            assertThat(text).contains("[이미지: photo.png]");
            assertThat(text).doesNotContain("<b>"); // HTML 태그 없음
        }

        @Test
        void RTF를_Markdown으로_변환한다() {
            RtfReader reader = new RtfReader();
            MarkdownBuilder builder = new MarkdownBuilder();

            reader.parse("rtf content...", builder);

            String md = builder.getResult();
            assertThat(md).contains("**World**");
            assertThat(md).contains("_Welcome_");
            assertThat(md).contains("![image](photo.png)");
        }

        @Test
        void 파싱_로직은_하나_출력만_다르다() {
            RtfReader reader = new RtfReader(); // 파싱 로직 1개

            // 같은 파싱 로직으로 3가지 출력
            HtmlBuilder htmlBuilder = new HtmlBuilder();
            PlainTextBuilder textBuilder = new PlainTextBuilder();
            MarkdownBuilder mdBuilder = new MarkdownBuilder();

            reader.parse("rtf content...", htmlBuilder);
            reader.parse("rtf content...", textBuilder);
            reader.parse("rtf content...", mdBuilder);

            // 같은 "World"가 다르게 표현됨
            assertThat(htmlBuilder.getResult()).contains("<b>World</b>");
            assertThat(textBuilder.getResult()).contains("World");
            assertThat(mdBuilder.getResult()).contains("**World**");
        }
    }

    @Nested
    class Builder_패턴이_없었다면 {

        @Test
        void 변환기마다_파싱_로직을_중복_작성해야_한다() {
            /*
             * Builder 패턴 없이:
             *
             * class RtfToHtmlConverter {
             *     String convert(String rtf) {
             *         // RTF 파싱 로직 100줄...
             *         // HTML 생성 로직...
             *     }
             * }
             *
             * class RtfToTextConverter {
             *     String convert(String rtf) {
             *         // RTF 파싱 로직 100줄... (중복!)
             *         // Text 생성 로직...
             *     }
             * }
             *
             * class RtfToMarkdownConverter {
             *     String convert(String rtf) {
             *         // RTF 파싱 로직 100줄... (또 중복!)
             *         // Markdown 생성 로직...
             *     }
             * }
             *
             * 문제:
             * 1. 파싱 로직 3번 중복
             * 2. 파싱 버그 수정하면 3군데 수정
             * 3. 새 형식 추가하면 또 파싱 로직 복사
             */
        }

        @Test
        void Builder_패턴으로_해결() {
            /*
             * Builder 패턴 적용:
             *
             * RtfReader (파싱 로직 1번만 작성)
             *     ↓
             * DocumentBuilder 인터페이스
             *     ↓
             * HtmlBuilder / TextBuilder / MarkdownBuilder (출력만 담당)
             *
             * 장점:
             * 1. 파싱 로직 1번만 작성
             * 2. 새 형식 추가 = Builder만 추가
             * 3. 파싱 버그 수정 = 1군데만
             */
        }
    }
}
