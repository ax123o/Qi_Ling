package com.hnu.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.hnu.model.ClickerState;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class KeyboardService implements NativeKeyListener {
    private final ClickerState clickerState;
    private final MacroRecorder macroRecorder;
    private final MacroPlayer macroPlayer;
    private final AtomicReference<Consumer<NativeKeyEvent>> keyConsumer = new AtomicReference<>(null);

    public KeyboardService(ClickerState clickerState, MacroRecorder macroRecorder, MacroPlayer macroPlayer) {
        this.clickerState = clickerState;
        this.macroRecorder = macroRecorder;
        this.macroPlayer = macroPlayer;
    }

    public void start() {
        try {
            GlobalScreen.addNativeKeyListener(this);
        } catch (Exception ex) {
            System.err.println("全局键盘监听注册失败: " + ex.getMessage());
            System.exit(1);
        }
    }

    public void stop() {
        try {
            GlobalScreen.removeNativeKeyListener(this);
        } catch (Exception ex) {
            System.err.println("注销全局键盘监听失败: " + ex.getMessage());
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // 获取当前的 Consumer 对象
        Consumer<NativeKeyEvent> consumer = keyConsumer.get();
        if (consumer != null) {
            consumer.accept(e); // 调用 Consumer 的 accept 方法
            keyConsumer.set(null); // 重置为 null
            return; // 直接返回，不处理其他按键
        }
        // 处理连点热键 (F8)
        if (e.getKeyCode() == NativeKeyEvent.VC_F8) {
            clickerState.toggleRunning();
            System.out.println("\n连点器状态: " + (clickerState.isRunning() ? "已启动" : "已停止"));
        }

        // 处理宏录制热键 (F9)
        if (e.getKeyCode() == NativeKeyEvent.VC_F9) {
            macroRecorder.toggleRecording();
            return;
        }

        // 处理宏播放热键 (F10)
        if (e.getKeyCode() == NativeKeyEvent.VC_F10) {
            macroPlayer.togglePlaying();
            return;
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {

    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {

    }

    public Consumer<NativeKeyEvent> getKeyConsumer() {
        return keyConsumer.get();
    }

    public void setKeyConsumer(Consumer<NativeKeyEvent> consumer) {
        keyConsumer.set(consumer);
    }

    public void clearKeyConsumer() {
        keyConsumer.set(null);
    }
}
