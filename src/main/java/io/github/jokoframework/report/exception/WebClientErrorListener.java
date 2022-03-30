package io.github.jokoframework.report.exception;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author ncanatta
 */
public class WebClientErrorListener implements JavaScriptErrorListener {

    String name;
    private final Logger logger = LoggerFactory.getLogger(WebClientErrorListener.class);

    @Override
    public void scriptException(HtmlPage htmlPage, ScriptException e) {
        logger.error(e.getMessage(), e);
    }

    @Override
    public void timeoutError(HtmlPage htmlPage, long l, long l1) {
        logger.error("Timeout Error - {} - {}", l, l1);
    }

    @Override
    public void malformedScriptURL(HtmlPage htmlPage, String s, MalformedURLException e) {
        logger.error(e.getMessage(), e);
    }

    @Override
    public void loadScriptError(HtmlPage htmlPage, URL url, Exception e) {
        logger.error(e.getMessage(), e);
    }

    @Override
    public void warn(String s, String s1, int i, String s2, int i1) {
        logger.info("Web client: {} - {}", s, s2);
    }
}
