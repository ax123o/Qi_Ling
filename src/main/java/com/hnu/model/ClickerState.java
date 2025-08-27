package com.hnu.model;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.Preferences;

public class ClickerState {
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger interval;
    private final AtomicReference<KeyType> keyType;
    private final AtomicInteger keyCode;

    private final Preferences prefs = Preferences.userNodeForPackage(ClickerState.class);

    public ClickerState() {
        // 从持久化存储加载配置，如果没有则使用默认值
        int savedInterval = prefs.getInt("interval", 1000);
        interval = new AtomicInteger(savedInterval);

        String savedKeyType = prefs.get("keyType", "MOUSE");
        keyType = new AtomicReference<>(KeyType.valueOf(savedKeyType));

        int savedKeyCode = prefs.getInt("keyCode", InputEvent.BUTTON1_DOWN_MASK);
        keyCode = new AtomicInteger(savedKeyCode);
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void toggleRunning() {
        isRunning.set(!isRunning.get());
    }

    public int getInterval() {
        return interval.get();
    }

    public void setInterval(int interval) {
        if (interval > 0) {
            this.interval.set(interval);
            prefs.putInt("interval", interval);
        }
    }

    public KeyType getKeyType() {
        return keyType.get();
    }

    public void setKeyType(KeyType keyType) {
        this.keyType.set(keyType);
        prefs.put("keyType", keyType.name());
    }

    public int getKeyCode() {
        return keyCode.get();
    }

    public void setKeyCode(int keyCode) {
        this.keyCode.set(keyCode);
        prefs.putInt("keyCode", keyCode);
    }


    public String getKeyName() {
        if (keyType.get() == KeyType.MOUSE) {
            switch (keyCode.get()) {
                case InputEvent.BUTTON1_DOWN_MASK:
                    return "左键";
                case InputEvent.BUTTON3_DOWN_MASK:
                    return "右键";
                case InputEvent.BUTTON2_DOWN_MASK:
                    return "中键";
                default:
                    return "键盘按键";
            }
        } else {
            return KeyEvent.getKeyText(keyCode.get());
        }
    }

    public void setMouseButton(String button) {
        int buttonMask;
        switch (button) {
            case "左键":
                buttonMask = InputEvent.BUTTON1_DOWN_MASK;
                break;
            case "右键":
                buttonMask = InputEvent.BUTTON3_DOWN_MASK;
                break;
            case "中键":
                buttonMask = InputEvent.BUTTON2_DOWN_MASK;
                break;
            default:
                buttonMask = InputEvent.BUTTON1_DOWN_MASK;
        }

        setKeyType(KeyType.MOUSE);
        setKeyCode(buttonMask);
    }

    public void setKeyboardKey(int code) {
        setKeyType(KeyType.KEYBOARD);
        setKeyCode(code);
    }

    public void requestShutdown() {
        shutdownRequested.set(true);
    }
    public boolean isShutdownRequested() {
        return shutdownRequested.get();
    }
}
