package io.github.jokoframework.report.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtil {

    private Locale locale;

    public NumberUtil(Locale locale) {
        this.locale = locale;
    }

    public String format(String format, Object value) {
        return this.format(format, value, null);
    }

    public String format(String format, Object value, String formatWhenZero) {
        if (value instanceof BigDecimal && formatWhenZero != null) {
            format = BigDecimal.ZERO.compareTo((BigDecimal) value) == 0
                    ? formatWhenZero : format;
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern(format);
        return df.format(value);
    }

}
