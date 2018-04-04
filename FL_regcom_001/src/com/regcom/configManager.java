package com.regcom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by cengen on 8/16/17.
 */
class configManager {
    int mouseClickDelay;
    int keyboardPressDelay;
    int loadPageDelay;
    int commandDelay;
    int mouseMoveTime;
    String browserLocation;
    String testFileBaseLocation;
    int retryDelay;
    int retryAttemptsScreenFind;
    int browserOpenDelay;
    private Properties prop;
    private FileOutputStream output = null;
    private FileInputStream input;
    private boolean inFile = false;
    private boolean outFile = false;
    private boolean loaded = false;

    configManager() {
        prop = new Properties();
    }

    private void openOut(String outfile) throws IOException, NullPointerException {
        if (!outFile & !inFile) {
            ClassLoader classloader = getClass().getClassLoader();
            File file = new File(classloader.getResource("conf/" + outfile).getFile());
            file.createNewFile();
            output = new FileOutputStream(file);
            outFile = true;
        } else
            System.out.println("Failed to load file, file already open");
    }

    private void openIn(String infile) throws IOException, NullPointerException {
        if (!outFile & !inFile) {
            ClassLoader classloader = getClass().getClassLoader();
            input = new FileInputStream(classloader.getResource("conf/" + infile).getFile());
            inFile = true;
        } else
            System.out.println("Failed to load file, file already open");
    }

    private void save() throws IOException {
        if (outFile)
            prop.store(output, null);
        else
            System.out.println("Failed to save as file is not open");
    }

    private void load() throws IOException {
        if (inFile) {
            prop.load(input);
            this.loaded = true;
        } else
            System.out.println("Failed to load file as there is not file loaded");
    }

    private void setProperties() {
        if (this.inFile & this.loaded) {
            mouseClickDelay = Integer.parseInt(this.prop.getProperty("mouseClickDelay"));
            keyboardPressDelay = Integer.parseInt(this.prop.getProperty("keyboardPressDelay"));
            loadPageDelay = Integer.parseInt(this.prop.getProperty("loadPageDelay"));
            commandDelay = Integer.parseInt(this.prop.getProperty("commandDelay"));
            mouseMoveTime = Integer.parseInt(this.prop.getProperty("mouseMoveTime"));
            browserLocation = this.prop.getProperty("browserLocation");
            retryDelay = Integer.parseInt(this.prop.getProperty("retryDelay"));
            retryAttemptsScreenFind = Integer.parseInt(this.prop.getProperty("retryAttemptsScreenFind"));
            browserOpenDelay = Integer.parseInt(this.prop.getProperty("browserOpenDelay"));
            testFileBaseLocation = this.prop.getProperty("testFileBaseLocation");
        } else
            System.out.println("Failed to set properties, file not loaded");
    }

    private void closeIn() throws IOException {
        if (inFile) {
            input.close();
            inFile = false;
            this.loaded = false;
        } else
            System.out.println("Failed to close file as it is not open");
    }

    private void closeOut() throws IOException {
        if (outFile) {
            output.close();
            outFile = false;
        } else
            System.out.println("Failed to close file as it is not open");
    }

    private void addProperty(String val1, String val2) {
        prop.setProperty(val1, val2);
    }

    LinkedList getProperties() {
        LinkedList<String> properties = new LinkedList<>();
        properties.add(String.format("mouseClickDelay: %d", this.mouseClickDelay));
        properties.add(String.format("keyboardPressDelay: %d", this.keyboardPressDelay));
        properties.add(String.format("loadPageDelay: %d", this.loadPageDelay));
        properties.add(String.format("commandDelay: %d", this.commandDelay));
        properties.add(String.format("mouseMoveTime: %d", this.mouseMoveTime));
        properties.add(String.format("browserLocation: %s", this.browserLocation));
        properties.add(String.format("retryDelay: %d", this.retryDelay));
        properties.add(String.format("retryAttemptsScreenFind: %d", this.retryAttemptsScreenFind));
        properties.add(String.format("testFileBaseLocation: %s", this.testFileBaseLocation));
        return properties;
    }

    void loadConfig() throws IOException {
        String file = "config.properties";
        configManager config = new configManager();
        config.openIn(file);
        config.load();
        config.setProperties();
        mouseClickDelay = config.mouseClickDelay;
        keyboardPressDelay = config.keyboardPressDelay;
        loadPageDelay = config.loadPageDelay;
        commandDelay = config.commandDelay;
        mouseMoveTime = config.mouseMoveTime;
        browserLocation = config.browserLocation;
        retryDelay = config.retryDelay;
        retryAttemptsScreenFind = config.retryAttemptsScreenFind;
        browserOpenDelay = config.browserOpenDelay;
        testFileBaseLocation = config.testFileBaseLocation;
        config.closeIn();
    }

    void createConfig() throws IOException {
        String file = "config.properties";
        configManager config = new configManager();
        config.openOut(file);
        config.addProperty("mouseClickDelay", "500");
        config.addProperty("keyboardPressDelay", "80");
        config.addProperty("loadPageDelay", "6000");
        config.addProperty("commandDelay", "100");
        config.addProperty("mouseMoveTime", "1");
        config.addProperty("browserLocation", "/usr/bin/google-chrome");
        config.addProperty("retryDelay", "1000");
        config.addProperty("retryAttemptsScreenFind", "5");
        config.addProperty("browserOpenDelay", "5000");
        config.addProperty("testFileBaseLocation", "");
        config.save();
        config.closeOut();
    }
}
