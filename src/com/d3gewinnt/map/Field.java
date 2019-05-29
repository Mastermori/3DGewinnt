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

    public void draw3D(int x, int y, int z) {

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
