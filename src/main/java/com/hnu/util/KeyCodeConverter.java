package com.hnu.util;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class KeyCodeConverter {
    private static final Map<Integer, Integer> keyMap = new HashMap<>();

    static {
        // 字母键
        keyMap.put(NativeKeyEvent.VC_A, KeyEvent.VK_A);
        keyMap.put(NativeKeyEvent.VC_B, KeyEvent.VK_B);
        keyMap.put(NativeKeyEvent.VC_C, KeyEvent.VK_C);
        keyMap.put(NativeKeyEvent.VC_D, KeyEvent.VK_D);
        keyMap.put(NativeKeyEvent.VC_E, KeyEvent.VK_E);
        keyMap.put(NativeKeyEvent.VC_F, KeyEvent.VK_F);
        keyMap.put(NativeKeyEvent.VC_G, KeyEvent.VK_G);
        keyMap.put(NativeKeyEvent.VC_H, KeyEvent.VK_H);
        keyMap.put(NativeKeyEvent.VC_I, KeyEvent.VK_I);
        keyMap.put(NativeKeyEvent.VC_J, KeyEvent.VK_J);
        keyMap.put(NativeKeyEvent.VC_K, KeyEvent.VK_K);
        keyMap.put(NativeKeyEvent.VC_L, KeyEvent.VK_L);
        keyMap.put(NativeKeyEvent.VC_M, KeyEvent.VK_M);
        keyMap.put(NativeKeyEvent.VC_N, KeyEvent.VK_N);
        keyMap.put(NativeKeyEvent.VC_O, KeyEvent.VK_O);
        keyMap.put(NativeKeyEvent.VC_P, KeyEvent.VK_P);
        keyMap.put(NativeKeyEvent.VC_Q, KeyEvent.VK_Q);
        keyMap.put(NativeKeyEvent.VC_R, KeyEvent.VK_R);
        keyMap.put(NativeKeyEvent.VC_S, KeyEvent.VK_S);
        keyMap.put(NativeKeyEvent.VC_T, KeyEvent.VK_T);
        keyMap.put(NativeKeyEvent.VC_U, KeyEvent.VK_U);
        keyMap.put(NativeKeyEvent.VC_V, KeyEvent.VK_V);
        keyMap.put(NativeKeyEvent.VC_W, KeyEvent.VK_W);
        keyMap.put(NativeKeyEvent.VC_X, KeyEvent.VK_X);
        keyMap.put(NativeKeyEvent.VC_Y, KeyEvent.VK_Y);
        keyMap.put(NativeKeyEvent.VC_Z, KeyEvent.VK_Z);

        // 数字键
        keyMap.put(NativeKeyEvent.VC_0, KeyEvent.VK_0);
        keyMap.put(NativeKeyEvent.VC_1, KeyEvent.VK_1);
        keyMap.put(NativeKeyEvent.VC_2, KeyEvent.VK_2);
        keyMap.put(NativeKeyEvent.VC_3, KeyEvent.VK_3);
        keyMap.put(NativeKeyEvent.VC_4, KeyEvent.VK_4);
        keyMap.put(NativeKeyEvent.VC_5, KeyEvent.VK_5);
        keyMap.put(NativeKeyEvent.VC_6, KeyEvent.VK_6);
        keyMap.put(NativeKeyEvent.VC_7, KeyEvent.VK_7);
        keyMap.put(NativeKeyEvent.VC_8, KeyEvent.VK_8);
        keyMap.put(NativeKeyEvent.VC_9, KeyEvent.VK_9);

//        // 功能键
//        keyMap.put(NativeKeyEvent.VC_F1, KeyEvent.VK_F1);
//        keyMap.put(NativeKeyEvent.VC_F2, KeyEvent.VK_F2);
//        keyMap.put(NativeKeyEvent.VC_F3, KeyEvent.VK_F3);
//        keyMap.put(NativeKeyEvent.VC_F4, KeyEvent.VK_F4);
//        keyMap.put(NativeKeyEvent.VC_F5, KeyEvent.VK_F5);
//        keyMap.put(NativeKeyEvent.VC_F6, KeyEvent.VK_F6);
//        keyMap.put(NativeKeyEvent.VC_F7, KeyEvent.VK_F7);
//        keyMap.put(NativeKeyEvent.VC_F8, KeyEvent.VK_F8);
//        keyMap.put(NativeKeyEvent.VC_F9, KeyEvent.VK_F9);
//        keyMap.put(NativeKeyEvent.VC_F10, KeyEvent.VK_F10);
//        keyMap.put(NativeKeyEvent.VC_F11, KeyEvent.VK_F11);
//        keyMap.put(NativeKeyEvent.VC_F12, KeyEvent.VK_F12);

//        // 特殊键
//        keyMap.put(NativeKeyEvent.VC_ENTER, KeyEvent.VK_ENTER);
//        keyMap.put(NativeKeyEvent.VC_SPACE, KeyEvent.VK_SPACE);
//        keyMap.put(NativeKeyEvent.VC_TAB, KeyEvent.VK_TAB);
//        keyMap.put(NativeKeyEvent.VC_BACKSPACE, KeyEvent.VK_BACK_SPACE);
//        keyMap.put(NativeKeyEvent.VC_ESCAPE, KeyEvent.VK_ESCAPE);
//        keyMap.put(NativeKeyEvent.VC_SHIFT, KeyEvent.VK_SHIFT);
//        keyMap.put(NativeKeyEvent.VC_CONTROL, KeyEvent.VK_CONTROL);
//        keyMap.put(NativeKeyEvent.VC_ALT, KeyEvent.VK_ALT);
//        keyMap.put(NativeKeyEvent.VC_CAPS_LOCK, KeyEvent.VK_CAPS_LOCK);
//        keyMap.put(NativeKeyEvent.VC_NUM_LOCK, KeyEvent.VK_NUM_LOCK);
//        keyMap.put(NativeKeyEvent.VC_SCROLL_LOCK, KeyEvent.VK_SCROLL_LOCK);
//        keyMap.put(NativeKeyEvent.VC_PRINTSCREEN, KeyEvent.VK_PRINTSCREEN);
//        keyMap.put(NativeKeyEvent.VC_INSERT, KeyEvent.VK_INSERT);
//        keyMap.put(NativeKeyEvent.VC_DELETE, KeyEvent.VK_DELETE);
//        keyMap.put(NativeKeyEvent.VC_HOME, KeyEvent.VK_HOME);
//        keyMap.put(NativeKeyEvent.VC_END, KeyEvent.VK_END);
//        keyMap.put(NativeKeyEvent.VC_PAGE_UP, KeyEvent.VK_PAGE_UP);
//        keyMap.put(NativeKeyEvent.VC_PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);

        // 方向键
        keyMap.put(NativeKeyEvent.VC_UP, KeyEvent.VK_UP);
        keyMap.put(NativeKeyEvent.VC_DOWN, KeyEvent.VK_DOWN);
        keyMap.put(NativeKeyEvent.VC_LEFT, KeyEvent.VK_LEFT);
        keyMap.put(NativeKeyEvent.VC_RIGHT, KeyEvent.VK_RIGHT);

        // 符号键
        keyMap.put(NativeKeyEvent.VC_COMMA, KeyEvent.VK_COMMA);
        keyMap.put(NativeKeyEvent.VC_PERIOD, KeyEvent.VK_PERIOD);
        keyMap.put(NativeKeyEvent.VC_SLASH, KeyEvent.VK_SLASH);
        keyMap.put(NativeKeyEvent.VC_SEMICOLON, KeyEvent.VK_SEMICOLON);
        keyMap.put(NativeKeyEvent.VC_EQUALS, KeyEvent.VK_EQUALS);
        keyMap.put(NativeKeyEvent.VC_OPEN_BRACKET, KeyEvent.VK_OPEN_BRACKET);
        keyMap.put(NativeKeyEvent.VC_BACK_SLASH, KeyEvent.VK_BACK_SLASH);
        keyMap.put(NativeKeyEvent.VC_CLOSE_BRACKET, KeyEvent.VK_CLOSE_BRACKET);
        keyMap.put(NativeKeyEvent.VC_BACKQUOTE, KeyEvent.VK_BACK_QUOTE);
        keyMap.put(NativeKeyEvent.VC_QUOTE, KeyEvent.VK_QUOTE);
        keyMap.put(NativeKeyEvent.VC_MINUS, KeyEvent.VK_MINUS);

//        // 小键盘
//        keyMap.put(NativeKeyEvent.VC_NUMPAD0, KeyEvent.VK_NUMPAD0);
//        keyMap.put(NativeKeyEvent.VC_NUMPAD1, KeyEvent.VK_NUMPAD1);
//        keyMap.put(NativeKeyEvent.VC_NUMPAD2, KeyEvent.VK_NUMPAD2);
//        keyMap.put(NativeKeyEvent.VC_NUMPAD3, KeyEvent.VK_NUMPAD3);
//        keyMap.put(NativeKeyEvent.VC_NUMPAD4, KeyEvent.VK_NUMPAD4);
//        keyMap.put(NativeKeyEvent.VC_NUMPAD5, KeyEvent.VK_NUMPAD5);
//        keyMap.put(NativeKeyEvent.VC_NUMPAD6, KeyEvent.VK_NUMPAD6);
//        keyMap.put(NativeKeyEvent.VC_NUMPAD7, KeyEvent.VK_NUMPAD7);
//        keyMap.put(NativeKeyEvent.VC_NUMPAD8, KeyEvent.VK_NUMPAD8);
//        keyMap.put(NativeKeyEvent.VC_NUMPAD9, KeyEvent.VK_NUMPAD9);
//        keyMap.put(NativeKeyEvent.VC_MULTIPLY, KeyEvent.VK_MULTIPLY);
//        keyMap.put(NativeKeyEvent.VC_ADD, KeyEvent.VK_ADD);
//        keyMap.put(NativeKeyEvent.VC_SEPARATOR, KeyEvent.VK_SEPARATOR);
//        keyMap.put(NativeKeyEvent.VC_SUBTRACT, KeyEvent.VK_SUBTRACT);
//        keyMap.put(NativeKeyEvent.VC_DECIMAL, KeyEvent.VK_DECIMAL);
//        keyMap.put(NativeKeyEvent.VC_DIVIDE, KeyEvent.VK_DIVIDE);
    }

    public static Integer nativeToJavaKeyCode(int nativeKeyCode) {
        return keyMap.get(nativeKeyCode);
    }

    public static boolean isKeySupported(int nativeKeyCode) {
        return keyMap.containsKey(nativeKeyCode);
    }
}