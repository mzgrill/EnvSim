package testUtil;

import de.htw.saar.env.sim.io.IOManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class testUtils {

    public static void initializeIOManager(){
        IOManager manager = IOManager.getInstance();
        ReflectionTestUtils.setField(manager, "path", "/home/dbuech/workspace/EnvSim/EnvSimResources/");
        try {
            IOManager.getInstance().loadConfig();
        }catch (IOException ioException){ioException.printStackTrace(); fail();}
    }
}
