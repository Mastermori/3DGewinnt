package com.d3gewinnt;

import com.d3gewinnt.input.KeyBind;
import com.d3gewinnt.input.KeyCodeAction;
import com.d3gewinnt.input.KeyboardInput;
import com.d3gewinnt.map.Field;
import com.d3gewinnt.map.Map;
import de.net.client.Client;
import de.net.interfaces.Receiver;
import de.net.server.EchoServer;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main extends PApplet implements Receiver {

    //---------- VARIABLES ----------
    public static Main inst;

    public static int[] playerColor;
    private static int player;
    private Map map;
    public static int layer;
    public static boolean hideLayers;

    private static int onlinePlayer;

    private static boolean started;

    private static Client client;
    private static EchoServer host;

    private int currWidth, currHeight;

    public static float rotX, rotY;
    private static float rotInc;

    public static float offX, offY, offZ;

    private static int dragMouseX, dragMouseY;

    //---------- SETUP ----------

    /**
     * Setup method called by Processing framework called on program start
     */
    public void setup() {
        //Request focus for key inputs asap
        frame.requestFocusInWindow();

        //Set processing parameters
        noStroke();
        frameRate(120); //limit framerate to 120, its enough
        textAlign(CENTER);
        textSize(40);

        //Set the current dimensions of the window for resizing
        currWidth = width;
        currHeight = height;

        //Set the rotation speed for WASD rotation
        rotInc = 0.5f;

        //Instantiate the player array with 4 players (Empty, 1, 2, 3) through their colors
        //playerColor = new int[]{color(200), color(255, 0, 0), color(0, 255, 0), color(0, 0, 255)};

        //..... KEYBINDS .....
        //rotation
        KeyboardInput.addBind(new KeyBind("rot-left", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_A),
                (keys) -> rotate(0, rotInc)));
        KeyboardInput.addBind(new KeyBind("rot-right", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_D),
                (keys) -> rotate(0, -rotInc)));
        KeyboardInput.addBind(new KeyBind("rot-up", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_W),
                (keys) -> rotate(rotInc, 0)));
        KeyboardInput.addBind(new KeyBind("rot-down", new KeyCodeAction(KeyCodeAction.DOWN, KeyEvent.VK_S),
                (keys) -> rotate(-rotInc, 0)));

        //movement
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

        //camera / visualisation
        KeyboardInput.addBind(new KeyBind("reset-cube", new KeyCodeAction(KeyCodeAction.JUSTDOWN, KeyEvent.VK_SPACE),
                (keys) -> rotX = rotY = 0));
        KeyboardInput.addBind(new KeyBind("hide-layers", new KeyCodeAction(KeyCodeAction.JUSTDOWN, KeyEvent.VK_H),
                (keys) -> hideLayers = !hideLayers));
        KeyboardInput.addBind(new KeyBind("reset-game", new KeyCodeAction(KeyCodeAction.JUSTDOWN, KeyEvent.VK_R),
                (keys) -> reset()));


        //Register "pre" method to be run by processing (for resizing)
        registerMethod("pre", this);

        //Reset the board (/ set it for the first time)
        reset();
    }

    //Used to detect resizing of the window and resets the window accordingly
    public void pre() {
        if (currWidth != width || currHeight != height) {
            reset();
            currWidth = width;
            currHeight = height;
        }
    }

    /**
     * Resets the camera and map to play again
     */
    private void reset() {
        map = new Map(3, 3, 3, 3, width / 2 - 10, width / 2 - 10);
        layer = 0;
        player = 1;
        rotX = 0;
        rotY = 0;
    }

    //---------- DRAW ----------

    /**
     * Draw method called by Processing framework called every frame
     */
    public void draw() {
        //Set the background to black
        background(0);
        //If the game has started (server is full)
        if (started) {
            //Update keyPress lists
            KeyboardInput.update();
            //Draw the two sides of the screen
            drawLeftSide();
            drawRightSide();
        } else {
            //If the game isn't started, display waiting message (if its the host with a player counter)
            stroke(255);
            text("Waiting for more players to join... " + (host != null ? host.getClients().size() + "/" + host.getMaxClients() : ""), width / 2, height / 2);
        }
    }

    /**
     * Draws the left side of the screen (the 3D visualisation)
     */
    private void drawLeftSide() {
        map.draw3D();
    }

    /**
     * Draws the right side of the screen (the 2D visualisation)
     */
    private void drawRightSide() {
        map.draw2D(width / 2, height / 2 - (width / 4), layer);
    }

    //---------- INPUT ----------

    /**
     * KeyPress method called by the Processing framework whenever a key is pressed
     */
    public void keyPressed() {
        //Only register key presses if the game has started
        if (started) {
            //Register the key press for the KeyBinds
            KeyboardInput.registerKeyPress(keyCode, key);
            //Process unbound key presses (can't be bound with KeyEvent - keyCodes don't match)
            if (key == '+' || key == 'q') {
                layer = min(layer + 1, (int) map.getSize().z - 1);
            } else if (key == '-' || key == 'e') {
                layer = max(layer - 1, 0);
            }
            //Process numpad keyEvents
            if (keyCode >= 129 && keyCode <= 137) {
                int num = keyCode - 129;
                int fX = num % 3;
                int fY = 2 - num / 3;
                makeTurn(fX, fY);
            }
        }
    }

    /**
     * KeyRelease method called by the Processing framework whenever a key is released
     */
    public void keyReleased() {
        //Only register key releases if the game has started
        if (started) {
            //Register the key release for the KeyBinds
            KeyboardInput.registerKeyRelease(keyCode, key);
        }
    }

    /**
     * MouseDrag method called by the Processing framework whenever a mouse button is held down and moved
     */
    public void mouseDragged() {
        //Only register mouse movement if the game has started
        if (started) {
            //Only rotate the camera if the mouse is on the left side of the screen
            if (mouseX < width / 2) {
                //Rotate the cube with by the amount dragged
                rotate(-(dragMouseY - mouseY) * 0.1f, 0);
                rotate(0, (dragMouseX - mouseX) * 0.1f);
            }
            //Set the last mouse position to calculate drag distance
            dragMouseX = mouseX;
            dragMouseY = mouseY;
        }
    }

    /**
     * MouseWheel method called by the Processing framework whenever the mouse wheel is rotated
     */
    public void mouseWheel(MouseEvent event) {
        //Only register mouse movement if the game has started
        if (started) {
            //Get the amount (and direction) the mouse wheel was rotated in
            float e = event.getCount();
            //If it was rotated up, advance by one layer, if it was rotated down, move one layer down
            if (e < 0) {
                layer = min(layer + 1, (int) map.getSize().z - 1);
            } else if (e > 0) {
                layer = max(layer - 1, 0);
            }
        }
    }

    /**
     * Rotate the cube around x and y axis
     * @param x amount to be rotated around the x axis
     * @param y amount to be rotated around the y axis
     */
    private void rotate(float x, float y) {
        rotX = (rotX + x + 100) % 100;
        rotY = (rotY + y + 100) % 100;
    }

    /**
     * Moves the cube along the x, y and z axis
     * @param x amount to be moved along the x axis
     * @param y amount to be moved along the y axis
     * @param z amount to be moved along the z axis
     */
    private void move(float x, float y, float z) {
        offX = min(offX + x, width / 2);
        offY = min(offY + y, height);
        offZ = offZ + z;
    }

    /**
     * Try to assign the field at the supplied coordinates to the current player, if successful, advance to the next player
     * @param x coordinate
     * @param y coordinate
     */
    private void makeTurn(int x, int y) {
        //If the player, whose turn it is, is not the player who is controlling this board (multiplayer), don't take action - do if it's hotseat (onlinePlayer == 0)
        if (player != onlinePlayer && onlinePlayer != 0)
            return;
        //Get the field from the map
        Field f = map.getField(x, y, layer);
        //If the field is already assigned, return
        if (f.getPlayer() != 0)
            return;
        //Assign the field to the player
        f.setPlayer(player);
        //If it's a multiplayer game,
        if (client != null)
            client.send(new String[]{"madeTurn", "" + x, "" + y, "" + layer, "" + player}, ";");
        player = player % (playerColor.length - 1) + 1;
        int won;
        if ((won = map.checkWin()) != 0) {
            System.out.println("Player " + won + " won!");
            reset();
        }
    }

    private void setField(int x, int y, int layer, int player) {
        Field f = map.getField(x, y, layer);
        f.setPlayer(player);
        Main.player = Main.player % (playerColor.length - 1) + 1;
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


    //---------- NETWORKING ----------
    @Override
    public void receive(String s, Client client) {
        System.out.println("Received " + s);
        String[] args = s.split(";");
        if (args[0].equals("madeTurn")) {
            setField(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        } else if (args[0].equals("playerId")) {
            onlinePlayer = Integer.parseInt(args[1]);
        } else if (args[0].equals("startGame")) {
            playerColor = new int[args.length];
            playerColor[0] = color(200);
            for(int i = 1; i < args.length; i++){
                playerColor[i] = Integer.parseInt(args[i]);
            }
            started = true;
        }
    }

    public Main() {
        inst = this;
    }

    //---------- PRE-SETUP ----------
    public void settings() {
        size(1200, 800, P3D);
        //fullScreen(P3D);
        smooth(4);
    }

    public static void main(String[] args) {
        PApplet.main("com.d3gewinnt.Main");
        //playerColor = new int[Integer.parseInt(args[0])];
        if (args.length > 0 && args[0].equals("-host")) {
            int port = Integer.parseInt(args[1]);
            host = new EchoServer(Integer.parseInt(args[1]), Integer.parseInt(args[2])) {
                @Override
                protected void onClientConnect(Client client) {
                    client.send(new String[]{"playerId", "" + clients.size()}, ";");
                    if (isFull()) {
                        List<String> colors = new LinkedList<>();
                        colors.add("startGame");
                        for(int i = 0; i < clients.size(); i++) {
                            Random r = new Random();
                            colors.add("" + inst.color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
                        }
                        clients.forEach((c) -> c.send(colors.toArray(new String[0]), ";"));
                    }
                }
            };
            try {
                client = new Client("127.0.0.1", port, inst);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (args.length > 0 && args[0].equals("-join")) {
            try {
                client = new Client(args[1], Integer.parseInt(args[2]), inst);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            onlinePlayer = 0;
            playerColor = new int[]{inst.color(200), inst.color(255, 0, 0), inst.color(0, 255, 0), inst.color(0, 0, 255)};
            started = true;
        }
    }

}
