package io.github.jokoframework.report.printer;

import java.nio.charset.StandardCharsets;

/**
 * @author ncanatta
 */
public class EscPUtil {

    public static final int MAX_PAGE_LENGTH = 127;
    public static final int MAX_PAGE_WIDTH = 255;

    public static final int ESC_CODE = 27;
    public static final int COMMAND_INITIALIZE = 64;
    public static final int COMMAND_ONE_PER_SIX_INCH_LINE_SPACING = 50;
    public static final int COMMAND_ONE_PER_EIGHT_LINE_SPACING = 48;
    public static final int COMMAND_N72_LINE_SPACING = 65;
    public static final int COMMAND_N216_LINE_SPACING = 51;
    public static final int COMMAND_MASTER_SELECT = 33;
    public static final int COMMAND_PAGE_LENGTH = 67;
    public static final int COMMAND_RIGHT_MARGIN = 81;
    public static final int COMMAND_LEFT_MARGIN = 108;
    public static final int COMMAND_BOTTOM_MARGIN = 78;
    public static final int COMMAND_SELECT_TYPEFACE = 107;
    public static final int COMMAND_SELECT_BOLD = 69;
    public static final int COMMAND_CANCEL_BOLD = 70;
    public static final int COMMAND_SELECT_ITALIC = 52;
    public static final int COMMAND_CANCEL_ITALIC = 53;
    public static final int COMMAND_SELECT_DOUBLESTRIKE = 71;
    public static final int COMMAND_CANCEL_DOUBLESTRIKE = 72;
    public static final int COMMAND_SELECT_UNDERLINE = 45;
    public static final int COMMAND_CANCEL_UNDERLINE = 45;
    public static final int COMMAND_SELECT_SUBSCRIPT = 83;
    public static final int COMMAND_SELECT_SUPERSCRIPT = 83;
    public static final int COMMAND_CANCEL_SUBSCRIPT_OR_SUPERSCRIPT = 84;
    public static final int COMMAND_12_CPI = 77;
    public static final int COMMAND_LETTER_QUALITY_SELECT = 120;

    public static final char CP347_LIGHT_VERTICAL = (char) 179;
    public static final char CP347_LIGHT_DOWN_RIGHT = (char) 218;
    public static final char CP347_LIGHT_HORIZONTAL = (char) 196;
    public static final char CP347_LIGHT_DOWN_LEFT = (char) 191;
    public static final char CP347_LIGHT_DOWN_HORIZONTAL = (char) 194;
    public static final char CP347_LIGHT_VERTICAL_RIGHT = (char) 195;
    public static final char CP347_LIGHT_VERTICAL_HORIZONTAL = (char) 197;
    public static final char CP347_LIGHT_VERTICAL_LEFT = (char) 180;
    public static final char CP347_LIGHT_UP_RIGHT = (char) 192;
    public static final char CP347_LIGHT_UP_HORIZONTAL = (char) 193;
    public static final char CP347_LIGHT_UP_LEFT = (char) 217;

    public static final String CR = new String(new byte[]{13}, StandardCharsets.US_ASCII);
    public static final String CRLF = new String(new byte[]{13, 10}, StandardCharsets.US_ASCII);
    public static final String CRFF = new String(new byte[]{13, 12}, StandardCharsets.US_ASCII);

    private String validationMessagePageLength = "Invalid value for page length:  %d  (valid: 1 to %d )";
    private String getValidationMessagePageLeftMargin = "Invalid value for left margin: %d (valid: 1 to %d )";

    private StringBuilder result;

    public EscPUtil() {
        this.result = new StringBuilder();
    }

    public static EscPUtil getInstance() {
        EscPUtil escPUtil = new EscPUtil();
        escPUtil.escInitalize();
        return escPUtil;
    }

    public static EscPUtil getCleanInstance() {
        return new EscPUtil();
    }

    public void clear() {
        this.result = new StringBuilder();
    }

    /**
     * Add text to builder. Useful for building reports or adding generated reports after ESC/P commands
     *
     * @param text a String value as parameter
     * @return updated EscPUtil instance.
     */
    public EscPUtil addText(String text) {
        this.result.append(text);
        return this;
    }

    /**
     * Return the result string with ESC/P commands
     *
     * @return String with ESC/P commands
     */
    public String getResult() {
        return this.result.toString();
    }

