package de.htw.saar.env.sim.mqtt;

import de.htw.saar.env.sim.io.IOManager;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import testUtil.testUtils;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticatorTest {

    @Test
    void getSSLSocketFactory() throws Exception {
        testUtils.initializeIOManager();
        Authenticator authenticator = new Authenticator();
        assertNotNull(authenticator.getSSLSocketFactory());
    }
}