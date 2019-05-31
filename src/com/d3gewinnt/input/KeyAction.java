package com.d3gewinnt.input;

@FunctionalInterface
public interface KeyAction {
    void execute(int[] keyCodes);
}
