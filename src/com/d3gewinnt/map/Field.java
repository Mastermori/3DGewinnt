package com.d3gewinnt.map;

import de.mathlib.Vector3;

public class Field {

    Vector3 pos;
    int player;


    public Field(int x, int y, int z) {
        pos = new Vector3(x, y, z);
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
