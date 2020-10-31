package de.htw.saar.env.sim.device.management;

import de.htw.saar.env.sim.io.IOManager;
import de.htw.saar.env.sim.io.SystemLogger;

import java.io.*;

/**
 * Custom ClassLoader to load new Java classes from a specified path at runtime
 */
public class DeviceClassLoader extends ClassLoader {

    private String path;

    public DeviceClassLoader(ClassLoader parent) {
        super(parent);
        path = IOManager.getInstance().path;
    }

    /**
     * Loads and defines a new class
     *
     * @param name Name of the .class file containing the desired Class
     * @return A reference to the defined Class object
     * @throws ClassNotFoundException
     */
    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        byte[] b;
        try {
            b = loadClassFromFile(name);
        } catch (Exception e) {
            try {
                return getSystemClassLoader().loadClass(name + ".class");
            } catch (Exception ex) {
                throw new ClassNotFoundException();
            }
        }
        return defineClass(name, b, 0, b.length);
    }

    /**
     * Method to load a class file from a specified path
     *
     * @param fileName Name of the .class file
     * @return the content of the .class file as byte-Array
     * @throws Exception
     */
    private byte[] loadClassFromFile(String fileName) throws Exception {
        File file = new File(path + fileName.replace('.', File.separatorChar) + ".class");
        InputStream inputStream = new FileInputStream(file);
        if (inputStream==null){
        }
        byte[] buffer;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue = 0;
        while ((nextValue = inputStream.read()) != -1) {
            byteStream.write(nextValue);
        }
        buffer = byteStream.toByteArray();
        inputStream.close();
        return buffer;
    }
}
