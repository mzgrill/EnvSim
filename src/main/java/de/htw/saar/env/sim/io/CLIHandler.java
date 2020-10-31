package de.htw.saar.env.sim.io;

import de.htw.saar.env.sim.device.container.DeviceBlueprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.*;
import static de.htw.saar.env.sim.io.LogStrings.*;
import static de.htw.saar.env.sim.io.IOManager.*;
import static java.lang.Thread.sleep;

/**
 * Simple IO class, using UNIX-pipes to enable CLI-Interaction
 */
@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class CLIHandler implements Runnable{

    @Autowired
    FacadeREST fassade;

    @Autowired
    SystemLogger logger;

    private String path;
    private BufferedReader bufferedReader;
    private String[] pipes;
    private FileWriter fileWriter;

    public CLIHandler() {
        IOManager ioManager = IOManager.getInstance();
        path = ioManager.path;
        pipes = new String[10];
        pipes[0] = ioManager.properties.getProperty(CLI_OUTPUT_PIPE);
        pipes[1] = ioManager.properties.getProperty(CLI_BENCHMARK_PIPE);
        pipes[2] = ioManager.properties.getProperty(CLI_INPUT_PIPE);
    }

    @PostConstruct
    private void initialize(){
        new Thread(this).start();
    }

    /**
     * Reads the next line from the input-pipe, then hands the input over to a handler method
     * @throws Exception
     */
    private void read() throws Exception {
        String in = bufferedReader.readLine();
        if (in != null) {
            handleInput(in);
        }
    }

    /**
     * Run method, opening an InputStream to the input pipe, then checking it every second for new input
     * Note that this operation automatically blocks when trying to read from a pipe that is not opened in write mode
     * by another process
     */
    @Override
    public void run(){
        try {
            FileInputStream inputS = new FileInputStream(new File(path + pipes[2]));
            InputStreamReader inputStreamReader = new InputStreamReader(inputS);
            bufferedReader = new BufferedReader(inputStreamReader);
        }catch (FileNotFoundException fileNotFoundException){
            logger.logError(CLI_PIPE_ERROR);
        }
        try {
            write(CLI_HELP, 0);
        } catch (IOException e) {
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                read();
                sleep(1000);
            } catch (Exception e){
            }
        }
    }

    /**
     * Writes the output to the corresponding output-pipe specified in @outputPipe
     */
    public void write(String out, int outputPipe) throws IOException {
        fileWriter = new FileWriter(new File(path + pipes[outputPipe]));
        fileWriter.write(out);
        fileWriter.close();
    }

    /**
     * Checks the received input for a match with a known command and then calls the corresponding methods
     * Throws an exception when a unknown command is processed
     */
    private void handleInput(String in) throws Exception {
        switch (in.substring(0,2).trim()){
            case CLI_COMMAND_PS: { ps(); break; }
            case CLI_COMMAND_RUN: { create(in); break; }
            case CLI_COMMAND_RM: { remove(in); break; }
            case CLI_COMMAND_CB: { changeBehaviour(in); break;}
            case CLI_COMMAND_RA: { removeAll(); break;}
            case CLI_COMMAND_HE: { write(CLI_HELP, 0); break;}
            default: { throw new Exception(); }
        }
    }

    /**The following methods call their respective corresponding methods from the facade, using arguments extracted from
     * the received input
    **/

    private void ps() throws IOException {
        StringBuffer out = new StringBuffer();
        out.append("DeviceStatus: \n");
        fassade.getDeviceChanges(0L).getHeaderList().forEach(header -> {
            out.append("Device: " + header.getId() + " Type: " + header.getType() +
                    " Behaviour: " + header.getBehaviour() + " Value: " + header.getCurrentValue() + "\n");
        });
        write(out.toString(),0);
    }

    private void create(String in){
        String[] args = in.split("\\s+");
        fassade.addDevice(new DeviceBlueprint(args[1], args[2], new Integer(args[3])));
    }

    private void remove(String in) {
        String[] args = in.split("\\s+");
        fassade.removeDevice(new Long(args[1]));
    }

    private void changeBehaviour(String in) {
        String[] args = in.split("\\s+");
        fassade.changeBehaviour(args[1], new Long(args[2]));
    }

    private void removeAll(){
        fassade.removeAllDevices();
    }
}
