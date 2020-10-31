package de.htw.saar.env.sim.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemLoggerTest {

    SystemLogger logger = new SystemLogger();

    @Test
    void logError() {
        try{
            logger.logError("This is a test");
        }catch (Exception exception){
            fail();
    }
    }

    @Test
    void logInfo() {
        try {
            logger.logInfo("This is a test");
        }catch (Exception exception){
            fail();
        }
    }
}