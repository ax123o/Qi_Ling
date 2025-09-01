package com.hnu.service;

import com.hnu.model.Macro;
import com.hnu.model.MacroEvent;
import com.hnu.model.MacroState;
import com.hnu.util.RobotFactory;

import java.awt.*;
/**
 * 宏播放器，负责播放记录的宏
 */
public class MacroPlayer implements Runnable{
    private final Robot robot;
    private final MacroState macroState;
    private long startTime;
    private double scaleX;
    private double scaleY;
    private Thread playerThread;


    /**
     * 创建一个新的宏播放器
     * 
     * @param macroState 宏状态
     */
    public MacroPlayer(MacroState macroState) {
        this.robot = RobotFactory.createRobot();
        this.macroState = macroState;

    }
    
    /**
     * 开始播放宏
     */
    public void start() {
        if (macroState.getSelectedMacro() != null) {

            macroState.startPlaying();
            playerThread = new Thread(this);
            playerThread.start();
        }
    }
    
    /**
     * 停止播放宏
     */
    public void stop() {
        macroState.stopPlaying();
        if (playerThread != null) {
            playerThread.interrupt();
        }
    }

    public void togglePlaying(){
        if (macroState.isPlaying()) {
            stop();
        } else {
            start();
        }
    }

    @Override
public void run() {
        try {
            Macro macro = macroState.getSelectedMacro();

            // 获取 JVM 感知的逻辑屏幕尺寸（如 1440×900）
            Dimension logicalSize = Toolkit.getDefaultToolkit().getScreenSize();

            // 计算缩放比例（逻辑尺寸 / 物理尺寸）
            scaleX = (double) logicalSize.width / macro.getScreenWidth();
            scaleY = (double) logicalSize.height / macro.getScreenHeight();

            System.out.println("实际分辨率：" + "X：" +  macro.getScreenWidth() + "Y：" + macro.getScreenHeight());
            System.out.println("缩放比例：" + "scaleX：" +  scaleX + "scaleY：" + scaleY);

            if (macro == null || macro.getEvents().isEmpty()) {
                return;
            }

            int loopCount = macroState.getLoopCount();
            int currentLoop = 0;

            // 如果循环次数为0，则无限循环
            while (macroState.isPlaying() && (loopCount == 0 || currentLoop < loopCount)) {
                startTime = System.currentTimeMillis();

                // 播放宏中的所有事件
                for (MacroEvent event : macro.getEvents()) {
                    if (!macroState.isPlaying()) {
                        break;
                    }

                    // 计算需要等待的时间
                    long waitTime = event.getTimestamp() - (System.currentTimeMillis() - startTime);
                    if (waitTime > 0) {
                        Thread.sleep(waitTime);
                    }

                    //计算缩放后的坐标
                    Point scaledPoint = scalePoint(event.getPosition());

                    // 根据事件类型执行相应的操作
                    switch (event.getType()) {
                        case MOUSE_MOVE:
                            robot.mouseMove(scaledPoint.x, scaledPoint.y);
                            break;
                        case MOUSE_PRESS:
                            robot.mouseMove(scaledPoint.x, scaledPoint.y);
                            robot.mousePress(event.getCode());
                            break;
                        case MOUSE_RELEASE:
                            robot.mouseMove(scaledPoint.x, scaledPoint.y);
                            robot.mouseRelease(event.getCode());
                            break;
                        case KEY_PRESS:
                            robot.keyPress(event.getCode());
                            break;
                        case KEY_RELEASE:
                            robot.keyRelease(event.getCode());
                            break;
                    }
                }

                currentLoop++;

                // 如果不是无限循环，并且已经完成了所有循环，则退出
                if (loopCount > 0 && currentLoop >= loopCount) {
                    break;
                }

                // 循环之间稍微暂停一下
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            // 线程被中断，停止播放
            System.out.println("宏播放被中断");
        } catch (Exception e) {
            System.err.println("宏播放出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            macroState.stopPlaying();
        }
    }

    /**
     * 根据屏幕缩放比例调整坐标点
     *
     * @param point 原始坐标点
     * @return 调整后的坐标点
     */
    private Point scalePoint(Point point) {
        int scaledX = (int) (point.x * scaleX);
        int scaledY = (int) (point.y * scaleY);
        return new Point(scaledX, scaledY);
    }
}
