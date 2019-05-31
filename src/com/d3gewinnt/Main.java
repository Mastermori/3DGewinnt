package com.d3gewinnt;

import com.d3gewinnt.input.KeyBind;
import com.d3gewinnt.input.KeyCodeAction;
import com.d3gewinnt.input.KeyboardInput;
import com.d3gewinnt.map.Field;
import com.d3gewinnt.map.Map;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.awt.event.KeyEvent;

public class Main extends PApplet {

    public static Main inst;

    public static int[] playerColor;
    private static int player;
    private Map map;
    public static int layer;

    private int currWidth, currHeight;

    public static float rotX, rotY;
    private static float rotInc;

    public static float offX, offY, offZ;

    private static int dragMouseX, dragMouseY;

    public void setup() {
        //Request focus for key inputs asap
        frame.requestFocusInWindow();

        noStroke();
        frameRate(120);

        currWidth = width;
        currHeight = height;

        rotInc = 0.5f;

        playerColor = new int[]{color(200), color(255, 0, 0), color(0, 255, 0), color(0, 0, 255)};

        KeyboardInput.addBind(new KeyBind("rot-left", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_A),
                (keys) -> rotate(0, rotInc)));
        KeyboardInput.addBind(new KeyBind("rot-right", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_D),
                (keys) -> rotate(0, -rotInc)));
        KeyboardInput.addBind(new KeyBind("rot-up", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_W),
                (keys) -> rotate(rotInc, 0)));
        KeyboardInput.addBind(new KeyBind("rot-down", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_S),
                (keys) -> rotate(-rotInc, 0)));

        KeyboardInput.addBind(new KeyBind("move-left", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_LEFT),
                (keys) -> move(-1, 0, 0)));
        KeyboardInput.addBind(new KeyBind("move-right", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_RIGHT),
                (keys) -> move(1, 0, 0)));
        KeyboardInput.addBind(new KeyBind("move-up", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_UP),
                (keys) -> move(0, -1, 0)));
        KeyboardInput.addBind(new KeyBind("move-down", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_DOWN),
                (keys) -> move(0, 1, 0)));
        KeyboardInput.addBind(new KeyBind("move-fwd", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_PERIOD),
                (keys) -> move(0, 0, -1)));
        KeyboardInput.addBind(new KeyBind("move-bck", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_COMMA),
                (keys) -> move(0, 0, 1)));

        registerMethod("pre", this);

        reset();
    }

    public void pre() {
        if (currWidth != width || currHeight != height) {
            reset();
            currWidth = width;
            currHeight = height;
        }
    }

    private void reset() {
        map = new Map(3, 3, 3, 3, width / 2 - 10, width / 2 - 10);
        layer = 0;
        player = 1;
        rotX = 0;
        rotY = 0;
    }

    public void draw() {
        background(0);
        KeyboardInput.update();
        drawLeftSide();
        drawRightSide();
    }

    private void drawLeftSide() {
        map.draw3D();
    }

    private void drawRightSide() {
        map.draw2D(width / 2, height / 2 - (width / 4), layer);
    }

    public void keyPressed() {
        KeyboardInput.registerKeyPress(keyCode, key);
        if (key == '+' || key == 'q') {
            layer = min(layer + 1, (int) map.getSize().z - 1);
        } else if (key == '-' || key == 'e') {
            layer = max(layer - 1, 0);
        } else if (key == 'r') {
            reset();
        }
        if (keyCode >= 129 && keyCode <= 137) {
            int num = keyCode - 129;
            int fX = num % 3;
            int fY = 2 - num / 3;
            makeTurn(fX, fY);
        }
    }

    private void rotate(float x, float y) {
        rotX = (rotX + x + 100) % 100;
        rotY = (rotY + y + 100) % 100;
    }

    private void move(float x, float y, float z) {
        offX = min(offX + x, width / 2);
        offY = min(offY + y, height);
        offZ = offZ + z;
    }

    public void keyReleased() {
        KeyboardInput.registerKeyRelease(keyCode, key);
    }

    public void mouseDragged() {
        if (mouseX < width / 2) {
            rotate(-(dragMouseY - mouseY) * 0.1f, 0);
            rotate(0, (dragMouseX - mouseX) * 0.1f);
        }
        dragMouseX = mouseX;
        dragMouseY = mouseY;
    }

    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();
        if (e > 0) {
            layer = min(layer + 1, (int) map.getSize().z - 1);
        } else if (e < 0) {
            layer = max(layer - 1, 0);
        }
    }

    private void makeTurn(int x, int y) {
        Field f = map.getField(x, y, layer);
        if (f.getPlayer() != 0)
            return;
        f.setPlayer(player);
        player = player % (playerColor.length - 1) + 1;
        int won;
        if ((won = map.checkWin()) != 0) {
            System.out.println("Player " + won + " won!");
            reset();
        }
    }

    public void mousePressed() {
        if (mouseX < width / 2) {
            dragMouseX = mouseX;
            dragMouseY = mouseY;
        }
        if (mouseX > width / 2) {
            if (mouseButton == LEFT) {
                int fX = (int) ((mouseX - width / 2) / map.getFieldSize().x);
                int fY = (int) ((mouseY - (height / 2 - (width / 4))) / map.getFieldSize().y);
                System.out.println("X: " + fX + " Y: " + fY);
                if (fX >= 0 && fX < map.getSize().x && fY >= 0 && fY < map.getSize().y)
                    makeTurn(fX, fY);
            }
        }
    }


    public void settings() {
        size(1200, 800, P3D);
        //fullScreen(P3D);
        smooth(4);
    }

    public Main() {
        inst = this;
    }

    public static void main(String[] args) {
        PApplet.main("com.d3gewinnt.Main");
        //playerColor = new int[Integer.parseInt(args[0])];
    }

}