    /**
     * Create an ESC/P code and adds ESC/P in form of a string to string builder
     *
     * @param command an ASCII number that represent ESC/P command.
     * @param value   an ASCII number value as parameters for specified command.
     * @return updated EscPUtil instance.
     */
    public EscPUtil esc(int command, int... value) {
        this.result.append((char) ESC_CODE);
        this.result.append((char) command);
        if (value != null) {
            for (int i : value) {
                this.result.append((char) i);
            }
        }
        return this;
    }

    /**
     * Create an ESC/P code and adds ESC/P in form of a string to string builder
     *
     * @param command an ASCII number that represent ESC/P command.
     * @return updated EscPUtil instance.
     */
    public EscPUtil esc(int command) {
        return esc(command, null);
    }

    /**
     * Generate ESC @ command.  Use this command to initialized printer.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escInitalize() {
        return esc(COMMAND_INITIALIZE);
    }

    /**
     * Generate ESC 2 to select 1/6-inch line spacing.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escOnePerSixInchLineSpacing() {
        return esc(COMMAND_ONE_PER_SIX_INCH_LINE_SPACING);
    }

    /**
     * Generate ESC 0 to select 1/8-inch line spacing.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escOnePerEightInchLineSpacing() {
        return esc(COMMAND_ONE_PER_EIGHT_LINE_SPACING);
    }

    /**
     * Generate ESC 0 to select n/72-inch line spacing.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escN72InchLineSpacing(int n) {
        return esc(COMMAND_N72_LINE_SPACING, n);
    }

    /**
     * Generate ESC 0 to select n/216-inch line spacing.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escN216InchLineSpacing(int n) {
        return esc(COMMAND_N216_LINE_SPACING, n);
    }


    /**
     * Generate ESC ! for master select.
     *
     * @param characterPitch parameter for ESC !
     * @return updated EscPUtil instance.
     */
    public EscPUtil escMasterSelect(CHARACTER_PITCH characterPitch) {
        return esc(COMMAND_MASTER_SELECT, characterPitch.getValue());
    }

    /**
     * Generate ESC ! for print quality select.
     *
     * @param printQuality parameter for ESC !
     * @return updated EscPUtil instance.
     */
    public EscPUtil escPrintQualitySelect(PRINT_QUALITY printQuality) {
        return esc(COMMAND_LETTER_QUALITY_SELECT, printQuality.getValue());
    }

    /**
     * Generate ESC ! for 12 cpi select
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil esc12CpiSelect() {
        return esc(COMMAND_12_CPI);
    }


    /**
     * Generate ESC C for setting page length.
     *
     * <p>Note that some printer drivers will ignore this value and use the page length setting
     * stored in printer's ROM.  For example, in Epson LX-310, you can change page length by
     * pressing <em>LF/FF</em> and <em>Load/Eject</em> button in the same time.
     *
     * @param value number of lines in 1 to 127 lines.
     * @return updated EscPUtil instance.
     */
    public EscPUtil escPageLength(int value) {
        if ((value < 1) || (value > MAX_PAGE_LENGTH)) {
            throw new IllegalArgumentException(String.format(validationMessagePageLength, value, MAX_PAGE_LENGTH));
        }
        return esc(COMMAND_PAGE_LENGTH, value);
    }

    /**
     * Generate ESC l for setting left margin.
     *
     * @param value number of characters in 1 to 255 character per line.
     * @return updated EscPUtil instance.
     */
    public EscPUtil escLeftMargin(int value) {
        if ((value < 1) || (value > MAX_PAGE_WIDTH)) {
            throw new IllegalArgumentException(String.format(getValidationMessagePageLeftMargin, value, MAX_PAGE_WIDTH));
        }
        return esc(COMMAND_LEFT_MARGIN, value);
    }

    /**
     * Generate ESC Q for setting right margin.
     *
     * @param value number of characters in 1 to 255 character per line.
     * @return updated EscPUtil instance.
     */
    public EscPUtil escRightMargin(int value) {
        if ((value < 1) || (value > MAX_PAGE_WIDTH)) {
            throw new IllegalArgumentException("Invalid value for right margin: " + value + " (valid: 1 to " +
                    MAX_PAGE_WIDTH + ")");
        }
        return esc(COMMAND_RIGHT_MARGIN, value);
    }

