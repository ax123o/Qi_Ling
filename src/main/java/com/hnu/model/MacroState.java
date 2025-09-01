package com.hnu.model;

import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MacroState {
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);
    private final AtomicBoolean isRecording = new AtomicBoolean(false);
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private final AtomicReference<Macro> currentMacro = new AtomicReference<>(null);
    private final AtomicReference<Macro> selectedMacro = new AtomicReference<>(null);
    private final AtomicReference<List<Macro>> macros = new AtomicReference<>(new ArrayList<>());
    private final AtomicInteger loopCount = new AtomicInteger(1);

    public MacroState() {
        // 初始化时创建一个空的宏
        currentMacro.set(new Macro("新建宏"));
    }

    /**
     * 请求关闭
     */
    public void requestShutdown() {
        shutdownRequested.set(true);
    }

    /**
     * 检查是否请求关闭
     */
    public boolean isShutdownRequested() {
        return shutdownRequested.get();
    }

    /**
     * 开始录制宏
     */
    public void startRecording() {
        if (!isRecording.get() && !isPlaying.get()) {
            // 创建新的宏开始录制
            isRecording.set(true);
        }
    }

    /**
     * 停止录制宏
     */
    public void stopRecording() {
        isRecording.set(false);
    }

    /**
     * 开始播放宏
     */
    public void startPlaying() {
        if (!isRecording.get() && !isPlaying.get() && selectedMacro.get() != null) {
            isPlaying.set(true);
        }
    }

    /**
     * 停止播放宏
     */
    public void stopPlaying() {
        isPlaying.set(false);
    }

    /**
     * 检查是否正在录制
     */
    public boolean isRecording() {
        return isRecording.get();
    }

    /**
     * 检查是否正在播放
     */
    public boolean isPlaying() {
        return isPlaying.get();
    }

    /**
     * 获取当前正在录制的宏
     */
    public Macro getCurrentMacro() {
        return currentMacro.get();
    }

    /**
     * 设置当前宏
     */
    public void setCurrentMacro(Macro macro) {
        currentMacro.set(macro);
    }

    /**
     * 获取当前选中的宏
     */
    public Macro getSelectedMacro() {
        return selectedMacro.get();
    }

    /**
     * 设置当前选中的宏
     */
    public void setSelectedMacro(Macro macro) {
        selectedMacro.set(macro);
    }

    public List<Macro> getMacros() { return macros.get(); }

    public void setMacros(List<Macro> macros) { this.macros.set(macros); }

    public void addMacro(Macro macro) { this.macros.get().add(macro); }

    public boolean removeMacro(Macro macro) { return macros.get().remove(macro); }

    public boolean containsMacro(Macro macro) { return macros.get().contains(macro); }

    public int getLoopCount() { return loopCount.get(); }

    public void setLoopCount(int loopCount) { this.loopCount.set(loopCount); }
}
