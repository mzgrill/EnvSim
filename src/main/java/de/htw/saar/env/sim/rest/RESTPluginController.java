package de.htw.saar.env.sim.rest;

import de.htw.saar.env.sim.device.container.Behaviour;
import de.htw.saar.env.sim.io.FacadeREST;
import de.htw.saar.env.sim.util.PluginRESTContainer;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
public class RESTPluginController
{

    private static String javaPath = "EnvSimResources//Beispielgeräte";
    private static String classFilePath = "EnvSimResources//";

    @Autowired
    FacadeREST fassade;


   @PutMapping("/addBlueprint")
    public void addDeviceBehavior(@RequestBody PluginRESTContainer pluginRESTContainer) throws IOException, JAXBException {
       JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();



       byte[] bytes = Base64.getMimeDecoder().decode(pluginRESTContainer.getDevice());
       String tempClassFilePath = javaPath + "\\" + pluginRESTContainer.getDeviceName().replace(".java", "");
       File newDir = new File(tempClassFilePath);
       newDir.mkdir();
       writeToFile(bytes, tempClassFilePath + "\\" + pluginRESTContainer.getDeviceName());
       writeToFile(bytes, classFilePath + "\\" + pluginRESTContainer.getDeviceName());

       tempClassFilePath = tempClassFilePath + "\\" + "Behavior" ;
       newDir = new File(tempClassFilePath);
       newDir.mkdir();
       for(int i = 0; i < pluginRESTContainer.getBehaviors().size(); i++){
           bytes = Base64.getMimeDecoder().decode(pluginRESTContainer.getBehaviors().get(i));

           writeToFile(bytes, tempClassFilePath + "\\" + pluginRESTContainer.getBehaviorNames().get(i));
           writeToFile(bytes, classFilePath + "\\" + pluginRESTContainer.getBehaviorNames().get(i));
       }


       javaCompiler.run(null, null , null,"D:\\HTW\\Bachelor\\Code\\SmartCityEnvSim-master\\EnvSimResources\\Device.java");//"-sourcepath", classFilePath, "device.java");

        
       for(File f : new File(classFilePath).listFiles()){
           javaCompiler.run(null, null , null,"D:\\HTW\\Bachelor\\Code\\SmartCityEnvSim-master\\EnvSimResources\\Device.java");
           }

       for(File f : new File(classFilePath).listFiles()){
           if(f.getName().contains(".java")){
                f.delete();
           }
       }
   }


   @GetMapping("/getNames")
   public List<PluginRESTContainer> getNames() throws JAXBException, FileNotFoundException {
       System.out.println("AgetNames");
       return readFromFile();
   }

    private void writeToFile(byte[] bytes, String uploadedFileLocation) throws IOException {
        FileUtils.writeByteArrayToFile(new File(uploadedFileLocation), bytes);
    }

    private void toXml(PluginRESTContainer pluginRESTContainer) throws JAXBException {
        File file = new File(javaPath  + "\\" + pluginRESTContainer.getDeviceName() + ".xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(PluginRESTContainer.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(pluginRESTContainer, file);
    }

    private List<PluginRESTContainer> fromXML() throws JAXBException {
        ArrayList<PluginRESTContainer> pluginRESTContainers = new ArrayList<>();
        File file = new File(javaPath);
        File[] files = file.listFiles();
        JAXBContext jaxbContext = JAXBContext.newInstance(PluginRESTContainer.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        for(File f : files){
            pluginRESTContainers.add((PluginRESTContainer) jaxbUnmarshaller.unmarshal(f));
        }

        return pluginRESTContainers;

    }

    private List<PluginRESTContainer> readFromFile() throws FileNotFoundException {
        List<PluginRESTContainer> containers = new ArrayList<>();
        File file = new File(javaPath);
        File[] files = file.listFiles();
        PluginRESTContainer pluginRESTContainer = new PluginRESTContainer();
            for(File f : files){
                if(f.isDirectory()){
                    pluginRESTContainer = new PluginRESTContainer();
                    for(File fileDevice : f.listFiles()){

                        if(fileDevice.isDirectory()){
                            ArrayList<String> behaviorNames = new ArrayList<>();
                            for(File fileBehavior : fileDevice.listFiles()){
                                behaviorNames.add(fileBehavior.getName().replace(".java", ""));
                            }
                            pluginRESTContainer.setBehaviorNames(behaviorNames);
                        }else{
                            pluginRESTContainer.setDeviceName(fileDevice.getName().replace(".java", ""));
                        }

                    }
                    containers.add(pluginRESTContainer);
                }
            }

        return containers;
    }


}
