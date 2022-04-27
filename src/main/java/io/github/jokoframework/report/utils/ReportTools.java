package io.github.jokoframework.report.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
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

    public final NumberTool numberTool;
    public final DateTool date;
    public final StringUtils stringUtils;
    public final ObjectUtils objectUtils;
    public final TextStyle textStyle;
    public Locale locale;
    public final NumberUtil number;
    public final DecimalFormat decimalFormat;
    public final NumbersToSpanishWords numbersToSpanishWords;
    public final DateToWords dateToWords;


    public ReportTools() {
        this.numberTool = new NumberTool();
        this.date = new DateTool();
        this.stringUtils = new StringUtils();//NOSONAR
        this.objectUtils = new ObjectUtils();//NOSONAR
        this.textStyle = TextStyle.FULL;
        this.locale = new Locale("es", "ES");
        this.decimalFormat = new DecimalFormat();
        this.number = new NumberUtil(this.locale);
        this.numbersToSpanishWords = new NumbersToSpanishWords();//NOSONAR
        this.dateToWords = new DateToWords();
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

    public Class<String> string() {
        return String.class;
    }

    public Class<ZoneId> zoneId() {
        return ZoneId.class;
    }

    public TextStyle textStyle() {
        return textStyle;
    }

    public Locale getLocale() {
        return locale;
    }

    public NumberUtil number() {
        return number;
    }

    public NumbersToSpanishWords numbersToWords() {
        return numbersToSpanishWords;
    }

    public DateToWords dateToWords() {
        return dateToWords;
    }
}
