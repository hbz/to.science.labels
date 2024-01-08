package helper;

import java.io.File;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import play.Play;

/**
 * @author I. Kuss
 *         <ul>
 *         <li>Developer Logger Context - konfiguriert einen Logger für den
 *         Entwicklungsmodus</li>
 *         <li>benutzt JoranConfigurator, um die Logback Konfiguration zu
 *         überschreiben</li>
 *         <li>die Konfiguration selber muss als externe Datei (z.B.
 *         logback.xml) übergeben werden</li>
 *         </ul>
 *
 */
public class DevLoggerContext {
    // Quelle hier:
    // http://stackoverflow.com/questions/14288623/logback-externalization

    /**
     * @param logbackConfFilename
     *            - Dateiname der logback.xml mit vorangestelltem relativem
     *            Pfad, also z.B. "conf/logback.xml"
     */
    public static void doConfigure(String logbackConfFilename) {
        File logbackConfigurationFile = null;
        if (logbackConfFilename == null) {
            play.Logger.info("No logback configuration specified -- using default confguration.");
            play.Logger.info("Use application conf variable \"logger.config\" to point to a logback configuration.");
            return;
        }
        try {
            logbackConfigurationFile = Play.application().getFile(logbackConfFilename);
            play.Logger.info("logback configuration {} wird geladen.", logbackConfFilename);
            JoranConfigurator configurator = new JoranConfigurator();
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();
            configurator.setContext(loggerContext);
            configurator.doConfigure(logbackConfigurationFile);
            play.Logger.info("Default configuration overridden by logback configuration {}.", logbackConfFilename);
        } catch (Exception exception) {
            play.Logger.warn("Can't configure logback with specified file " + logbackConfFilename
                    + " - Keeping default configuration", exception);
        }
    }

}