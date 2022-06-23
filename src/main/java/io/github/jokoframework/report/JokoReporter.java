package io.github.jokoframework.report;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import io.github.jokoframework.report.exception.JokoReportException;
import io.github.jokoframework.report.exception.WebClientErrorListener;
import io.github.jokoframework.report.printer.ESCPrinter;
import io.github.jokoframework.report.tools.ReportTools;
import lombok.Getter;
import lombok.Setter;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.ToolManager;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
public class JokoReporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JokoReporter.class);
    public static final String VELOCITY_TOOLS_CONFIG_PATH = "/tools/velocity-tools.xml";
    public static final String TOOLS = "Tools";
    public static final String PARAMS = "Params";
    public static final String STRING = "String";
    public static final String ZONE_ID = "ZoneId";
    public static final String ESCP = "Escp";
    private Context context;
    private Template template;
    private ReportTools reportTools;
    private ESCPrinter escPrinter;
    private StringWriter writer;
    private boolean esc24pin = false;
    private static final String CONTEXT_NULL_ERROR = "context.null.error";


    public JokoReporter() {
        //empty constructor
    }

    public JokoReporter(boolean esc24pin) {
        this.esc24pin = esc24pin;
    }

    public static JokoReporter buildInstance(String reportTemplatePath, boolean esc24pin, Object params) {
        JokoReporter jokoReporter = new JokoReporter(esc24pin);
        jokoReporter.initializeContext(reportTemplatePath, params);
        return jokoReporter;
    }

    public static JokoReporter buildInstance(String reportTemplatePath, Object params) {
        JokoReporter jokoReporter = new JokoReporter();
        jokoReporter.initializeContext(reportTemplatePath, params);
        return jokoReporter;
    }

    public static JokoReporter buildInstance(ESCPrinter escPrinter, boolean esc24pin) {
        JokoReporter jokoReporter = new JokoReporter(esc24pin);
        jokoReporter.setEscPrinter(escPrinter);
        return jokoReporter;
    }

    public static JokoReporter buildInstance(boolean esc24pin) {
        return new JokoReporter(esc24pin);
    }

    public void setEscPrinter(ESCPrinter escPrinter) {
        if (escPrinter != null) {
            this.escPrinter = escPrinter;
        }
        if (this.escPrinter != null && this.getContext() != null) {
            this.getContext().put(ESCP, this.escPrinter);
        }

    }

    public ESCPrinter getEscPrinter() {
        if (this.escPrinter == null) {
            this.setEscPrinter(new ESCPrinter(esc24pin));
        }
        return this.escPrinter;
    }

    /**
     * Initialize the apache velocity engine and configures report context tools
     *
     * @param reportTemplatePath
     * @param params
     */
    public void initializeContext(String reportTemplatePath, Object params) {
        // Initializing velocity engine
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
        velocityEngine.init();

        // Initializing template from path
        this.setTemplate(velocityEngine.getTemplate(reportTemplatePath));

        // Configuring context with the standard velocity generic tools
        ToolManager toolManager = new ToolManager(false, true);
        toolManager.configure(VELOCITY_TOOLS_CONFIG_PATH);
        toolManager.setVelocityEngine(velocityEngine);
        this.setContext(toolManager.createContext());

        // Configuring context with the custom joko report tools
        this.setReportTools(new ReportTools());
        this.getContext().put(PARAMS, params);
        this.getContext().put(STRING, String.class);
        this.getContext().put(ZONE_ID, ZoneId.class);
        this.updateReportTools();
        this.setEscPrinter(new ESCPrinter(esc24pin));
    }

    /**
     * Updates the {@link ReportTools} instance in the velocity context
     */
    private void updateReportTools() {
        this.getContext().put(TOOLS, this.getReportTools());
    }

    public void configDecimalFormatter(String format) {
        this.getReportTools().decimalFormat.applyPattern(format);
        this.updateReportTools();
    }

    public void configLocale(String language, String country) {
        this.getReportTools().setLocale(new Locale(language, country));
        this.updateReportTools();
    }

    public StringWriter buildReport(int copyNumber) throws JokoReportException {
        this.getContext().put("copyNumber", copyNumber);
        return this.buildReport();
    }

    /**
     * Process the template with the context params to build the report output
     *
     * @return
     */
    public StringWriter buildReport() throws JokoReportException {
        if (this.getContext() == null) {
            throw new JokoReportException("context.null", "Context is null. Please initialize context first");
        }
        // If writer is not null report is already build
        if (writer == null) {
            writer = new StringWriter();
            this.getTemplate().merge(this.getContext(), writer);
        }
        return writer;
    }

    /**
     * Retrieves the report output as String
     * param escEnabled
     *
     * @return
     */
    public String getAsString(boolean escEnabled) {
        try {
            this.buildReport();
        } catch (JokoReportException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return escEnabled ? this.escPrinter.getAsString() : this.writer.toString();
    }


    /**
     * Retrieves the report output as bytes
     *
     * @return
     */
    public byte[] getEscBytes() {
        try {
            this.buildReport();
        } catch (JokoReportException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return this.escPrinter.getAsBytes();
    }

    /**
     * Retrieves the report output as list of Hex
     *
     * @return
     */
    public List<String> getEncodedList() {
        try {
            this.buildReport();
        } catch (JokoReportException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return this.escPrinter.getEncodedList();
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
    public static ByteArrayOutputStream generatePDFFromHTML(String html) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        File file = createTmpFile(html);
        WebRequest webRequest = new WebRequest(file.toURI().toURL());
        webRequest.setCharset(StandardCharsets.UTF_8);

        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.setJavaScriptErrorListener(new WebClientErrorListener());

            HtmlPage page = webClient.getPage(webRequest);
            Document doc = Jsoup.parse(page.asXml());
            outputStream = getPDFStream(new W3CDom().fromJsoup(doc), file.getAbsolutePath());
        }
        return outputStream;
    }

    private static ByteArrayOutputStream getPDFStream(org.w3c.dom.Document htmlDoc, String uri) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withW3cDocument(htmlDoc, uri);
        builder.toStream(outputStream);
        builder.run();
        return outputStream;
    }

    /**
     * Generates a pdf byte based on a given html String
     *
     * @return
     */
    public byte[] getPDFAsByte(String html) throws IOException {
        return generatePDFFromHTML(html).toByteArray();
    }
}
