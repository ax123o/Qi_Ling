package com.hnu.service;

import com.hnu.model.ClickerState;
import com.hnu.model.KeyType;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClickerService implements Runnable {
    private final ClickerState state;
    private final Robot robot;
    private final AtomicInteger clickCount;

    public ClickerService(ClickerState state, Robot robot, AtomicInteger clickCount) {
        this.state = state;
        this.robot = robot;
        this.clickCount = clickCount;
    }

    @Override
    public void run() {
        while (!state.isShutdownRequested()) {
            if (state.isRunning()) {
                try {
                    if (state.getKeyType() == KeyType.MOUSE) {
                        int buttonMask = state.getKeyCode();
                        robot.mousePress(buttonMask);
                        robot.mouseRelease(buttonMask);
                    } else {
                        int keyCode = state.getKeyCode();
                        robot.keyPress(keyCode);
                        robot.keyRelease(keyCode);
                    }

                    // 增加点击计数
                    clickCount.incrementAndGet();

                    // 只在运行时休眠
                    if (state.isRunning()) {
                        Thread.sleep(state.getInterval());
                    }
                } catch (InterruptedException e) {
                    System.err.println("连点线程被中断");
                    break;
                } catch (Exception e) {
                    System.err.println("连点服务发生错误: " + e.getMessage());
                    break;
                }
            }
        }
        System.out.println("连点服务已停止");
    }
}
