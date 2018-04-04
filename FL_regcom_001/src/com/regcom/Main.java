package com.regcom;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by cengen on 8/15/17.
 * Version 1.6.2
 * This program is capable of following a chain of links by visually locating a link on screen and clicking it until
 * it exhausts the chain of links.
 */

class Main {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

        regcom main = new regcom("google.com");  // this defines a start page, should be a search engine or page
        // that will not cause the bot to fail if search is set to true.

        main.setUp();

        //main.addWorkOrder(isSearch=true, launchOrder="cars", work="advanced search", "auto.com", "2018 honda accord");

        // when called as follows
        // the first term is if the first string is a search term or not
        // the second term -nth is the links to be clicked in order.
        //main.addWorkOrder(true, "cars", "advanced search", "auto.com", "2018 honda accord");
        //main.addWorkOrder(true, "butter", "Butter (2011) - IMDb", "(2011)", "hugo", "watchlist");
        //main.addWorkOrder(false, "https://www.apple.com/", "Apple and Business");

        // When called as follows
        // the first like is tells if the first term is a search term
        // the second term tells if the page should stay opened
        // the third term is the is the search term
        // the forth term -nth is the links to be clicked
        //main.addWorkOrder(false, false, "https://www.apple.com/", "Apple and Business", "partners");
        //main.addWorkOrder(true, false, "teal", "Teal - Wikipedia", "web colors");
        main.addWorkOrder(true, true, "ginger", "Ginger - Wikipedia");

        // ----------IMPORTANT-------------
        // The search term changes use depending on if the flag is set or not, if it is set to true the term
        // will be used by the search engine, if false it will be entered into the address bar for navigation.

        try {
            main.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        main.setStartPage("http://www.google.com.br/search?q=ferrari&num=100");

        // By giving a custom start page and a true value as the isSearch value, then giving it a null value
        // in the search parameter you can achieve some special functionality as shown below.
        main.addWorkOrder(true, true, null, "Official Ferrari website", "Heritage Collection");

        // The following call signature allows you to specify that the bot opens each link clicked link
        // in a new tab. So on click it will open in a new tab each time.
        // This allows the origin page to stay up and links to be clicked from it until the list is exhausted.
        main.addWorkOrder(true, true, true, null, "Official Ferrari website",
                "Ferrari | Webmotors",
                " Sergio Marchionne");

        // If the above does not work because the new page is brought into focus the following call
        // signature will force the focus to the left most tab.
        // the forth boolean value indicates that this is a special case and needs to have the
        // focus brought manually back to the left.
        main.addWorkOrder(true, true, true, true, null, "Official Ferrari website",
                "Ferrari | Webmotors",
                " Sergio Marchionne");


        // Running the run command will open a new browser window, each run command will exhaust the stack of jobs
        // this means the first job added to the stack will be the last one off.
        try {
            main.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // The bellow will toggle the incognito state of the browser, it will only effect newly opened browsers
        //main.toggleIncognito();

        main.setStartPage("https://www.google.com.br");

        // the example below will open the start page and then enter in the search term. Once the term is
        // entered it will then open the links in a new tab and stay focused on the main tab.
        // once it exhausts the links it will terminate.
        main.addWorkOrder(true, true, false, true, "ferrari", "Official Ferrari website", "Ferrari | Webmotors");

        try {
            main.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
            Old functionality of the program from 1.3.4 can still be used if wanted,
            This is accomplished by doing the following



        configManager manager = new configManager();

        try {
            manager.loadConfig();
        } catch (IOException io) {
            manager.createConfig();
            manager.loadConfig();
        }

        robot bot = new robot(manager.mouseClickDelay, manager.keyboardPressDelay);

        webHandler browser = new webHandler(manager.browserLocation);
        browser.openBrowser(browser.getTestPage());

        Thread.sleep(manager.browserOpenDelay);

        boolean found = false;
        while (!found) {
            try {
                bot.getRGB(browser, manager);
                found = true;
            } catch (NoSuchElementException ex) {
                ex.printStackTrace();
                System.out.println("Failed to locate test page, trying");
                Thread.sleep(manager.retryDelay);
            }
        }

        Thread.sleep(manager.browserOpenDelay);

        try (BufferedReader bFile = new BufferedReader(new FileReader("topics.txt"))) {
            for (String line; (line = bFile.readLine()) != null; ) {
                String[] tmp = line.split(",");
                bot.run(manager, tmp[0] + "\n", Arrays.copyOfRange(tmp, 1, tmp.length));
                Thread.sleep(manager.loadPageDelay);
            }
        } finally {
            browser.closeBrowser();
        }
         */
    }
}