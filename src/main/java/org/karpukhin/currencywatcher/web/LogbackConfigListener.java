package org.karpukhin.currencywatcher.web;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.karpukhin.currencywatcher.exceptions.ApplicationException;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ServletContextPropertyUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Pavel Karpukhin
 * @since 07.12.14
 */
public class LogbackConfigListener implements ServletContextListener {

    public static final String CONFIG_LOCATION_PARAM = "logbackConfigLocation";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        initLogging(event.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    static void initLogging(ServletContext servletContext) {
        String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        if (location != null) {
            location = ServletContextPropertyUtils.resolvePlaceholders(location, servletContext);
        }

        // Write log message to server log.
        servletContext.log("Initializing logback from [" + location + "]");

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        context.reset();
        try {
            configurator.doConfigure(location);
        } catch (JoranException e) {
            throw new ApplicationException("Error while logback configure", e);
        }
    }
}
