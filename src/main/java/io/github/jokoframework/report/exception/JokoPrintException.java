package io.github.jokoframework.report.exception;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author ncanatta
 */
@Data
public class JokoPrintException extends Exception implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JokoPrintException.class);

    private static final long serialVersionUID = 1L;

    private String errorCode;
    private transient Object[] params;

    public JokoPrintException(String messageKey) {
        super(messageKey);
        this.errorCode = messageKey;
    }

    public JokoPrintException(String messageKey, Object... params) {
        this(messageKey);
        this.params = params;
    }

}
