package io.github.jokoframework.report.component;

import io.github.jokoframework.report.JokoReporter;
import io.github.jokoframework.report.PrintAssistant;
import io.github.jokoframework.report.exception.JokoReportException;
import io.github.jokoframework.report.printer.ESCPrinter;
import org.cups4j.CupsPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.print.PrintService;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author ncanatta
 * @since 05/04/2022
 */
@Component
public class JokoReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(JokoReport.class);

    @Value("${joko.report.cups.server.url:localhost}")
    private String cupsServerUrl;

    @Value("${joko.report.cups.server.port:631}")
    private int cupsServerPort;

    /**
     * Retrieves all printers from cups server using the <strong>cusps4j</strong> library.
     * Uses the properties <strong>joko.report.cups.server.url</strong> and <strong>joko.report.cups.server.port</strong>
     * to configure the server connection. Default is <strong>localhost:631</strong> respectively
     * @return
     */
    public List<CupsPrinter> findAllPrintersFromServer(){
        LOGGER.debug("Retrieving all printers from: {}:{}", cupsServerUrl, cupsServerPort);
        return PrintAssistant.findAllPrinterServices(cupsServerUrl, cupsServerPort);
    }

    /**
     * Retrieves a printer by name from cups server using the <strong>cusps4j</strong> library.
     * Uses the properties <strong>joko.report.cups.server.url</strong> and <strong>joko.report.cups.server.port</strong>
     * to configure the server connection. Default is <strong>localhost:631</strong> respectively
     * @return
     */
    public CupsPrinter findPrinterByNameFromServer(String name){
        LOGGER.debug("Retrieving printer {} from: {}:{}", name, cupsServerUrl, cupsServerPort);
        return PrintAssistant.findPrinterByName(name, cupsServerUrl, cupsServerPort);
    }

    /**
     * Retrieves a printer by name from the operative system service using the java
     * <strong>java.awt.print.PrinterJob.lookupPrintServices()</strong> method.
     * @return
     */
    public PrintService findPrinterByNameFromOS(String name){
        LOGGER.debug("Retrieving printer {} from OS", name);
        return PrintAssistant.findPrintService(name);
    }

    public JokoReporter newJokoReporter(String templatePath, Object params) {
        return JokoReporter.buildInstance(templatePath, params);
    }

    public JokoReporter newJokoReporter(String templatePath, Object params, ESCPrinter escPrinter) {
        JokoReporter jokoReporter = JokoReporter.buildInstance(templatePath, params);
        jokoReporter.setEscPrinter(escPrinter);
        return jokoReporter;
    }

    public JokoReporter newJokoReporter(ESCPrinter escPrinter) {
        return JokoReporter.buildInstance(escPrinter, false);
    }

    public JokoReporter newJokoReporter() {
        return JokoReporter.buildInstance(false);
    }

    public ESCPrinter newEscPrinter(boolean esc24pin) {
        return new ESCPrinter(esc24pin);
    }

    public void printOnMatrixPrinter(CupsPrinter printer, String templatePath, Object params) throws JokoReportException {
        JokoReporter jokoReporter = newJokoReporter(templatePath, params);
        byte[] reportOutput = jokoReporter.getEscBytes();
        PrintAssistant.printOnMatrixPrinter(printer, reportOutput);
    }

    public void printOnMatrixPrinter(CupsPrinter printer, JokoReporter jokoReporter) throws JokoReportException {
        byte[] reportOutput = jokoReporter.getEscBytes();
        PrintAssistant.printOnMatrixPrinter(printer, reportOutput);
    }

    public void printAsPDF(CupsPrinter printer, String templatePath, Object params) throws JokoReportException {
        JokoReporter jokoReporter = JokoReporter.buildInstance(templatePath, params);
        String reportOutput = jokoReporter.getAsString(false);
        PrintAssistant.printAsPDF(printer, reportOutput, true);
    }

    public byte[] getPDFAsByte(String templatePath, Object params) throws IOException {
        JokoReporter jokoReporter = newJokoReporter(templatePath, params);
        String reportOutput = jokoReporter.getAsString(false);
        return jokoReporter.getPDFAsByte(reportOutput);
    }

    public ResponseEntity<byte[]> getPDFAsResponseEntity(String templatePath, Object params, String fileName) throws IOException {
        byte[] pdf = getPDFAsByte(templatePath, params);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, MessageFormat.format("attachment;filename={0}.pdf", fileName));
        return new ResponseEntity<>(pdf, httpHeaders, HttpStatus.OK);
    }

}
