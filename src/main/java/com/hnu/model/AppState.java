package com.hnu.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

public class AppState {
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);

    private final ClickerState clickerState;
    private final MacroState macroState;

    private final Preferences prefs = Preferences.userNodeForPackage(AppState.class);

    public AppState(){
        clickerState = new ClickerState();
        macroState = new MacroState();
    }

    public ClickerState getClickerState() {
        return clickerState;
    }

    public MacroState getMacroState() {
        return macroState;
    }

    public void requestShutdown() {
        shutdownRequested.set(true);
        clickerState.requestShutdown();
        macroState.requestShutdown();
    }
    public boolean isShutdownRequested() {
        return shutdownRequested.get();
    }
}
