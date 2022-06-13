/*
    Copyright (c) 2006-2007, Giovanni Martina
    All rights reserved.
    Redistribution and use in source and binary forms, with or without modification, are permitted provided that
    the following conditions are met:
    - Redistributions of source code must retain the above copyright notice, this list of conditions and the
    following disclaimer.
    - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other materials provided with the distribution.
    -Neither the name of Drayah, Giovanni Martina nor the names of its contributors may be used to endorse or
    promote products derived from this software without specific prior written permission.
    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
    WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
    PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
    DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
    CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
    OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
    DAMAGE.
 *
 * ESCPrinter.java
 *
 * Created on 10 de Setembro de 2006, 13:57
 *
 * @author Giovanni <gio@drayah.net>
 * Copyright © 2006 G.M. Martina
 *
 * Class that enables printing to ESC/P and ESC/P2 dot matrix printers (e.g. Epson LQ-570, Epson LX-300) by writing directly to a stream using standard I/O
 * Like this we have direct control over the printer and bypass Java2D printing which is considerably slower printing in graphics to a dotmatrix
 *
 */
package io.github.jokoframework.report.printer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Class that enables printing to ESC/P and ESC/P2 dot matrix printers (e.g. Epson LQ-570, Epson LX-300) by writing directly to a stream using standard I/O
 * Like this we have direct control over the printer and bypass Java2D printing which is considerably slower printing in graphics to a dotmatrix
 */
