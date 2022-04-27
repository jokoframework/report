package io.github.jokoframework.report.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author ncanatta
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class JokoReportException extends Exception implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JokoReportException.class);

    private static final long serialVersionUID = 1L;

    private String errorCode;
    private transient Object[] params;

    public JokoReportException(String messageKey) {
        super(messageKey);
        this.errorCode = messageKey;
    }

    public JokoReportException(String messageKey, Object... params) {
        this(messageKey);
        this.params = params;
    }

}
