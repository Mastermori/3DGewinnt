package com.d3gewinnt.map;

import com.d3gewinnt.Main;
import de.mathlib.Vector2;
import de.mathlib.Vector3;

public class Field {

    //---------- VARIABLES ----------
    private static Main pa = Main.inst;

    private Vector3 pos;
    private int player;

    //---------- CONSTRUCTOR ----------
    Field(int x, int y, int z) {
        pos = new Vector3(x, y, z);
    }

    //---------- DRAW ----------

    /**
     * Draws a 2D representation of this field (Square / Rectangle)
     * @param x position to draw at
     * @param y position to draw at
     * @param size to draw with (rectangle dimensions)
     */
    void draw2D(float x, float y, Vector2 size) {
        pa.fill(Main.playerColors[player]);
        pa.rect(x, y, size.x, size.y);
    }

    /**
     * Draws a 3D representation of this field (Cube)
     * @param x position to draw at
     * @param y position to draw at
     * @param z position to draw at
     * @param size to draw with (box dimensions)
     * @param boxColor outline color (color of the hollow box, not the player mark)
     */
    void draw3D(float x, float y, float z, int size, int boxColor) {
        pa.pushMatrix(); //Push matrix to draw stack - changes made here are restored after popMatrix()
        pa.translate(x, y, z);
        if(!Main.hideBoxes) {
            pa.stroke(boxColor);
            pa.noFill();
            drawBox(size); //Draw the outline (box)
        }
        if(player != 0) { //If the field is not empty, draw a player mark in it
            pa.strokeWeight(3);
            pa.stroke(pa.color(Main.playerColors[player], 100));
            //drawBox(size);
            drawCross(size/2);
            pa.strokeWeight(2);
        }
        pa.popMatrix(); //Pops the matrix from the stack - reverts any changes made in terms of translation etc.
    }

    /**
     * Draws a 3D Cross at from (-size, -size, -size) to (size, size, size) with the origin being (0, 0, 0) (translate to move)
     * @param size of the cross (actual width and height are size*2 - see above)
     */
    private void drawCross(int size) {
        pa.beginShape(); //Begin drawing a shape to append vertices to it
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
        pa.endShape(); //Finish the shape
    }

    /**
     * Draws a box with a centered origin of (0, 0, 0)
     * @param size length of all edges
     */
    private void drawBox(int size) {
        pa.box(size);
    }

    //---------- GETTER/SETTER ----------
    private boolean isFree() {
        return player == 0;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        if (isFree()) {
            this.player = player;
        }
    }

}
