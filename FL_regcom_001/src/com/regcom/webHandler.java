package com.regcom;

import java.awt.*;
import java.io.IOException;

/**
 * Created by cengen on 8/15/17.
 */
class webHandler {
    private String browserLocation;
    private boolean browserOpened;
    private Process process;
    private boolean incognito = false;

    webHandler(String path) {
        setBrowserLocation(path);
        setBrowserOpened(false);
        setProcess(null);
    }

    void openBrowser() {
        _openBrowser(null);
    }

    void openBrowser(String s) {
        _openBrowser(s);
    }

    private void _openBrowser(String s) {
        try {
            setProcess(Runtime.getRuntime().exec(new String[]{getBrowserLocation(), (s != null) ? s : "", this.incognito ? "--incognito" : ""}));
            //getProcess().waitFor();
            setBrowserOpened(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void closeBrowser() {
        getProcess().destroy();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            getProcess().waitFor();
        }
        catch (Exception ignored) {}
    }

    private String getBrowserLocation() {
        return browserLocation;
    }

    private void setBrowserLocation(String browserLocation) {
        this.browserLocation = browserLocation;
    }

    boolean isBrowserOpened() {
        return browserOpened;
    }

    private void setBrowserOpened(boolean browserOpened) {
        this.browserOpened = browserOpened;
    }

    private Process getProcess() {
        return process;
    }

    private void setProcess(Process process) {
        this.process = process;
    }

    public String getTestPage(String baseLocation) throws NullPointerException {
        System.out.print(baseLocation);
        return baseLocation + "testpage.html";
    }

    public Color getTestPageColor() {
        return new Color(126, 52, 112);
    }

    public void toggleIncognito() {
        this.incognito = !this.incognito;
    }
}
