package io.github.jokoframework.report.printer;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author ncanatta
 */
public class EscP2Tool {

    public String addTab(int tab, Object param) {
        String tabs = StringUtils.repeat("\t", tab);
        return tabs + param;
    }

    public String addSpace(int space, Object param) {
        String spaces = StringUtils.repeat(" ", space);
        return spaces + param;
    }

    public String addTabAndSpace(int tab, int space, Object param) {
        String tabs = StringUtils.repeat("\t", tab);
        tabs = this.addSpace(space, tabs);
        return tabs + param;
    }

    public String addLine(int line) {
        return StringUtils.repeat("\n", line);
    }

    public String escOnePerSixInchLineSpacing() {
        EscPUtil escPUtil = EscPUtil.getCleanInstance();
        escPUtil.escOnePerSixInchLineSpacing();
        return escPUtil.getResult();
    }

    public String escOnePerEightInchLineSpacing() {
        EscPUtil escPUtil = EscPUtil.getCleanInstance();
        escPUtil.escOnePerEightInchLineSpacing();
        return escPUtil.getResult();
    }

    public String escN72InchLineSpacing(int n) {
        EscPUtil escPUtil = EscPUtil.getCleanInstance();
        escPUtil.escN72InchLineSpacing(n);
        return escPUtil.getResult();
    }

    public String escN216InchLineSpacing(int n) {
        EscPUtil escPUtil = EscPUtil.getCleanInstance();
        escPUtil.escN216InchLineSpacing(n);
        return escPUtil.getResult();
    }

    public String format(String format, Object value) {
        return this.format(format, value, null);
    }

    public String format(String format, Object value, String formatWhenZero) {
        if (value instanceof BigDecimal && formatWhenZero != null) {
            format = BigDecimal.ZERO.compareTo((BigDecimal) value) == 0
                    ? formatWhenZero : format;
        }
        DecimalFormat df = new DecimalFormat(format);
        return df.format(value);
    }

}