    /**
     * Generate ESC N for setting bottom margin.
     *
     * @param value number of lines from top-of-form position.  Valid values is in range of 1 to 127 lines.
     * @return updated EscPUtil instance.
     */
    public EscPUtil escBottomMargin(int value) {
        if ((value < 1) || (value > MAX_PAGE_LENGTH)) {
            throw new IllegalArgumentException("Invalid value for bottom margin: " + value + " (valid: 1 to " +
                    MAX_PAGE_LENGTH + ")");
        }
        return esc(COMMAND_BOTTOM_MARGIN, value);
    }

    /**
     * Generate ESC k for select typeface.
     *
     * @param typeface a typeface to select.
     * @return updated EscPUtil instance.
     */
    public EscPUtil escSelectTypeface(TYPEFACE typeface) {
        return esc(COMMAND_SELECT_TYPEFACE, typeface.getValue());
    }

    /**
     * Generate ESC E for select bold font.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escSelectBoldFont() {
        return esc(COMMAND_SELECT_BOLD);
    }

    /**
     * Generate ESC F for cancel bold font.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escCancelBoldFont() {
        return esc(COMMAND_CANCEL_BOLD);
    }

    /**
     * Generate ESC 4 for select italic font.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escSelectItalicFont() {
        return esc(COMMAND_SELECT_ITALIC);
    }

    /**
     * Generate ESC 5 to cancel italic font.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escCancelItalicFont() {
        return esc(COMMAND_CANCEL_ITALIC);
    }

    /**
     * Generate ESC G to select double-strike printing.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escSelectDoubleStrikeFont() {
        return esc(COMMAND_SELECT_DOUBLESTRIKE);
    }

    /**
     * Generate ESC H to cancel double-strike printing.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escCancelDoubleStrikeFont() {
        return esc(COMMAND_CANCEL_DOUBLESTRIKE);
    }

    /**
     * Generate ESC - 1 to turn underline on.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escSelectUnderline() {
        return esc(COMMAND_SELECT_UNDERLINE, 1);
    }

    /**
     * Generate ESC - 0 to turn underline off.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escCancelUnderline() {
        return esc(COMMAND_CANCEL_UNDERLINE, 0);
    }

    /**
     * Generate ESC S 1 for subscript printing.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escSelectSubscript() {
        return esc(COMMAND_SELECT_SUBSCRIPT, 1);
    }

    /**
     * Generate ESC S 0 for superscript printing.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escSelectSuperscript() {
        return esc(COMMAND_SELECT_SUPERSCRIPT, 0);
    }

    /**
     * Generate ESC T to cancel subscript or superscript printing.
     *
     * @return updated EscPUtil instance.
     */
    public EscPUtil escCancelSuperscriptOrSubscript() {
        return esc(COMMAND_CANCEL_SUBSCRIPT_OR_SUPERSCRIPT);
    }


    public enum PRINT_QUALITY {
        DRAFT(0), LQ(1);

        private int value;

        /**
         * Create new instance of CHARACTER_PITCH.
         *
         * @param value a Master Select command's parameter to select this CPI.
         */
        PRINT_QUALITY(int value) {
            this.value = value;
        }

        /**
         * Get the parameter value for this CPI.
         *
         * @return a parameter for Master Select command.
         */
        public int getValue() {
            return value;
        }
    }

    /**
     * This enum represents available character pitchs.
     */
    public enum CHARACTER_PITCH {
        CPI_5(32), CPI_6(33), CPI_10(0), CPI_12(1), CPI_17(4), CPI_20(5);

        private int value;

        /**
         * Create new instance of CHARACTER_PITCH.
         *
         * @param value a Master Select command's parameter to select this CPI.
         */
        CHARACTER_PITCH(int value) {
            this.value = value;
        }

        /**
         * Get the parameter value for this CPI.
         *
         * @return a parameter for Master Select command.
         */
        public int getValue() {
            return value;
        }
    }

    /**
     * This enum represents available typeface.
     */
    public enum TYPEFACE {
        ROMAN(0), SANS_SERIF(1);

        private int value;

        /**
         * Create new instance of TYPEFACE.
         *
         * @param value a parameter for Select type face command.
         */
        TYPEFACE(int value) {
            this.value = value;
        }

        /**
         * Get the parameter value for this typeface.
         *
         * @return a parameter for Select type face command.
         */
        public int getValue() {
            return value;
        }
    }
}
