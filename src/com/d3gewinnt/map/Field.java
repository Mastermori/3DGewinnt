package com.d3gewinnt.map;

import com.d3gewinnt.Main;
import de.mathlib.Vector2;
import de.mathlib.Vector3;

public class Field {

    private static Main pa = Main.inst;

    Vector3 pos;
    int player;

    public Field(int x, int y, int z) {
        pos = new Vector3(x, y, z);
    }

    public void draw2D(float xOff, float yOff, Vector2 size) {
        float x = pos.x + xOff;
        float y = pos.y + yOff;
        pa.fill(Main.playerColor[player]);
        pa.rect(x, y, size.x, size.y);
    }

    public void draw3D(int x, int y, int z, int size, int boxColor) {
        pa.translate(x, y, z);
        pa.stroke(boxColor);
        drawBox(size);
        if(player != 0) {
            pa.stroke(Main.playerColor[player]);
            drawCross(size/2);
        }
        pa.translate(-x, -y, -z);
    }

    private void drawCross(int size) {
        pa.beginShape();
        pa.vertex(-size, -size, -size);
        pa.vertex(0, 0, 0);
        pa.vertex(size, -size, -size);
        pa.vertex(0, 0, 0);
        pa.vertex(-size, size, -size);
        pa.vertex(0, 0, 0);
        pa.vertex(-size, -size, size);
        pa.vertex(0, 0, 0);
        pa.vertex(size, size, -size);
        pa.vertex(0, 0, 0);
        pa.vertex(size, -size, size);
        pa.vertex(0, 0, 0);
        pa.vertex(-size, size, size);
        pa.vertex(0, 0, 0);
        pa.vertex(size, size, size);
        pa.endShape();
    }

    private void drawBox(int size) {
        pa.box(size);
    }


    public boolean isFree() {
        return player == 0;
    }

    public int getPlayer() {
        return player;
    }

    public boolean setPlayer(int player) {
        if (isFree()) {
            this.player = player;
            return true;
        }
        return false;
    }

}
