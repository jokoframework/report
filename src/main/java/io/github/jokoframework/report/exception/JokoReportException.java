package io.github.jokoframework.report.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author ncanatta
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JokoReportException extends Exception implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JokoReportException.class);

    private static final long serialVersionUID = 1L;

    private String errorMessage;
    private transient Object[] params;

    public JokoReportException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public JokoReportException(String errorMessage, Object... params) {
        this(MessageFormat.format(errorMessage, params));
        this.params = params;
    }

}
