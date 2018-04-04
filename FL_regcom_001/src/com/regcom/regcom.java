package com.regcom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Created by cengen on 8/20/17.
 */
public class regcom {

    private configManager manager;
    private robot bot;
    private webHandler web;
    private boolean hasBeenConFigured = false;
    private Stack<workOrder> workOrders;
    private String startPage;

    public regcom(String startPage) throws IOException {
        this.manager = new configManager();

        try {
            this.manager.loadConfig();
        } catch (IOException io) {
            this.manager.createConfig();
            this.manager.loadConfig();
        }

        this.bot = new robot(this.manager.mouseClickDelay, this.manager.keyboardPressDelay);
        this.web = new webHandler(this.manager.browserLocation);

        this.workOrders = new Stack<>();

        this.startPage = startPage;
    }

    public workOrder lastWorkOrder() {
        return this.workOrders.peek();
    }

    public void addWorkOrder(boolean isSearch, boolean stayOpen, String launchOrder, String... work) {
        this._addwork(isSearch, stayOpen, false, false, launchOrder, work);
    }

    public void addWorkOrder(boolean isSearch, boolean stayOpen, boolean clickOpen, String launchOrder, String... work) {
        this._addwork(isSearch, stayOpen, clickOpen, false, launchOrder, work);
    }

    public void addWorkOrder(boolean isSearch, boolean stayOpen, boolean isSpecialCase, boolean clickOpen, String launchOrder, String... work) {
        this._addwork(isSearch, stayOpen, clickOpen, isSpecialCase, launchOrder, work);
    }

    public void addWorkOrder(boolean isSearch, String launchOrder, String... work) {
        this._addwork(isSearch, true, false, false, launchOrder, work);
    }

    private void _addwork(boolean isSearch, boolean stayOpen, boolean clickOpen, boolean isSpecialCase, String launchOrder, String[] work) {
        this.workOrders.push(new workOrder(isSearch, stayOpen, clickOpen, isSpecialCase, launchOrder, work));
    }

    private Stack<workOrder> getWorkOrders() {
        return workOrders;
    }

    public void setWorkOrders(Stack<workOrder> workOrders) {
        this.workOrders = workOrders;
    }

    public void run() throws Exception {
        if (this.hasBeenConFigured) {
            this.web.openBrowser();
            Thread.sleep(this.manager.loadPageDelay);
            this.bot.getKeyboard().escape(this.bot.getBot());
            while (!this.getWorkOrders().isEmpty()) {
                workOrder order = workOrders.pop();
                if (!order.stayOpen()) {
                    this.bot.getKeyboard().ctrlT(this.bot.getBot());
                    Thread.sleep(this.manager.commandDelay);
                }
                if (order.isSearch) {
                    this.bot.getKeyboard().ctrlL(this.bot.getBot());
                    Thread.sleep(this.manager.commandDelay);
                    this.bot.getKeyboard().addWord(this.startPage + "\n");
                    this.bot.typeText(this.bot.getKeyboard(), this.bot.getBot());
                    Thread.sleep(this.manager.loadPageDelay);
                    this.bot.getKeyboard().addWord(order.getLaunchOrder());
                    this.bot.typeText(this.bot.getKeyboard(), this.bot.getBot());
                    Thread.sleep(this.manager.loadPageDelay);
                } else {
                    this.bot.getKeyboard().ctrlL(this.bot.getBot());
                    Thread.sleep(this.manager.commandDelay);
                    this.bot.getKeyboard().addWord(order.getLaunchOrder());
                    this.bot.typeText(this.bot.getKeyboard(), this.bot.getBot());
                    Thread.sleep(this.manager.loadPageDelay);
                }
                try {
                    for (String page : order.getWork()) {
                        this.bot.clickLink(page, this.manager, order.isClickOpen());
                        Thread.sleep(this.manager.loadPageDelay);
                        if (order.isSpecialCase())
                            this.bot.getKeyboard().alt1(this.bot.getBot()); // brings the focus to the left most tab
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Failed to complete link chain, moving on to next entry.\n" + order.getWork());
                }
            }
            this.web.closeBrowser();
        } else
            throw new Exception("System has not been configured, run <regcom>.setUp() first");
    }

    public void setUp() {

        this.web.openBrowser(this.web.getTestPage(manager.testFileBaseLocation));

        try {
            Thread.sleep(this.manager.browserOpenDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean found = false;
        while (!found) {
            try {
                this.bot.getRGB(this.web, this.manager);
                found = true;
            } catch (NoSuchElementException | InterruptedException ex) {
                ex.printStackTrace();
                System.out.println("Failed to locate test page, trying");
                try {
                    Thread.sleep(manager.retryDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        this.hasBeenConFigured = true;

        this.web.closeBrowser();
    }

    public void setStartPage(String page) {
        this.startPage = page;
    }

    private class workOrder {
        private boolean stayOpen;
        private boolean isSearch;
        private boolean clickOpen;
        private boolean isSpecialCase;
        private String launchOrder;
        private ArrayList<String> work;

        public workOrder(boolean isSearch, String launchOrder, String[] work) {
            this._workOrder(isSearch, true, false, false, launchOrder, work);
        }

        public workOrder(boolean isSearch, boolean stayOpen, String launchOrder, String[] work) {
            this._workOrder(isSearch, stayOpen, false, false, launchOrder, work);
        }

        public workOrder(boolean isSearch, boolean stayOpen, boolean clickOpen, String launchOrder, String[] work) {
            this._workOrder(isSearch, stayOpen, clickOpen, false, launchOrder, work);
        }

        public workOrder(boolean isSearch, boolean stayOpen, boolean clickOpen, boolean isSpecialCase, String launchOrder, String[] work) {
            this._workOrder(isSearch, stayOpen, clickOpen, isSpecialCase, launchOrder, work);
        }

        private void _workOrder(boolean isSearch, boolean stayOpen, boolean clickOpen, boolean isSpecialCase, String launchOrder, String[] work) {
            this.work = new ArrayList<>();
            this.stayOpen = stayOpen;
            this.launchOrder = launchOrder + "\n";
            this.isSearch = isSearch;
            this.clickOpen = clickOpen;
            Collections.addAll(this.work, work);
        }

        public boolean stayOpen() {
            return stayOpen;
        }

        public String getLaunchOrder() {
            return launchOrder;
        }

        public void setLaunchOrder(String launchOrder) {
            this.launchOrder = launchOrder;
        }

        public ArrayList<String> getWork() {
            return work;
        }

        public void setWork(ArrayList<String> work) {
            this.work = work;
        }

        public boolean isClickOpen() {
            return clickOpen;
        }

        public void setClickOpen(boolean clickOpen) {
            this.clickOpen = clickOpen;
        }

        public boolean isStayOpen() {
            return stayOpen;
        }

        public void setStayOpen(boolean stayOpen) {
            this.stayOpen = stayOpen;
        }

        public boolean isSearch() {
            return isSearch;
        }

        public void setSearch(boolean search) {
            isSearch = search;
        }

        public boolean isSpecialCase() {
            return isSpecialCase;
        }

        public void setSpecialCase(boolean specialCase) {
            isSpecialCase = specialCase;
        }
    }

    public void toggleIncognito() {
        this.web.toggleIncognito();
    }
}
