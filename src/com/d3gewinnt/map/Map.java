package com.d3gewinnt.map;

import com.d3gewinnt.Main;
import de.mathlib.Vector2;
import de.mathlib.Vector3;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class Map {

    //---------- VARIABLES ----------
    private static Main pa = Main.inst;

    private List<Field> fields;
    private Vector3 size;
    private int winLength;
    private int width, height;
    private Vector3 fieldSize;

    //---------- CONSTRUCTOR ----------
    public Map(int xSize, int ySize, int zSize, int winLength, int width, int height) {
        fields = new ArrayList<>();
        size = new Vector3(xSize, ySize, zSize);
        this.winLength = winLength;
        this.width = width;
        this.height = height;
        fieldSize = new Vector3(width / size.x, height / size.y, 100); //Calculates the fieldSize from the available space and the needed amount of fields
        //Adds an empty Field to every spot on the map
        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                for (int z = 0; z < size.z; z++) {
                    fields.add(new Field(x, y, z));
                }
            }
        }
    }

    //---------- DRAW ----------

    /**
     * Draws a 2D representation of the map
     *
     * @param x     position to draw at
     * @param y     position to draw at
     * @param layer to draw
     */
    public void draw2D(int x, int y, int layer) {
        pa.noFill();
        pa.stroke(125);
        for (int i = 0; i < size.x; i++) {
            for (int j = 0; j < size.y; j++) {
                Field f = getField(i, j, layer);
                f.draw2D(x + i * fieldSize.x, y + j * fieldSize.y, new Vector2(fieldSize.x, fieldSize.y));
            }
        }
    }

    /**
     * Draws a 3D representation of the map (has no positional arguments - pulls them directly from Main)
     */
    public void draw3D() {
        pa.pushMatrix();
        pa.noFill();
        pa.stroke(255, 0, 0);

        //Translates the map to the offSet set in Main
        //noinspection IntegerDivisionInFloatingPointContext
        pa.translate(width / 2 + Main.offX, height / 3 * 2 + Main.offY, (PApplet.max(size.x, size.y, size.z) - 3) * -fieldSize.z * 2 + Main.offZ);

        //Calculate the x and y rotation from the rotation set in Main
        float rotX = (Main.rotX / 100.0f) * -2 * pa.PI + pa.PI;
        float rotY = (Main.rotY / 100.0f) * 2 * pa.PI - pa.PI;
        //Rotate the cube around the x and y axis
        pa.rotateX(rotX);
        pa.rotateY(rotY);

        //Loop through all fields in the map and draw them in 3D
        for (int y = 0; y < size.y; y++) {
            for (int x = 0; x < size.x; x++) {
                for (int z = 0; z < size.z; z++) {
                    if (Main.hideLayers && Main.layer != z) //If the layer which is to be drawn not the selected layer and hideLayers is active - skip drawing them
                        continue;
                    //Get the field current field and calculate its position in 3D space (relative to the others)
                    getField(x, y, z).draw3D((size.x / 2.0f - x) * fieldSize.z - fieldSize.z / 2, //Calculate the centered x position
                            (size.y / 2.0f - y) * fieldSize.z - fieldSize.z / 2, //Calculate the centered y position
                            (size.z / 2.0f - z) * fieldSize.z - fieldSize.z / 2, //Calculate the centered z position
                            (int) (fieldSize.z * 0.9f), //Calculate the actual draw size of the field
                            Main.layer == z ? pa.color(50, 150, 50, 150) : pa.color(200, 200, 200, 50)); //Set the highlight color, if it's the selected layer
                }
            }
        }
        pa.popMatrix();
    }

    //---------- WIN CHECKS ----------

    /**
     * Check if any player has won the game
     *
     * @return the play that won the game or 0 if no one won
     */
    public int checkWin() {
        //Loops through all fields and check if the player occupying that field has won
        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                for (int z = 0; z < size.z; z++) {
                    if (checkFieldWin(new Vector3(x, y, z)))
                        return getField(x, y, z).getPlayer(); //Returns the player that has won
                }
            }
        }
        return 0; //Returns 0 if winner was found
    }

    /**
     * Checks a single field if its occupant has [winLength] marks in a row in each direction
     *
     * @param f the field to check
     * @return true if the occupant won; false if he didn't win or there is no occupant
     */
    private boolean checkFieldWin(Vector3 f) {
        int winPlayer = getField(f).getPlayer();
        if (winPlayer == 0) { //Return false if there is no occupant (/the field is empty)
            return false;
        }
        //Checks rows in each direction originating in the field and returns true if any return[s] true
        return checkRow(f, new Vector3(1, 0, 0), winPlayer)
                || checkRow(f, new Vector3(0, 1, 0), winPlayer)
                || checkRow(f, new Vector3(0, 0, 1), winPlayer)

                || checkRow(f, new Vector3(1, 1, 0), winPlayer)
                || checkRow(f, new Vector3(1, -1, 0), winPlayer)

                || checkRow(f, new Vector3(1, 0, 1), winPlayer)
                || checkRow(f, new Vector3(1, 0, -1), winPlayer)

                || checkRow(f, new Vector3(0, 1, 1), winPlayer)
                || checkRow(f, new Vector3(0, 1, -1), winPlayer)

                || checkRow(f, new Vector3(1, 1, 1), winPlayer)
                || checkRow(f, new Vector3(1, -1, 1), winPlayer)
                || checkRow(f, new Vector3(1, 1, -1), winPlayer)
                || checkRow(f, new Vector3(1, -1, -1), winPlayer);
    }

    /**
     * Checks [winLength] fields in a row, originating from f, if they all have the same player
     *
     * @param f         the field to originate from
     * @param inc       the directional vector to move in
     * @param winPlayer the player to check all fields with
     * @return true if all fields are assigned to the same player; false if any field isn't assigned to winPlayer
     */
    private boolean checkRow(Vector3 f, Vector3 inc, int winPlayer) {
        for (int i = 0; i < winLength; i++) { //Loops winLength times
            int fX = (int) (f.x + inc.x * i); //Calculates current field x value from directional vector and origin
            int fY = (int) (f.y + inc.y * i); //Calculates current field y value from directional vector and origin
            int fZ = (int) (f.z + inc.z * i); //Calculates current field z value from directional vector and origin
            if (fX >= size.x || fY >= size.y || fZ >= size.z) //Return false if out of bounds
                return false;
            Field currF = getField(fX, fY, fZ); //Get the current field
            if (currF.getPlayer() != winPlayer) //Return false if player is not the same
                return false;
        }
        return true; //Return true if no field returned false
    }

    //---------- GETTER/SETTER ----------
    public Field getField(int x, int y, int z) {
        return fields.get(getFieldIndex(x, y, z));
    }

    private Field getField(float x, float y, float z) {
        return getField((int) x, (int) y, (int) z);
    }

    private Field getField(Vector3 v) {
        return getField(v.x, v.y, v.z);
    }

    private int getFieldIndex(int x, int y, int z) {
        return (int) (z + y * size.z + x * size.z * size.y);
    }

    public Vector3 getSize() {
        return size;
    }

    public Vector3 getFieldSize() {
        return fieldSize;
    }
}
