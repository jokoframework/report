package io.github.jokoframework.report.tools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberTools {

    private Locale locale;

    public NumberTools(Locale locale) {
        this.locale = locale;
    }

    public String format(String format, Object value) {
        return this.format(format, value, null);
    }

    public String format(String format, Object value, String formatWhenZero) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal && formatWhenZero != null) {
            format = BigDecimal.ZERO.compareTo((BigDecimal) value) == 0
                    ? formatWhenZero : format;
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern(format);
        return df.format(value);
    }

    public String toSpanish(Object number) {
        String numberToConvert = "";
        if (number instanceof Integer || number instanceof Double || number instanceof BigDecimal) {
            numberToConvert = number.toString();
        }
        return NumbersToSpanishWords.numberToWords(numberToConvert);
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
