package com.hnu.test;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class ScreenTest {
    public static void main(String[] args) {

        // 获取当前屏幕的缩放变换
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        AffineTransform transform = gc.getDefaultTransform();


        gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = gd.getDisplayMode();
        int physicalWidth = displayMode.getWidth();
        int physicalHeight = displayMode.getHeight();


        System.out.println("实际分辨率：" + "physicalWidth：" +  physicalWidth + "physicalHeight：" + physicalHeight);

        // 计算缩放比例
        double scaleX = transform.getScaleX();
        double scaleY = transform.getScaleY();
        System.out.println("缩放比例：" + "scaleX：" +  scaleX + "scaleY：" + scaleY);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println("JVM感知的屏幕尺寸：" + screenSize.width + "×" + screenSize.height);
    }
}