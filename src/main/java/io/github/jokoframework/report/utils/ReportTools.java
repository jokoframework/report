package io.github.jokoframework.report.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

@Getter
@Setter
public class ReportTools {

    private NumberTool numberTool;
    private DateTool date;
    private StringUtils stringUtils;
    private TextStyle textStyle;
    private Locale locale;
    private NumberUtil numberUtil;
    private DecimalFormat decimalFormat;
    private NumbersToSpanishWords numbersToSpanishWords;


    public ReportTools() {
        this.numberTool = new NumberTool();
        this.date = new DateTool();
        this.stringUtils = new StringUtils();//NOSONAR
        this.textStyle = TextStyle.FULL;
        this.locale = new Locale("es", "ES");
        this.decimalFormat = new DecimalFormat();
        this.numberUtil = new NumberUtil(this.locale);
        this.numbersToSpanishWords = new NumbersToSpanishWords();
    }

    public NumberTool numberTool() {
        return numberTool;
    }

    public DateTool date() {
        return date;
    }

    public DecimalFormat decimalFormat() {
        return decimalFormat;
    }

    public StringUtils stringUtils() {
        return stringUtils;
    }

    public Class string() {
        return String.class;
    }

    public Class zoneId() {
        return ZoneId.class;
    }

    public TextStyle textStyle() {
        return textStyle;
    }

    public Locale getLocale() {
        return locale;
    }

    public NumberUtil number() {
        return numberUtil;
    }

    public NumbersToSpanishWords numbersToWords() {
        return numbersToSpanishWords;
    }
}
