package com.d3gewinnt;

import com.d3gewinnt.map.Field;
import com.d3gewinnt.map.Map;
import processing.core.PApplet;

public class Main extends PApplet {

    public static Main inst;

    public static int[] playerColor;
    public static int player;
    Map map;
    static int layer;

    public static float rotX, rotY;
    private static float rotInc;

    public void setup() {
        //Request focus for key inputs asap
        frame.requestFocusInWindow();

        noStroke();
        frameRate(120);

        rotInc = 0.5f;

        playerColor = new int[]{color(200), color(255, 0, 0), color(0, 255, 0), color(0, 0, 255)};

        reset();
    }

    public void reset() {
        map = new Map(3, 3, 3, 3, width/2-10, width/2-10);
        layer = 0;
        player = 1;
    }

    public void draw() {
        background(0);
        drawLeftSide();
        drawRightSide();
    }

    public void drawLeftSide() {
        map.draw3D();
    }

    public void drawRightSide() {
        int fWidth = width/2-10;
        map.draw2D(width/2, height/2-(fWidth/2), layer);
    }

    public void keyPressed() {
        if (key == '+') {
            layer = min(layer + 1, (int) map.getSize().z - 1);
        } else if (key == '-') {
            layer = max(layer - 1, 0);
        }else if (key == 'w') {
            rotX = (rotX + rotInc) % 100;
        } else if (key == 's') {
            rotX = (rotX - rotInc + 100) % 100;
        } else if (key == 'd') {
            rotY = (rotY + rotInc) % 100;
        } else if (key == 'a') {
            rotY = (rotY - rotInc + 100) % 100;
        }
        if (keyCode >= 129 && keyCode <= 137) {
            int num = keyCode - 129;
            int fX = num % 3;
            int fY = num  / 3;
            makeTurn(fX, fY);
        }

    }



    private void makeTurn(int x, int y) {
        Field f = map.getField(x, y, layer);
        if (f.getPlayer() != 0)
            return;
        f.setPlayer(player);
        player = player % (playerColor.length-1) + 1;
        int won;
        if ((won = map.checkWin()) != 0) {
            System.out.println("Player " + won + " won!");
            reset();
        }
    }

    public void mousePressed() {
        if (mouseButton == LEFT) {
            int fX = (int) ((mouseX - width/2) / map.getFieldSize().x);
            int fY = (int) (mouseY / map.getFieldSize().y);
            System.out.println("X: " + fX + " Y: " + fY);
            if(fX >= 0 && fX < map.getSize().x && fY >= 0 && fY < map.getSize().y)
                makeTurn(fX, fY);
        }
    }


    public void settings() {
        size(1200, 600, P3D);
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
