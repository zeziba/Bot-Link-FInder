package com.regcom;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by cengen on 8/15/17.
 */

public class robot {

    private static int color;
    private Mouse mouse;
    private Keyboard keyboard;
    private Robot bot;
    private Screen screen;
    private int botDelay;

    public robot(int delay, int kDelay) {
        try {
            setBot(new Robot());
        } catch (AWTException e) {
            e.printStackTrace();
        }
        setBotDelay(delay);
        setMouse(new Mouse());
        setKeyboard(new Keyboard(kDelay));
        setScreen(new Screen());
    }

    private void moveTo(int x, int y, double secondsToMove, Robot b, Mouse m) {
        int mDelay = 1000 / 60;
        double iterations = 60 * secondsToMove;

        double[] mpos = m.getPos();

        double dx, dy;
        double mx = mpos[0], my = mpos[1];

        dx = (x - mx) / iterations;
        dy = (y - my) / iterations;

        for (int step = 1; step <= (int)iterations; step++) {
            try {
                Thread.sleep(mDelay);
                b.mouseMove((int) (mx + dx * step), (int) (my + dy * step));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        b.mouseMove(x, y);

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            this.moveToCheck(x, y, m);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to move mouse, check that everything is compiled correctly and that\n" +
                    "Java is able to achieve control over the mouse.\nExiting the program now.");
            System.exit(-1);
        }
    }

    private void moveToCheck(double x, double y, Mouse m) throws Exception {
        double[] pos = m.getPos();

        if (x != pos[0] && y != pos[1])
            throw new Exception("Failed to move the mouse to correct position");
    }

