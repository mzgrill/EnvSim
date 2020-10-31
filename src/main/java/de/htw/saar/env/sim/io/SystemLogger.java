package de.htw.saar.env.sim.io;

import de.htw.saar.env.sim.EnvSimApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * LoggerClass replacing and expanding the default SpringLogger
 */
@Service("SystemLogger")
public class SystemLogger {

    private Logger logger = LogManager.getLogger(EnvSimApplication.class);

    public void logError(String message){
            logger.error(message);
    }

    public void logInfo(String message){
            logger.info(message);
    }
}
