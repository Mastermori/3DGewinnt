package com.d3gewinnt.map;

import com.d3gewinnt.Main;
import de.mathlib.Vector2;
import de.mathlib.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Map {

    private static Main pa = Main.inst;

    List<Field> fields;
    Vector3 size;
    int winLength;
    int width, height;
    Vector2 fieldSize;

    public Map(int xSize, int ySize, int zSize, int winLength, int width, int height) {
        fields = new ArrayList<>();
        size = new Vector3(xSize, ySize, zSize);
        this.winLength = winLength;
        this.width = width;
        this.height = height;
        fieldSize = new Vector2(width / size.x, height / size.y);
        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                for (int z = 0; z < size.z; z++) {
                    fields.add(new Field(x, y, z));
                }
            }
        }
    }

    public void draw2D(int x, int y, int layer) {
        pa.noFill();
        pa.stroke(125);
        pa.strokeWeight(2);
        for (int i = 0; i < size.x; i++) {
            for (int j = 0; j < size.y; j++) {
                Field f = getField(i, j, layer);
                f.draw2D(x + i * fieldSize.x, y + j * fieldSize.y, fieldSize);
            }
        }
    }

    public void draw3D() {

    }

    public int checkWin() {
        int winPlayer;
        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                for (int z = 0; z < size.z; z++) {
                    if((winPlayer = checkFieldWin(new Vector3(x, y, z))) != 0)
                        return winPlayer;
                }
            }
        }
        return 0;
    }

    private int checkFieldWin(Vector3 f) {
        boolean won = false;
        int winPlayer = getField(f).getPlayer();
        won = won || checkRow(f, new Vector3(1, 0, 0), winPlayer);
        won = won || checkRow(f, new Vector3(0, 1, 0), winPlayer);
        won = won || checkRow(f, new Vector3(0, 0, 1), winPlayer);

        won = won || checkRow(f, new Vector3(1, 1, 0), winPlayer);
        won = won || checkRow(f, new Vector3(1, -1, 0), winPlayer);

        won = won || checkRow(f, new Vector3(1, 0, 1), winPlayer);
        won = won || checkRow(f, new Vector3(1, 0, -1), winPlayer);

        won = won || checkRow(f, new Vector3(0, 1, 1), winPlayer);
        won = won || checkRow(f, new Vector3(0, 1, -1), winPlayer);

        won = won || checkRow(f, new Vector3(1, 1, 1), winPlayer);
        won = won || checkRow(f, new Vector3(1, -1, 1), winPlayer);
        won = won || checkRow(f, new Vector3(1, 1, -1), winPlayer);
        won = won || checkRow(f, new Vector3(1, -1, -1), winPlayer);

        return won ? 0 : winPlayer;
    }

    private boolean checkRow(Vector3 f, Vector3 inc, int winPlayer) {
        for (int i = 0; i < winLength; i++) {
            if(getField(f.x + inc.x * i, f.y + inc.y * i, f.z + inc.z * i).getPlayer() != winPlayer)
                return false;
        }
        return true;
    }

    public Field getField(int x, int y, int z) {
        return fields.get(getFieldIndex(x, y, z));
    }

    public Field getField(float x, float y, float z) {
        return getField((int) x, (int) y, (int) z);
    }

    public Field getField(Vector3 v) {
        return getField(v.x, v.y, v.z);
    }

    private int getFieldIndex(int x, int y, int z) {
        return (int) (z + y * size.y + x * size.x * size.y);
    }

    public Vector3 getSize() {
        return size;
    }

    public Vector2 getFieldSize() {
        return fieldSize;
    }
}