    public void typeText(Keyboard k, Robot b) {
        while (!k.getTypeWords().isEmpty())
            try {
                k.type(k.getWord(), b);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private Mouse getMouse() {
        return mouse;
    }

    private void setMouse(Mouse mouse) {
        this.mouse = mouse;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    private void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public Robot getBot() {
        return bot;
    }

    private void setBot(Robot bot) {
        this.bot = bot;
    }

    private int getBotDelay() {
        return botDelay;
    }

    private void setBotDelay(int botDelay) {
        this.botDelay = botDelay;
    }

    private Screen getScreen() {
        return screen;
    }

    private void setScreen(Screen screen) {
        this.screen = screen;
    }

    void getRGB(webHandler brow, configManager manager) throws InterruptedException, NoSuchElementException {
        robot bot = new robot(manager.mouseClickDelay, manager.keyboardPressDelay);
        bot.getScreen().takeCapture(bot.getBot());
        int[] fp = bot.getScreen().findColor(brow.getTestPageColor().getRGB(), 0);
        bot.moveTo(fp[0], fp[1], manager.mouseMoveTime, bot.getBot(), bot.getMouse());
        // Find the color that is used by the system during highlighting.
        Thread.sleep(manager.commandDelay);
        bot.getKeyboard().escape(bot.getBot());
        Thread.sleep(manager.commandDelay);
        bot.getKeyboard().ctrlF(bot.getBot());
        Thread.sleep(manager.commandDelay);
        bot.getKeyboard().backpace(bot.getBot());
        Thread.sleep(manager.commandDelay);
        bot.getKeyboard().addWord("(TEST)");
        bot.typeText(bot.getKeyboard(), bot.getBot());
        Thread.sleep(manager.commandDelay);
        _getRGB(manager);
    }

    private void _getRGB(configManager manager) throws InterruptedException {

        Point M = MouseInfo.getPointerInfo().getLocation();

        double mx, my;
        mx = M.getX();
        my = M.getY();

        robot bot = new robot(manager.mouseClickDelay, manager.keyboardPressDelay);
        bot.moveTo(100, 100, manager.mouseMoveTime, bot.getBot(), bot.getMouse());

        Thread.sleep(manager.commandDelay);

        bot.getScreen().takeCapture(bot.getBot());
        color = bot.getScreen().getRobotScreen().getRGB((int) mx, (int) my);
    }

    void run(configManager manager, String search, String... topics) throws InterruptedException {
        robot _bot = new robot(manager.mouseClickDelay, manager.keyboardPressDelay);
        Thread.sleep(manager.commandDelay);
        _bot.getKeyboard().ctrlL(_bot.getBot());
        _bot.getKeyboard().addWord("google.com\n");
        _bot.typeText(_bot.getKeyboard(), _bot.getBot());
        Thread.sleep(manager.loadPageDelay);
        _bot.getKeyboard().backpace(_bot.getBot());
        Thread.sleep(manager.commandDelay);
        _bot.getKeyboard().addWord(search + "\n");
        _bot.typeText(_bot.getKeyboard(), _bot.getBot());
        Thread.sleep(manager.loadPageDelay);
        try {
            for (String t : topics) {
                clickLink(t, manager);
                Thread.sleep(manager.loadPageDelay);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Failed to complete link chain, moving on to next entry.");
        }
    }

    void clickLink(String link, configManager manager) throws Exception {
        _clickLink(link, manager.retryAttemptsScreenFind, 0, manager, false);
    }

    void clickLink(String link, configManager manager, boolean ctrlClick) throws Exception {
        _clickLink(link, manager.retryAttemptsScreenFind, 0, manager, ctrlClick);
    }

    private void _clickLink(String link, int retires, int attempt, configManager manager, boolean ctrlClick) throws Exception {
        robot bot = new robot(manager.mouseClickDelay, manager.keyboardPressDelay);
        bot.getKeyboard().escape(bot.getBot());
        bot.getKeyboard().ctrlF(bot.getBot());
        Thread.sleep(manager.commandDelay);
        bot.getKeyboard().backpace(bot.getBot());
        Thread.sleep(manager.commandDelay);
        bot.getKeyboard().addWord((attempt > 0) ? link + "\n" : link);
        bot.typeText(bot.getKeyboard(), bot.getBot());
        try {
            Thread.sleep(manager.commandDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bot.getScreen().takeCapture(bot.getBot());
        int[] fp = {};
        try {
            //System.out.println(String.format("Scanning for color: RED: %s GREEN: %s BLUE: %s", red, green, blue));
            //fp = bot.getScreen().findColor(red, green, blue);
            fp = bot.getScreen().findColor(color, attempt);
        } catch (NoSuchElementException ex) {
            if (fp == null) {
                if (attempt < retires) {
                    System.out.println(attempt);
                    Thread.sleep(manager.loadPageDelay);
                    _clickLink(link, retires, attempt + 1, manager, ctrlClick);
                } else if (attempt == retires + 1) {
                    bot.getKeyboard().addWord(link + "\n");
                    bot.typeText(bot.getKeyboard(), bot.getBot());
                    Thread.sleep(manager.loadPageDelay);
                } else
                    throw new Exception("Link Not Found");
            }
        }
        assert fp != null;
        bot.moveTo(fp[0], fp[1], manager.mouseMoveTime, bot.getBot(), bot.getMouse());
        if (ctrlClick) {
            bot.getBot().keyPress(KeyEvent.VK_CONTROL);
            bot.getBot().delay(bot.getKeyboard().getKeyDelay());
        }
        bot.getMouse().click(bot.getBot());
        if (ctrlClick)
            bot.getBot().keyRelease(KeyEvent.VK_CONTROL);
    }

    public class Screen {
        private BufferedImage robotScreen;
        private LinkedList colorPos;
        private int width;
        private int height;
        private boolean hasAlpha;
        private byte[] pixels;
        private LinkedList<Point> pixelLocations;
        private int pixelLength;


        Screen() {
            setRobotScreen(null);
        }

        void takeCapture(Robot b) {
            setRobotScreen(b.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())));
            //this.pixels = ((DataBufferByte) getRobotScreen().getRaster().getDataBuffer()).getData();
            this.width = getRobotScreen().getWidth();
            this.height = getRobotScreen().getHeight();
            this.hasAlpha = getRobotScreen().getAlphaRaster() != null;
            this.pixelLength = (this.hasAlpha) ? 4 : 3;
            this.pixelLocations = new LinkedList<>();
        }

        int[] findColor(int color, int _i) throws NoSuchElementException {
            int[] xCoords = new int[robotScreen.getWidth()], yCoords = new int[getRobotScreen().getHeight()];
            if (robotScreen != null) {
                for (int y = 0; y < this.height; y++)
                    for (int x = 0; x < this.width; x++) {
                        final int clr = getRobotScreen().getRGB(x, y);
                        if (color == clr) {
                            this.pixelLocations.push(new Point(x, y));
                            xCoords[x]++;
                            yCoords[y]++;
                        } else
                            getRobotScreen().setRGB(x, y, 0);
                    }
            }

            Collections.sort(this.pixelLocations, new PointComapare());

            for (int i = 0; i < 15; i++) {
                this.pixelLocations.removeFirst();
                this.pixelLocations.removeLast();
            }

            int midX = (this.pixelLocations.getFirst().getX() + this.pixelLocations.getLast().getX()) / 2;
            int midY = (this.pixelLocations.getFirst().getY() + this.pixelLocations.getLast().getY()) / 2;
            System.out.println(String.format("Moved To: %d %d", midX, midY));

            getRobotScreen().setRGB(midX, midY, 0xc6bcc6);

            try {
                File outPicture = new File(String.format("botVision%s.jpg", _i));
                ImageIO.write(getRobotScreen(), "jpg", outPicture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new int[]{midX, midY};
        }

        void printColors() {
            int i = 0;
            while (!pixelLocations.isEmpty()) {
                System.out.print(String.format("%s%s", this.pixelLocations.pop(), (i++ / 10 == 0) ? "\n" : ""));
            }
        }

        public BufferedImage getRobotScreen() {
            return robotScreen;
        }

        public void setRobotScreen(BufferedImage robotScreen) {
            this.robotScreen = null;
            this.robotScreen = robotScreen;
        }

        public LinkedList getColorPos() {
            return colorPos;
        }

        private class PointComapare implements Comparator<Point> {
            public int compare(Point a, Point b) {
                if (a.x < b.x) {
                    return -1;
                } else if (a.x > b.x) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        private class Point {
            int x, y;

            public Point(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }

            @Override
            public String toString() {
                return "Point{" +
                        "x=" + x +
                        ", y=" + y +
                        '}';
            }
        }
    }

    public class Mouse {
        Mouse() {
        }

        void click(Robot b) {
            leftClick(b);
            b.delay(getBotDelay());
            leftRelease(b);
        }

        double[] getPos() {
            Point m = MouseInfo.getPointerInfo().getLocation();

            return new double[]{m.getX(), m.getY()};
        }

        void leftClick(Robot b) {
            b.mousePress(InputEvent.BUTTON1_MASK);
        }

        void leftRelease(Robot b) {
            b.mouseRelease(InputEvent.BUTTON1_MASK);
        }

        void rightClick(Robot b) {
            b.mousePress(InputEvent.BUTTON2_MASK);
        }

        void rightRelease(Robot b) {
            b.mouseRelease(InputEvent.BUTTON2_MASK);
        }
    }

    public class Keyboard {
        private LinkedList<String> typeWords;
        private int keyDelay;

        Keyboard(int d) {
            setKeyDelay(d);
            typeWords = new LinkedList<>();
        }

        int[] helper(int[] values) {
            return values;
        }

        int[] press(char character) {
            int v[];
            switch (character) {
                case 'a':
                    v = helper(new int[]{KeyEvent.VK_A});
                    break;
                case 'b':
                    v = helper(new int[]{KeyEvent.VK_B});
                    break;
                case 'c':
                    v = helper(new int[]{KeyEvent.VK_C});
                    break;
                case 'd':
                    v = helper(new int[]{KeyEvent.VK_D});
                    break;
                case 'e':
                    v = helper(new int[]{KeyEvent.VK_E});
                    break;
                case 'f':
                    v = helper(new int[]{KeyEvent.VK_F});
                    break;
                case 'g':
                    v = helper(new int[]{KeyEvent.VK_G});
                    break;
                case 'h':
                    v = helper(new int[]{KeyEvent.VK_H});
                    break;
                case 'i':
                    v = helper(new int[]{KeyEvent.VK_I});
                    break;
                case 'j':
                    v = helper(new int[]{KeyEvent.VK_J});
                    break;
                case 'k':
                    v = helper(new int[]{KeyEvent.VK_K});
                    break;
                case 'l':
                    v = helper(new int[]{KeyEvent.VK_L});
                    break;
                case 'm':
                    v = helper(new int[]{KeyEvent.VK_M});
                    break;
                case 'n':
                    v = helper(new int[]{KeyEvent.VK_N});
                    break;
                case 'o':
                    v = helper(new int[]{KeyEvent.VK_O});
                    break;
                case 'p':
                    v = helper(new int[]{KeyEvent.VK_P});
                    break;
                case 'q':
                    v = helper(new int[]{KeyEvent.VK_Q});
                    break;
                case 'r':
                    v = helper(new int[]{KeyEvent.VK_R});
                    break;
                case 's':
                    v = helper(new int[]{KeyEvent.VK_S});
                    break;
                case 't':
                    v = helper(new int[]{KeyEvent.VK_T});
                    break;
                case 'u':
                    v = helper(new int[]{KeyEvent.VK_U});
                    break;
                case 'v':
                    v = helper(new int[]{KeyEvent.VK_V});
                    break;
                case 'w':
                    v = helper(new int[]{KeyEvent.VK_W});
                    break;
                case 'x':
                    v = helper(new int[]{KeyEvent.VK_X});
                    break;
                case 'y':
                    v = helper(new int[]{KeyEvent.VK_Y});
                    break;
                case 'z':
                    v = helper(new int[]{KeyEvent.VK_Z});
                    break;
                case 'A':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_A});
                    break;
                case 'B':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_B});
                    break;
                case 'C':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_C});
                    break;
                case 'D':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_D});
                    break;
                case 'E':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_E});
                    break;
                case 'F':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_F});
                    break;
                case 'G':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_G});
                    break;
                case 'H':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_H});
                    break;
                case 'I':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_I});
                    break;
                case 'J':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_J});
                    break;
                case 'K':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_K});
                    break;
                case 'L':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_L});
                    break;
                case 'M':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_M});
                    break;
                case 'N':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_N});
                    break;
                case 'O':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_O});
                    break;
                case 'P':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_P});
                    break;
                case 'Q':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Q});
                    break;
                case 'R':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_R});
                    break;
                case 'S':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_S});
                    break;
                case 'T':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_T});
                    break;
                case 'U':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_U});
                    break;
                case 'V':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_V});
                    break;
                case 'W':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_W});
                    break;
                case 'X':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_X});
                    break;
                case 'Y':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Y});
                    break;
                case 'Z':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Z});
                    break;
                case '`':
                    v = helper(new int[]{KeyEvent.VK_BACK_QUOTE});
                    break;
                case '0':
                    v = helper(new int[]{KeyEvent.VK_0});
                    break;
                case '1':
                    v = helper(new int[]{KeyEvent.VK_1});
                    break;
                case '2':
                    v = helper(new int[]{KeyEvent.VK_2});
                    break;
                case '3':
                    v = helper(new int[]{KeyEvent.VK_3});
                    break;
                case '4':
                    v = helper(new int[]{KeyEvent.VK_4});
                    break;
                case '5':
                    v = helper(new int[]{KeyEvent.VK_5});
                    break;
                case '6':
                    v = helper(new int[]{KeyEvent.VK_6});
                    break;
                case '7':
                    v = helper(new int[]{KeyEvent.VK_7});
                    break;
                case '8':
                    v = helper(new int[]{KeyEvent.VK_8});
                    break;
                case '9':
                    v = helper(new int[]{KeyEvent.VK_9});
                    break;
                case '-':
                    v = helper(new int[]{KeyEvent.VK_MINUS});
                    break;
                case '=':
                    v = helper(new int[]{KeyEvent.VK_EQUALS});
                    break;
                case '~':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE});
                    break;
                case '!':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_1});
                    break;
                case '@':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_2});
                    break;
                case '#':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_3});
                    break;
                case '$':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_4});
                    break;
                case '%':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_5});
                    break;
                case '^':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_6});
                    break;
                case '&':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_7});
                    break;
                case '*':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_8});
                    break;
                case '(':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_9});
                    break;
                case ')':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_0});
                    break;
                case '_':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SUBTRACT});
                    break;
                case '+':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS});
                    break;
                case '\t':
                    v = helper(new int[]{KeyEvent.VK_TAB});
                    break;
                case '\n':
                    v = helper(new int[]{KeyEvent.VK_ENTER});
                    break;
                case '[':
                    v = helper(new int[]{KeyEvent.VK_OPEN_BRACKET});
                    break;
                case ']':
                    v = helper(new int[]{KeyEvent.VK_CLOSE_BRACKET});
                    break;
                case '\\':
                    v = helper(new int[]{KeyEvent.VK_BACK_SLASH});
                    break;
                case '{':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET});
                    break;
                case '}':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET});
                    break;
                case '|':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH});
                    break;
                case ';':
                    v = helper(new int[]{KeyEvent.VK_SEMICOLON});
                    break;
                case ':':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON});
                    break;
                case '\'':
                    v = helper(new int[]{KeyEvent.VK_QUOTE});
                    break;
                case '"':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE});
                    break;
                case ',':
                    v = helper(new int[]{KeyEvent.VK_COMMA});
                    break;
                case '<':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA});
                    break;
                case '.':
                    v = helper(new int[]{KeyEvent.VK_PERIOD});
                    break;
                case '>':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD});
                    break;
                case '/':
                    v = helper(new int[]{KeyEvent.VK_SLASH});
                    break;
                case '?':
                    v = helper(new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH});
                    break;
                case ' ':
                    v = helper(new int[]{KeyEvent.VK_SPACE});
                    break;
                default:
                    throw new IllegalArgumentException("Cannot type character " + character);
            }
            return v;
        }

        public void hitEnter(Robot b) {
            b.keyPress(KeyEvent.VK_ENTER);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_ENTER);
        }

        public void ctrlF(Robot b) {
            b.keyPress(KeyEvent.VK_CONTROL);
            b.delay(getKeyDelay());
            b.keyPress(KeyEvent.VK_F);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_CONTROL);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_F);
        }

        public void backpace(Robot b) {
            b.keyPress(KeyEvent.VK_BACK_SPACE);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_BACK_SPACE);
        }

        public void escape(Robot b) {
            b.keyPress(KeyEvent.VK_ESCAPE);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_ESCAPE);
        }

        public void f11(Robot b) {
            b.keyPress(KeyEvent.VK_F11);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_F11);
        }

        public void f6(Robot b) {
            b.keyPress(KeyEvent.VK_F6);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_F6);
        }

        public void ctrlL(Robot b) {
            b.keyPress(KeyEvent.VK_CONTROL);
            b.delay(getKeyDelay());
            b.keyPress(KeyEvent.VK_L);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_CONTROL);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_L);
        }

        public void ctrlT(Robot b) {
            b.keyPress(KeyEvent.VK_CONTROL);
            b.delay(getKeyDelay());
            b.keyPress(KeyEvent.VK_T);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_CONTROL);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_T);
        }

        public void singleQuote(Robot b) {
            b.keyPress(KeyEvent.VK_QUOTE);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_QUOTE);
        }

        public void alt1(Robot b) {
            b.keyPress(KeyEvent.VK_ALT);
            b.delay(getKeyDelay());
            b.keyPress(KeyEvent.VK_1);
            b.delay(getKeyDelay());
            b.keyRelease(KeyEvent.VK_1);
            b.keyRelease(KeyEvent.VK_ALT);
        }

        void type(String word, Robot b) throws Exception {
            if (word != null)
                for (char c : word.toCharArray()) {
                    int[] t = press(c);
                    for (int aT : t)
                        b.keyPress(aT);
                    b.delay(getKeyDelay());
                    for (int i = t.length - 1; i >= 0; i--)
                        b.keyRelease(t[i]);
                }
            b.delay(getKeyDelay());
        }

        public void addWord(String word) {
            getTypeWords().push(word);
        }

        public String getWord() {
            return getTypeWords().pop();
        }

        public LinkedList<String> getTypeWords() {
            return typeWords;
        }

        public int getKeyDelay() {
            return keyDelay;
        }

        public void setKeyDelay(int keyDelay) {
            this.keyDelay = keyDelay;
        }
    }
}
