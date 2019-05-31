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
        pa.noFill();
        pa.stroke(255, 0, 0);
        int fieldSize = 50;
        pa.pushMatrix();
        pa.translate(width/4,height/2, (pa.max(size.x, size.y)-3) * -200);
        float rotX = (Main.rotX / 100.0f)*-2*pa.PI+pa.PI;
        float rotY = (Main.rotY / 100.0f)*2*pa.PI-pa.PI;

        /*
        float rotX = (pa.mouseY/(pa.height*1.0f))*-2*pa.PI+pa.PI;
        float rotY = (pa.min(pa.mouseX, pa.width/2)/(pa.width/2.0f))*2*pa.PI-pa.PI;
         */

        pa.rotateX(rotX);
        pa.rotateY(rotY);
            for (int y = 0; y < size.y; y++) {
                for (int x = 0; x < size.x; x++) {
                for (int z = 0; z < size.z; z++) {
                    getField(x, y, z).draw3D((int) ((Math.floor(size.x/2.0) - x) * fieldSize * 2),
                            (int) ((Math.floor(size.y/2.0) - y) * fieldSize * 2),
                            (int) ((Math.floor(size.z/2.0) - z) * fieldSize * 2), (int) (fieldSize*1.8f),
                            pa.layer == z ? pa.color(50, 150, 50, 100) :  pa.color(200, 200, 200, 70));
                }
            }
        }
        pa.popMatrix();
    }

    public int checkWin() {
        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                for (int z = 0; z < size.z; z++) {
                    if(checkFieldWin(new Vector3(x, y, z)))
                        return getField(x, y, z).getPlayer();
                }
            }
        }
        return 0;
    }

    private boolean checkFieldWin(Vector3 f) {
        int winPlayer = getField(f).getPlayer();
        if (winPlayer == 0) {
            return false;
        }
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

    private boolean checkRow(Vector3 f, Vector3 inc, int winPlayer) {
        for (int i = 0; i < winLength; i++) {
            int fX = (int) (f.x + inc.x * i);
            int fY = (int) (f.y + inc.y * i);
            int fZ = (int) (f.z + inc.z * i);
            if(fX >= size.x || fY >= size.y || fZ >= size.z)
                return false;
            Field currF = getField(fX, fY, fZ);
            if(currF.getPlayer() != winPlayer)
                return false;
            System.out.println(i + " " + currF.pos + " " + currF.getPlayer());
        }
        System.out.println("Winplayer: " + winPlayer + " dir: " + inc);
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
        return (int) (z + y * size.z + x * size.z * size.y);
    }

    public Vector3 getSize() {
        return size;
    }

    public Vector2 getFieldSize() {
        return fieldSize;
    }
}
