package com.d3gewinnt;

import com.d3gewinnt.map.Map;
import processing.core.PApplet;

public class Main extends PApplet {

    public static Main inst;

    public static int[] players;
    Map map;

    public void setup() {
        //Request focus for key inputs asap
        frame.requestFocusInWindow();

        map = new Map(3, 3, 3, 3);

        noStroke();
        smooth(4);
        frameRate(120);
    }

    public void draw() {
        background(255);
        drawLeftSide();
        drawRightSide();
    }

    public void drawLeftSide() {

    }

    public void drawRightSide() {
        map.draw2D(width/2, height/2-(width/4), width/2, width/2, 0);
    }


    public void settings() {
        size(600, 600);
    }

    public Main() {
        inst = this;
    }

    public static void main(String[] args) {
        PApplet.main("com.d3gewinnt.Main", new String[]{"P3D"});
        players = new int[Integer.parseInt(args[0])];
    }

}
