package com.d3gewinnt.input;

import com.d3gewinnt.Main;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Static handler keyboard inputs. <br>
 * Needs to be called by processing registerKeyPress() and registerKeyRelease() methods. <br>
 * Automatically runs added KeyCommands (needs update() to be run each frame).
 */
public class KeyboardInput {

    //---------- VARIABLES ----------
    //Lists to save keyCodeActions - enables checking keys through methods
    private static Set<Integer> pressed = new HashSet<>();
    private static Set<Integer> justPressed = new HashSet<>();
    private static Set<Integer> justReleased = new HashSet<>();

    //List of KeyCommands to be checked in update()
    private static List<KeyBind> commands = new LinkedList<>();

    //---------- KEY-STATES ----------
    /**
     * Checks if the provided key is pressed
     * @param keyCode to check if pressed
     * @return true - if the key is pressed
     */
    public static boolean keyDown(int keyCode) {
        return pressed.contains(keyCode);
    }

    /**
     * Checks if the provided key was just pressed (only true in exactly one frame after pressing)
     * @param keyCode to check was just pressed
     * @return true - if the key was just pressed
     */
    public static boolean keyJustDown(int keyCode) {
        return justPressed.contains(keyCode);
    }

    /**
     * Checks if the provided key was just released (only true in exactly one frame after releasing)
     * @param keyCode to check was just released
     * @return true - if the key was just released
     */
    public static boolean keyJustUp(int keyCode) {
        return justReleased.contains(keyCode);
    }

    //---------- KEY-REGISTERS ----------
    /**
     * Needs to be called by the processing keyPressed method
     * @param keyCode keyCode provided by processing
     * @param key key char provided by processing
     */
    public static void registerKeyPress(int keyCode, char key) {
        if (!pressed.contains(keyCode)) {
            justPressed.add(keyCode);
            pressed.add(keyCode);
        }
    }

    /**
     * Needs to be called by the processing keyReleased method
     * @param keyCode keyCode provided by processing
     * @param key key char provided by processing
     */
    public static void registerKeyRelease(int keyCode, char key) {
        justReleased.add(keyCode);
        pressed.remove(Integer.valueOf(keyCode)); //Can be directly removed because there is no foreach loop for pressed -> no ConcurrentModificationExceptions
    }

    //---------- UPDATE ----------
    /**
     * Needs to be called by the processing draw method. <br>
     * Used to check and call KeyCommands
     */
    public static void update() {
        updateCommands();
        //After the check, clear the justPressed and justReleased lists so they are only true for one frame
        justPressed.clear();
        justReleased.clear();
    }

    //---------- COMMANDS ----------
    private static void updateCommands() {
        commands.forEach(KeyBind::checkAction); //Check all KeyCommands and call them, if they are pressed (see KeyBind.isFulfilled())
    }
    /**
     * Add a KeyBind to be automatically called by this handler
     * @param c KeyBind to be added
     */
    public static void addBind(KeyBind c) {
        commands.add(c);
    }

    /**
     * Remove a KeyBind from the handler
     * @param c KeyBind to be removed
     */
    public static void removeBind(KeyBind c) {
        commands.remove(c);
    }


}
