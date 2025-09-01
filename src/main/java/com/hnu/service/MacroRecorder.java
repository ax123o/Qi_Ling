package com.hnu.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.hnu.model.Macro;
import com.hnu.model.MacroEvent;
import com.hnu.model.MacroState;
import com.hnu.util.KeyCodeConverter;

import java.awt.*;
import java.awt.event.InputEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 宏录制器，用于记录用户的鼠标和键盘操作
 */
public class MacroRecorder implements NativeKeyListener, NativeMouseInputListener, NativeMouseMotionListener {
    private final MacroState macroState;
    private long startTime;




    public MacroRecorder(MacroState macroState) {
        this.macroState = macroState;
    }

    /**
     * 开始录制
     */
    public void start() {
        try {
            // 添加监听器
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);
            GlobalScreen.addNativeMouseMotionListener(this);

            // 记录开始时间
            startTime = System.currentTimeMillis();

            // 生成基于当前时间的宏名称
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String macroName = dateFormat.format(new Date());


            // 获取实际物理屏幕尺寸（如 2880×1800）
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            DisplayMode displayMode = gd.getDisplayMode();
            int physicalWidth = displayMode.getWidth();
            int physicalHeight = displayMode.getHeight();

            Macro newMacro = new Macro(macroName, physicalWidth, physicalHeight);
            macroState.setCurrentMacro(newMacro);

            // 更新状态
            macroState.startRecording();

            System.out.println("宏录制开始，保存为: " + macroName);
        } catch (Exception e) {
            System.err.println("无法注册全局钩子: " + e.getMessage());
        }
    }

    /**
     * 停止录制
     */
    public void stop() {
        // 移除监听器
        GlobalScreen.removeNativeKeyListener(this);
        GlobalScreen.removeNativeMouseListener(this);
        GlobalScreen.removeNativeMouseMotionListener(this);

        // 更新状态
        macroState.stopRecording();

        System.out.println("宏录制结束，共记录了 " + macroState.getCurrentMacro().getEventCount() + " 个事件");
    }

    /**
     * 切换录制状态
     */
    public void toggleRecording() {
        if (macroState.isRecording()) {
            stop();
        } else {
            start();
        }
    }

    /**
     * 获取当前录制的宏
     */
    public Macro getCurrentMacro() {
        return macroState.getCurrentMacro();
    }

    // 键盘事件监听
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // 忽略F8和F9和F10键
        if (e.getKeyCode() == NativeKeyEvent.VC_F8 || e.getKeyCode() == NativeKeyEvent.VC_F9 || e.getKeyCode() == NativeKeyEvent.VC_F10) {
            return;
        }

        // 转换键码
        Integer javaKeyCode = KeyCodeConverter.nativeToJavaKeyCode(e.getKeyCode());
        if (javaKeyCode != null) {
            long timestamp = System.currentTimeMillis() - startTime;
            MacroEvent event = new MacroEvent(MacroEvent.EventType.KEY_PRESS, javaKeyCode, timestamp);
            macroState.getCurrentMacro().addEvent(event);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // 忽略F8和F9和F10键
        if (e.getKeyCode() == NativeKeyEvent.VC_F8 || e.getKeyCode() == NativeKeyEvent.VC_F9 || e.getKeyCode() == NativeKeyEvent.VC_F10) {
            return;
        }

        // 转换键码
        Integer javaKeyCode = KeyCodeConverter.nativeToJavaKeyCode(e.getKeyCode());
        if (javaKeyCode != null) {
            long timestamp = System.currentTimeMillis() - startTime;
            MacroEvent event = new MacroEvent(MacroEvent.EventType.KEY_RELEASE, javaKeyCode, timestamp);
            macroState.getCurrentMacro().addEvent(event);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // 不需要处理键入事件
    }

    // 鼠标事件监听
    @Override
    public void nativeMousePressed(NativeMouseEvent e) {

        int button = convertMouseButton(e.getButton());
        Point position = new Point(e.getX(), e.getY());
        long timestamp = System.currentTimeMillis() - startTime;

        MacroEvent event = new MacroEvent(MacroEvent.EventType.MOUSE_PRESS, button, position, timestamp);
        macroState.getCurrentMacro().addEvent(event);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {

        int button = convertMouseButton(e.getButton());
        Point position = new Point(e.getX(), e.getY());
        long timestamp = System.currentTimeMillis() - startTime;

        MacroEvent event = new MacroEvent(MacroEvent.EventType.MOUSE_RELEASE, button, position, timestamp);
        macroState.getCurrentMacro().addEvent(event);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {

        // 为了减少事件数量，可以考虑只在鼠标移动一定距离后才记录
        Point position = new Point(e.getX(), e.getY());
        long timestamp = System.currentTimeMillis() - startTime;

        MacroEvent event = new MacroEvent(MacroEvent.EventType.MOUSE_MOVE, 0, position, timestamp);
        macroState.getCurrentMacro().addEvent(event);
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        // 鼠标拖动也是一种移动，调用移动处理方法
        nativeMouseMoved(e);
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        // 不需要处理点击事件，因为已经有了按下和释放事件
    }

    /**
     * 将JNativeHook鼠标按钮转换为Java AWT鼠标按钮
     */
    private int convertMouseButton(int nativeButton) {
        switch (nativeButton) {
            case 1: // 左键
                return InputEvent.BUTTON1_DOWN_MASK;
            case 2: // 右键
                return InputEvent.BUTTON3_DOWN_MASK;
            case 3: // 中键
                return InputEvent.BUTTON2_DOWN_MASK;
            default:
                return InputEvent.BUTTON1_DOWN_MASK; // 默认为左键
        }
    }
}