package io.github.jokoframework.report;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.itextpdf.html2pdf.HtmlConverter;
import io.github.jokoframework.report.exception.WebClientErrorListener;
import io.github.jokoframework.report.printer.EscP2Tool;
import io.github.jokoframework.report.utils.ReportTools;
import lombok.Getter;
import lombok.Setter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Locale;

@Getter
@Setter
public class JokoReporter {

    public static final String TOOLS = "Tools";
    public static final String PARAMS = "Params";
    public static final String STRING = "String";
    public static final String ZONE_ID = "ZoneId";
    public static final String ESC_P2 = "EscP2";
    private VelocityContext context;
    private Template template;
    private ReportTools reportTools;

    public static JokoReporter buildInstance(String reportTemplatePath, Object params) {
        JokoReporter jokoReporter = new JokoReporter();
        jokoReporter.buildContext(reportTemplatePath, params);
        return jokoReporter;
    }

    /**
     * Initialize the apache velocity engine and configures report context tools
     *
     * @param reportTemplatePath
     * @param params
     */
    public void buildContext(String reportTemplatePath, Object params) {
        // Initializing velocity engine
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
        velocityEngine.init();

        // Initializing template from path
        this.setTemplate(velocityEngine.getTemplate(reportTemplatePath));

        // Configuring context
        this.setContext(new VelocityContext());
        this.setReportTools(new ReportTools());

        this.getContext().put(PARAMS, params);
        this.getContext().put(STRING, String.class);
        this.getContext().put(ZONE_ID, ZoneId.class);
        this.getContext().put(TOOLS, this.getReportTools());
        this.getContext().put(ESC_P2, new EscP2Tool());
    }

    public void configDecimalFormatter(String format) {
        this.getReportTools().decimalFormat().applyPattern(format);
        context.put(TOOLS, this.getReportTools());
    }

    public void configLocale(String language, String country) {
        this.getReportTools().setLocale(new Locale(language, country));
        context.put(TOOLS, this.getReportTools());
    }

    public StringWriter buildReport(int copyNumber) {
        this.getContext().put("copyNumber", copyNumber);
        return this.buildReport();
    }

    /**
     * Process the template with the context params to build the report output
     *
     * @return
     */
    public StringWriter buildReport() {
        StringWriter writer = new StringWriter();
        this.getTemplate().merge(this.getContext(), writer);
        return writer;
    }

    /**
     * Retrieves the report output as String
     *
     * @return
     */
    public String buildReportOutput() {
        return this.buildReport().toString();
    }

    public String buildReportOutput(int copyNumber) {
        return this.buildReport(copyNumber).toString();
    }

    /**
     * Creates a temporary html file based with a given html String as the content of the file
     *
     * @return
     */
    private static File createTmpFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("report-", ".html");
        File file = tempFile.toFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        } catch (Exception e) {
            throw new IOException(e);
        }
        return file;
    }

    /**
     * Generates a pdf byte array based on a given html String
     *
     * @return
     */
    public static ByteArrayOutputStream generatePDFFromHTML(String html, boolean enableBlocks) throws IOException {
        ByteArrayOutputStream outputStream = new
                ByteArrayOutputStream();

        String content = html;
        if (enableBlocks) {
            File file = createTmpFile(html);
            WebRequest webRequest = new WebRequest(file.toURI().toURL());
            webRequest.setCharset(StandardCharsets.UTF_8);

            try (WebClient webClient = new WebClient()) {
                webClient.getOptions().setJavaScriptEnabled(true);
                webClient.setJavaScriptErrorListener(new WebClientErrorListener());

                HtmlPage page = webClient.getPage(webRequest);
                Document doc = Jsoup.parse(page.asXml());

                content = doc.html();
            }
        }

        try {
            HtmlConverter.convertToPdf(content, outputStream);
        } catch (Exception e) {
            throw new IOException(e);
        }

        return outputStream;
    }

    /**
     * Generates a pdf byte based on a given html String
     *
     * @return
     */
    public byte[] getAsByte(String html) throws IOException {
        return generatePDFFromHTML(html, true).toByteArray();
    }
}
