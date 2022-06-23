package io.github.jokoframework.report.tools;

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

    public Locale locale;
    public DateTools date;
    public NumberTools number;
    public final Class<StringUtils> stringUtils;
    public final Class<ObjectUtils> objectUtils;
    public final TextStyle textStyle;
    public final DecimalFormat decimalFormat;
    public final Class<ZoneId> zoneId;
    public final NumberTool numberTool;
    public final DateTool dateTool;


    public ReportTools() {
        this.setLocale(new Locale("es", "ES"));

        //JAVA
        this.textStyle = TextStyle.FULL;
        this.decimalFormat = new DecimalFormat();
        this.zoneId = ZoneId.class;

        //Apache Commons
        this.stringUtils = StringUtils.class;//NOSONAR
        this.objectUtils = ObjectUtils.class;//NOSONAR

        //Velocity tools
        this.numberTool = new NumberTool();
        this.dateTool = new DateTool();
    }

    public void initTools() {
        this.date = new DateTools(this.locale);
        this.number = new NumberTools(this.locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        this.initTools();
    }

    public Object nullSafe(Object param, Object defaultValue) {
        return param == null ? defaultValue : param;
    }


}
