package com.d3gewinnt.input;

import de.mathlib.Utils;

import java.util.ArrayList;
import java.util.List;

public class KeyCodeAction {

    //Constants used for bitwise calculations
    public static final int DOWN = 1, JUSTDOWN = 2, JUSTUP = 4;

    int[] keyActions;
    int[] keyCodes;

    public KeyCodeAction(int keyAction, int keyCode) {
        this(new int[]{keyAction}, new int[]{keyCode});
    }

    public KeyCodeAction(int[] keyActions, int[] keyCodes) {
        this.keyActions = keyActions;
        this.keyCodes = keyCodes;
    }

    //---------- CHECK ACTIONS ----------
    /**
     * Check every key-action-pair for fulfillment - if all are fulfilled, execute the commands action. <br>
     * If any one of the keys is not fulfilled (according to their action pressed, just pressed or just released), the action isn't executed.
     */
    public boolean isFulfilled() {

        boolean[] fulfilled = new boolean[keyActions.length];
        isFulfilled(fulfilled, DOWN, KeyboardInput::keyDown); //Check the DOWN action
        isFulfilled(fulfilled, JUSTDOWN, KeyboardInput::keyJustDown); //Check the JUSTDOWN action
        isFulfilled(fulfilled, JUSTUP, KeyboardInput::keyJustUp); //Check the JUSTUP action
        return Utils.allTrue(fulfilled);
    }

    /**
     * Check all keys for provided action, if fulfilled, set the key to fulfilled.
     * @param keyAction action to check
     * @param ki KeyboardInput method to check the action with
     */
    private void isFulfilled(boolean[] fulfilled, int keyAction, KeyInterface ki) {
        for (int i = 0; i < keyActions.length; i++) {
            if (Utils.binaryCheck(keyActions[i], keyAction) && !fulfilled[i]) {
                if (ki.keyFulfilled(keyCodes[i])) {
                    fulfilled[i] = true;
                }
            }
        }
    }

    protected List<Integer> getKeyCodes() {
        List<Integer> keyCodes = new ArrayList<>(this.keyCodes.length);
        for (int keyCode : this.keyCodes) {
            keyCodes.add(keyCode);
        }
        return keyCodes;
    }

}
