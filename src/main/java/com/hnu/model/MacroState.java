package com.hnu.model;

import java.awt.event.InputEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MacroState {
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);

    public MacroState() {

    }

    public void requestShutdown() {
        shutdownRequested.set(true);
    }
    public boolean isShutdownRequested() {
        return shutdownRequested.get();
    }
}
