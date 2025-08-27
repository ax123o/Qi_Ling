package com.hnu.util;

import java.awt.AWTException;
import java.awt.Robot;

public class RobotFactory {
    public static Robot createRobot() {
        try {
            return new Robot();
        } catch (AWTException e) {
            System.err.println("无法创建Robot实例: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }
}