public class ESCPrinter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ESCPrinter.class);

    private boolean printNullAsEmpty = true;
    /* fields */
    final Charset UTF_8 = StandardCharsets.UTF_8;
    final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
    final Charset DEFAULT_CHARSET = ISO_8859_1;
    private boolean escp24pin; //boolean to indicate whether the printer is a 24 pin esc/p2 epson
    private ByteArrayOutputStream baos;
    private PrintStream pstream;

    private List encodedList;
    private static int MAX_ADVANCE_9PIN = 216; //for 24/48 pin esc/p2 printers this should be 180
    private static int MAX_ADVANCE_24PIN = 180;
    private static int MAX_UNITS = 127; //for vertical positioning range is between 0 - 255 (0 <= n <= 255) according to epson ref. but 255 gives weird errors at 1.5f, 127 as max (0 - 128) seems to be working
    private static final float CM_PER_INCH = 2.54f;
    public static final String START_REPORT_CHAR = "EscReportStart[";
    public static final String END_REPORT_CHAR = "]EscReportEnd";

    /* decimal ascii values for epson ESC/P commands */
    private static final char ESC = 27; //escape
    private static final char INIT_PRINTER = 64; //@ Initialize printer
    private static final char LINE_FEED = 10; //line feed/new line
    private static final char FORM_FEED = 12; //form feed
    private static final char PARENTHESIS_LEFT = 40;
    private static final char BACKSLASH = 92;
    private static final char CR = 13; //carriage return
    private static final char HTAB = 9; //Horizontal tab
    private static final char VTAB = 11; //Vertical tab

    private static final char g = 103; //15cpi pitch
    private static final char p = 112; //used for choosing proportional mode or fixed-pitch
    private static final char t = 116; //used for character set assignment/selection
    private static final char l = 108; //used for setting left margin
    private static final char x = 120; //used for setting draft or letter quality (LQ) printing
    private static final char k = 107; //used for setting font
    private static final char C = 67; //set the page length
    private static final char E = 69; //bold font on
    private static final char F = 70; //bold font off
    private static final char J = 74; //used for advancing paper vertically
    private static final char P = 80; //10cpi pitch
    private static final char M = 77; //12cpi pitch
    private static final char Q = 81; //used for setting right margin
    private static final char X = 88; //used for setting character size
    private static final char $ = 36; //used for absolute horizontal positioning
    private static final char DOUBLE_WIDTH = 87; //used for setting on/off double width
    private static final char DOUBLE_HEIGHT = 119; //used for setting n/off double height for one line
    private static final char ROMAN_DRAFT = 48; //Used for Roman font or to set draft mode
    private static final char SERIF_LQ = 49; //Used for Serif font or to set LQ mode
    private static final char SIX_LINES_PER_INCH = 50; //Six lines per inch
    private static final char EIGHT_LINES_PER_INCH = 48; //Eight lines per inch
    private static final char NUMBER_0 = 0;
    private static final char NUMBER_1 = 1;
    private static final char NUMBER_3 = 3;

    /* International character sets */
    public static final char USA = NUMBER_0;
    public static final char LATIN_AMERICA = 29;

    /**
     * Creates a new instance of ESCPrinter
     */
    public ESCPrinter(boolean escp24pin) {
        this.escp24pin = escp24pin;
        try {
            this.initialize();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the print stream result as string
     *
     * @return
     */
    public String generate() {
        String result = this.getAsString();
        close();
        return result;
    }

    /**
     * Close the print stream
     *
     * @return
     */
    public void close() {
        try {
            pstream.close();
            baos.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Initialize the printer with default arguments
     *
     * @return
     */
    public boolean initialize() throws UnsupportedEncodingException {
        //create stream objs
        baos = new ByteArrayOutputStream();
        encodedList = new ArrayList();
        pstream = new PrintStream(baos, true, DEFAULT_CHARSET.name());

        //reset default settings
//        print(START_REPORT_CHAR);
        print(ESC);
        print(INIT_PRINTER);

        //select 10-cpi character pitch by default
        select10CPI();

        //select LQ quality printing
        selectLQPrinting();
        selectSerifFont();

        //set character set
        selectCharacterSet(LATIN_AMERICA, 16);

        return true;
    }

    /**
     * Method for setting the international character set
     *
     * @param charset
     * @param arg
     */
    public void selectCharacterSet(char charset, int arg) {
        //Select international character set
        print(ESC);
        print(PARENTHESIS_LEFT);
        print(t);
        print(NUMBER_3); //always 3
        print(NUMBER_0); //always 0
        print(NUMBER_1);
        print(charset);
        print((char) arg);
    }

    /**
     * Set the page length in inches
     *
     * @param length
     * @return
     */
    public ESCPrinter setPageLengthInches(char length) {
        print(ESC);
        print(C);
        print(length);
        return this;
    }

    /**
     * Select 10 characters per inch (condensed mode available)
     *
     * @return
     */
    public ESCPrinter select10CPI() {
        print(ESC);
        print(P);
        return this;
    }

    /**
     * Select 12 characters per inch
     *
     * @return
     */
    public ESCPrinter select12CPI() {
        print(ESC);
        print(M);
        return this;
    }

    /**
     * Select 15 characters per inch
     *
     * @return
     */
    public ESCPrinter select15CPI() {
        print(ESC);
        print(g);
        return this;
    }

    public ESCPrinter selectSixLinesPerInch() {
        print(ESC);
        print(SIX_LINES_PER_INCH);
        return this;
    }

    public ESCPrinter selectEightLinesPerInch() {
        print(ESC);
        print(EIGHT_LINES_PER_INCH);
        return this;
    }

    /**
     * Set the printing quality to draft
     *
     * @return
     */
    public void selectDraftPrinting() {
        print(ESC);
        print(x);
        print(ROMAN_DRAFT);
    }

    /**
     * Set the printing quality to LQ
     */
    public void selectLQPrinting() {
        print(ESC);
        print(x);
        print(SERIF_LQ);
    }

    /**
     * Set the printing font to Roman
     */
    public ESCPrinter selectRomanFont() {
        print(ESC);
        print(k);
        print(ROMAN_DRAFT);
        return this;
    }

    /**
     * Set the printing font to Serif
     */
    public ESCPrinter selectSerifFont() {
        print(ESC);
        print(k);
        print(SERIF_LQ);
        return this;
    }

    /**
     * Set character Size
     */
    public ESCPrinter setCharacterSize(int size) {
        print(ESC);
        print(X);
        print(0);
        print(size);
        print(0);
        return this;
    }

    /**
     * Turn on/off double width
     */
    public ESCPrinter doubleWidth(boolean on) {
        print(ESC);
        print(DOUBLE_WIDTH);
        print(on ? NUMBER_1 : NUMBER_0);
        return this;
    }

    /**
     * Turn on/off double height
     */
    public ESCPrinter doubleHeight(boolean on) {
        print(ESC);
        print(DOUBLE_HEIGHT);
        print(on ? NUMBER_1 : NUMBER_0);
        return this;
    }

    /**
     * Double the size of a given word
     */
    public ESCPrinter doubleSize(Object param) {
        return doubleSize(param, false);
    }

    /**
     * Double the size of a given word wih the capability of making it bold
     */
    public ESCPrinter doubleSize(Object param, boolean bold) {
        if (bold) {
            bold(true);
        }
        doubleWidth(true);
        doubleHeight(true);
        printParam(param);
        doubleWidth(false);
        doubleHeight(false);
        if (bold) {
            bold(false);
        }
        return this;
    }

    /**
     * Turn on/of condensed mode
     */
    public ESCPrinter condensed(boolean on) {
        print(ESC);
        print(on ? (char) 15 : (char) 18);
        return this;
    }

    /**
     * Turn on/of bold mode
     */
    public ESCPrinter bold(boolean bold) {
        print(ESC);
        if (bold) {
            print(E);
        } else {
            print(F);
        }
        return this;
    }

    /**
     * Performs new line
     */
    public void newLine() {
        print(CR); //according to epson esc/p ref. manual always send carriage return before line feed
        print(LINE_FEED);
    }

    /**
     * Performs n new lines
     */
    public void newLine(int lines) {
        for (int i = 0; i < lines; i++) {
            newLine();
        }
    }

    /**
     * Ejects single sheet
     */
    public void formFeed() {
        print(CR); //according to epson esc/p ref. manual it is recommended to send carriage return before form feed
        print(FORM_FEED);
    }

    /**
     * Turn on/of proportional mode
     */
    public void proportionalMode(boolean proportional) {
        print(ESC);
        print(p);
        if (proportional)
            print((char) 49);
        else
            print((char) 48);
    }

    /**
     * Advance the horizontal print position to x centimeters from top
     *
     * @param centimeters
     * @return
     */
    public void advanceVertical(float centimeters) {
        float inches = centimeters / CM_PER_INCH;
        int units = (int) (inches * (escp24pin ? MAX_ADVANCE_24PIN : MAX_ADVANCE_9PIN));

        while (units > 0) {
            char n;
            if (units > MAX_UNITS)
                n = (char) MAX_UNITS; //want to move more than range of parameter allows (0 - 255) so move max amount
            else
                n = (char) units; //want to move a distance which fits in range of parameter (0 - 255)

            print(ESC);
            print(J);
            print(n);

            units -= MAX_UNITS;
        }
    }

    /**
     * Advance the horizontal print position to x centimeters from left
     *
     * @param centimeters
     * @return
     */
    public void advanceHorizontal(float centimeters) {
        float inches = centimeters / CM_PER_INCH;
        int unitsLow = (int) (inches * 120) % 256;
        int unitsHigh = (int) (inches * 120) / 256;

        print(ESC);
        print(BACKSLASH);
        print((char) unitsLow);
        print((char) unitsHigh);
    }

    /**
     * Sets the horizontal print position to x centimeters from left margin
     *
     * @param centimeters
     * @return
     */
    public ESCPrinter setAbsoluteHorizontalPosition(float centimeters) {
        float inches = centimeters / CM_PER_INCH;
        int unitsLow = (int) (inches * 60) % 256;
        int unitsHigh = (int) (inches * 60) / 256;

        print(ESC);
        print($);
        print((char) unitsLow);
        print((char) unitsHigh);
        return this;
    }

    /**
     * Sets the horizontal print position to x centimeters from left margin
     *
     * @param centimeters
     * @return
     */
    public ESCPrinter horizontalPositionCm(float centimeters) {
        setAbsoluteHorizontalPosition(centimeters);
        return this;
    }

    /**
     * Sets the horizontal print position to x centimeters from left margin and next print a given object value
     *
     * @param centimeters
     * @param param
     * @return
     */
    public ESCPrinter horizontalPositionCm(float centimeters, Object param) {
        setAbsoluteHorizontalPosition(centimeters);
        printParam(param);
        return this;
    }

    /**
     * Sets the horizontal print position to x centimeters from left margin with given spaces.
     * Next print a given object value
     *
     * @param centimeters
     * @param spaces
     * @param param
     * @return
     */
    public ESCPrinter horizontalPositionCm(float centimeters, int spaces, Object param) {
        setAbsoluteHorizontalPosition(centimeters);
        space(spaces);
        printParam(param);
        return this;
    }

    /**
     * performs horizontal tabs n number of times
     *
     * @param tabs
     * @return
     */
    public ESCPrinter tab(int tabs) {
        for (int i = 0; i < tabs; i++) {
            print(HTAB);
        }
        return this;
    }

    /**
     * performs horizontal tabs n number of times and print an object value
     *
     * @param tabs
     * @param param
     * @return
     */
    public ESCPrinter tab(int tabs, Object param) {
        for (int i = 0; i < tabs; i++) {
            print(HTAB);
        }
        printParam(param);
        return this;
    }

    /**
     * performs vertical tabs n number of times
     *
     * @param tabs
     * @return
     */
    public ESCPrinter vtab(int tabs) {
        for (int i = 0; i < tabs; i++) {
            print(VTAB);
        }
        return this;
    }

    /**
     * performs vertical tabs n number of times and print an object value
     *
     * @param tabs
     * @param param
     * @return
     */
    public ESCPrinter vtab(int tabs, Object param) {
        for (int i = 0; i < tabs; i++) {
            print(VTAB);
        }
        printParam(param);
        return this;
    }

    /**
     * Print a space char n number of times
     *
     * @param spaces
     * @return
     */
    public ESCPrinter space(int spaces) {
        String finalSpaces = StringUtils.repeat(" ", spaces);
        print(finalSpaces);
        return this;
    }

    /**
     * Print a space char n number of times and print an object value
     *
     * @param spaces
     * @param param
     * @return
     */
    public ESCPrinter space(int spaces, Object param) {
        String finalSpaces = StringUtils.repeat(" ", spaces);
        print(finalSpaces);
        printParam(param);
        return this;
    }

    /**
     * Print a point char n number of times
     *
     * @param points
     * @return
     */
    public ESCPrinter point(int points) {
        String finalPoints = StringUtils.repeat(".", points);
        print(finalPoints);
        return this;
    }

    /**
     * Set left and right margins
     *
     * @param columnsLeft  > 0 && <= 255
     * @param columnsRight > 0 && <= 255
     */
    public void setMargins(int columnsLeft, int columnsRight) {
        print(ESC);
        print(l);
        print((char) columnsLeft);

        //right
        print(ESC);
        print(Q);
        print((char) columnsRight);
    }

    public ESCPrinter print(Object param) {
        pstream.print(param);
        encodedList.add(String.valueOf(param));
        return this;
    }

    public ESCPrinter printParam(Object param) {
        if (param != null) {
            print(param);
        }
        if (param == null && isPrintNullAsEmpty()) {
            print("");
        }
        return this;
    }

    /**
     * Add text to builder. Useful for building reports or adding generated reports after ESC/P commands
     *
     * @param text a String value as parameter
     * @return updated EscPUtil instance.
     */
    public ESCPrinter addText(String text) {
        this.print(text);
        return this;
    }

    /**
     * Return the result string with ESC/P commands
     *
     * @return String with ESC/P commands
     */
    public String getAsString() {
        return Base64.getEncoder().encodeToString(this.getAsBytes());
    }

    public byte[] getAsBytes() {
        return baos.toByteArray();
    }

    public List getEncodedList() {
        return encodedList;
    }

    public void encode(String[] a) {
        try {
            System.setOut(this.pstream);
            for (char c = 128; c < 256; c++)
                System.out.println("Got char = " + c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPrintNullAsEmpty() {
        return printNullAsEmpty;
    }

    public void setPrintNullAsEmpty(boolean printNullAsEmpty) {
        this.printNullAsEmpty = printNullAsEmpty;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<ESCPrinter[=").append(", 24pin=").append(escp24pin).append("]>");
        return stringBuilder.toString();
    }
}
