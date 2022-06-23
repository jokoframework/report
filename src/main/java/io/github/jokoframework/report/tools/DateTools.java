package io.github.jokoframework.report.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class DateTools {

    private Locale locale;

    public DateTools(Locale locale) {
        this.locale = locale;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTools.class);

    public String format(Object dateToFormat, String format) {
        return format(dateToFormat, format, null);
    }

    public String format(Object dateToFormat, String format, Locale locale) {
        if (dateToFormat instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return ((LocalDateTime) dateToFormat).format(formatter);
        }
        if (dateToFormat instanceof LocalDate) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return ((LocalDate) dateToFormat).format(formatter);
        }
        if (dateToFormat instanceof Date) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, configLocale(locale));
            return simpleDateFormat.format(dateToFormat);
        }
        return "";
    }

    public String formatAsWords(Object dateToFormat, String format) {
        return formatAsWords(dateToFormat, format, null, null);
    }

    public String formatAsWords(Object dateToFormat, String format, Locale locale) {
        return formatAsWords(dateToFormat, format, null, locale);
    }

    public String formatAsWords(Object dateToFormat, String format, String city) {
        return formatAsWords(dateToFormat, format, city, null);
    }


    public String formatAsWords(Object dateToFormat, String format, String city, Locale locale) {
        if (dateToFormat instanceof LocalDateTime) {
            LocalDate localDate = ((LocalDateTime) dateToFormat).toLocalDate();
            return formatLocalDateAsWords(localDate, format, city, configLocale(locale));
        }
        if (dateToFormat instanceof LocalDate) {
            return formatLocalDateAsWords((LocalDate) dateToFormat, format, city, configLocale(locale));
        }
        if (dateToFormat instanceof Date) {
            LocalDate localDate = ((Date) dateToFormat).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            return formatLocalDateAsWords(localDate, format, city, configLocale(locale));
        }
        return "";

    }

    private String formatLocalDateAsWords(LocalDate localDate, String format, String city, Locale locale) {
        LOGGER.debug("FORMAT AS WORDS");
        Integer day = localDate.getDayOfMonth();
        Month month = localDate.getMonth();
        Integer year = localDate.getYear();

        String monthName = month.getDisplayName(TextStyle.FULL, locale);
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        String[] args = {day.toString(), monthName, year.toString()};
        if (city != null && !city.isEmpty()) {
            args = new String[]{city, day.toString(), monthName, year.toString()};
        }
        return MessageFormat.format(format, args);
    }

    private Locale configLocale(Locale locale) {
        return locale == null ? this.locale : locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}

