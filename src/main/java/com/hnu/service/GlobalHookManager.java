package com.hnu.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 全局钩子管理器，统一管理 JNativeHook 的注册和注销
 */
public class GlobalHookManager {
    private static final Logger logger = Logger.getLogger(GlobalHookManager.class.getName());

    /**
     * 注册全局钩子
     */
    public static synchronized void registerNativeHook() {

        try {
            GlobalScreen.registerNativeHook();
            logger.info("全局钩子注册成功");
        } catch (NativeHookException e) {
            logger.log(Level.SEVERE, "无法注册全局钩子: " + e.getMessage(), e);
            throw new RuntimeException("无法注册全局钩子", e);
        }
    }

    /**
     * 注销全局钩子
     */
    public static synchronized void unregisterNativeHook() {

        try {
            GlobalScreen.unregisterNativeHook();
            logger.info("全局钩子注销成功");
        } catch (NativeHookException e) {
            logger.log(Level.SEVERE, "注销全局钩子失败: " + e.getMessage(), e);
        }
    }
}