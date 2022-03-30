package io.github.jokoframework.report;

import io.github.jokoframework.report.exception.JokoPrintException;
import io.github.jokoframework.report.printer.EscPUtil;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.PrintService;
import javax.print.attribute.Attribute;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class PrintAssistant {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintAssistant.class);
    private static final String PRINTER_ERROR = "printer.error";

    private PrintAssistant() {
        throw new IllegalStateException("Print Utility class. Not meant to be instantiated.");
    }

    /**
     * Find a printer service from OS service given the printer name
     *
     * @param printerName
     * @return
     */
    public static PrintService findPrintService(String printerName) {

        PrintService service = null;

        // Get array of all print services - sort order NOT GUARANTEED!
        PrintService[] services = PrinterJob.lookupPrintServices();
        // Retrieve specified print service from the array
        if (services == null || services.length == 0) {
            LOGGER.warn("No printer services found");
        }
        for (PrintService pService : services) {
            LOGGER.debug("Service: {}", pService.getName());
            if (pService.getName().equalsIgnoreCase(printerName)) {
                service = pService;
                queryPrinter(service);
                break;
            }
        }
        // Return the print service
        return service;
    }

    /**
     * Find a printer from a Cups server given the printer name
     *
     * @param printerName
     * @param serverHost
     * @param serverPort
     * @return
     */
    public static CupsPrinter findPrinterByName(String printerName, String serverHost, int serverPort) {
        try {
            CupsPrinter cupsPrintService = null;
            List<CupsPrinter> printers = findAllPrinterServices(serverHost, serverPort);
            if (printers == null || printers.isEmpty()) {
                throw new JokoPrintException("Cant list Printers");
            }
            for (CupsPrinter cupsPrinter : printers) {
                LOGGER.debug("Service: {}", cupsPrinter.getName());
                if (cupsPrinter.getName().equalsIgnoreCase(printerName)) {
                    cupsPrintService = cupsPrinter;
                    break;
                }
            }
            return cupsPrintService;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Retrieves all printers from a Cups server
     *
     * @param serverHost
     * @param serverPort
     * @return
     */
    public static List<CupsPrinter> findAllPrinterServices(String serverHost, int serverPort) {
        try {
            CupsClient client = new CupsClient(serverHost, serverPort);

            return client.getPrintersWithoutDefault();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    // List details about the named printer
    public static void queryPrinter(PrintService service) {
        Attribute[] attrs = service.getAttributes().toArray();
        for (int i = 0; i < attrs.length; i++) {
            LOGGER.debug("{} - {}: ", attrs[i].getName(), attrs[i]);
        }
    }

    public static EscPUtil getDefaultEscPCommands() {
        EscPUtil escPUtil = EscPUtil.getInstance();
        // Add the ESC/P commands for font type and quality
        escPUtil.escSelectTypeface(EscPUtil.TYPEFACE.SANS_SERIF).escPrintQualitySelect(EscPUtil.PRINT_QUALITY.LQ);
        return escPUtil;
    }

    public static PrintRequestResult printOnMatrixPrinter(CupsPrinter cupsPrinterService, String reportOutput, EscPUtil escPUtil) throws JokoPrintException {
        try {
            if (escPUtil == null) {
                escPUtil = getDefaultEscPCommands();
            }
            List<String> listMimeTypes = new ArrayList<>();
            listMimeTypes.add("application/octet-stream");
            cupsPrinterService.setMimeTypesSupported(listMimeTypes);

            // Add the text to print
            escPUtil.addText(reportOutput);

            ByteArrayInputStream inputStream = new
                    ByteArrayInputStream(escPUtil.getResult().getBytes());

            Map<String, String> attributes = new HashMap<>();
            attributes.put("compression", "none");

            PrintJob printJob = new PrintJob.Builder(inputStream)
                    .copies(1)
                    .duplex(false)
                    .portrait(false)
                    .color(true)
                    .pageFormat("na_letter")
                    .attributes(attributes)
                    .build();
            PrintRequestResult result = cupsPrinterService.print(printJob);
            LOGGER.debug("cups result: {}", result.getResultMessage());
            return result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new JokoPrintException(PRINTER_ERROR);
        }
    }

    public static void printAsPDF(CupsPrinter cupsPrinterService, String reportOutput, Boolean enableBlocks)
            throws JokoPrintException {
        try {

            ByteArrayOutputStream output = JokoReporter.generatePDFFromHTML(reportOutput, enableBlocks);
            List<String> listMimeTypes = new ArrayList<>();
            listMimeTypes.add("application/pdf");
            List<String> listResolution = new ArrayList<>();
            listResolution.add("300dpi");
            Map<String, String> attributes = new HashMap<>();
            attributes.put("compression", "none");
            cupsPrinterService.setMimeTypesSupported(listMimeTypes);
            cupsPrinterService.setResolutionSupported(listResolution);

            PrintJob printJob = new PrintJob.Builder(output.toByteArray())
                    .copies(1)
                    .duplex(false)
                    .portrait(false)
                    .color(true)
                    .pageFormat("na_letter")
                    .attributes(attributes)
                    .build();
            PrintRequestResult result = cupsPrinterService.print(printJob);

            LOGGER.debug("Cups result print: {}", result.getResultMessage());

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new JokoPrintException(PRINTER_ERROR);
        }
    }
}
