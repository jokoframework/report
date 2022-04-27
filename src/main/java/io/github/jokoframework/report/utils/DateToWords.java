package io.github.jokoframework.report.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class DateToWords {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateToWords.class);

    public String formatDateAsWords(Date dateToFormat, String format) {
        return formatDateAsWords(dateToFormat, format, null, null);
    }

    public String formatDateAsWords(Date dateToFormat, String format, Locale locale) {
        return formatDateAsWords(dateToFormat, format, null, locale);
    }

    public String formatDateAsWords(Date dateToFormat, String format, String city) {
        return formatDateAsWords(dateToFormat, format, city, null);
    }

    public String formatDateAsWords(Date dateToFormat, String format, String city, Locale locale) {
        if (dateToFormat == null) {
            return "";
        }
        LocalDate localDate = dateToFormat.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return formatAsWords(localDate, format, city, configLocale(locale));
    }

    public String formatDateAsWords(LocalDate dateToFormat, String format) {
        return formatDateAsWords(dateToFormat, format, null, null);
    }

    public String formatDateAsWords(LocalDate dateToFormat, String format, Locale locale) {
        return formatDateAsWords(dateToFormat, format, null, locale);
    }

    public String formatDateAsWords(LocalDate dateToFormat, String format, String city) {
        return formatDateAsWords(dateToFormat, format, city, null);
    }

    public String formatDateAsWords(LocalDate dateToFormat, String format, String city, Locale locale) {
        if (dateToFormat == null) {
            return "";
        }
        return formatAsWords(dateToFormat, format, city, configLocale(locale));
    }

    public String formatDateAsWords(LocalDateTime dateToFormat, String format) {
        return formatDateAsWords(dateToFormat, format, null, null);
    }

    public String formatDateAsWords(LocalDateTime dateToFormat, String format, Locale locale) {
        return formatDateAsWords(dateToFormat, format, null, locale);
    }

    public String formatDateAsWords(LocalDateTime dateToFormat, String format, String city) {
        return formatDateAsWords(dateToFormat, format, city, null);
    }

    public String formatDateAsWords(LocalDateTime dateToFormat, String format, String city, Locale locale) {
        if (dateToFormat == null) {
            return "";
        }
        LocalDate localDate = dateToFormat.toLocalDate();
        return formatAsWords(localDate, format, city, configLocale(locale));
    }

    private Locale configLocale(Locale locale){
        return locale == null ? new Locale("es", "ES") : locale;
    }

    private String formatAsWords(LocalDate localDate, String format, String city, Locale locale){
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
}

