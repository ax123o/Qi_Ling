package com.hnu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 表示一个完整的宏，包含多个宏事件
 */
public class Macro implements Serializable {
    private static final long serialVersionUID = 1L;

    // 宏名称
    private String name;
    // 宏描述
    private String description;
    // 宏创建时间
    private long creationTime;
    // 宏事件列表
    private List<MacroEvent> events;
    // 宏总时长（毫秒）
    private long duration;
    // 录制时的屏幕宽度
    private int screenWidth;
    // 录制时的屏幕高度
    private int screenHeight;

    /**
     * 创建一个新的宏
     *
     * @param name 宏名称
     */
    public Macro(String name) {
        this.name = name;
        this.description = "";
        this.creationTime = System.currentTimeMillis();
        this.events = new ArrayList<>();
        this.duration = 0;
    }

    public Macro(String name, int screenWidth, int screenHeight) {
        this.name = name;
        this.description = "";
        this.creationTime = System.currentTimeMillis();
        this.events = new ArrayList<>();
        this.duration = 0;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    /**
     * 添加一个事件到宏中
     *
     * @param event 要添加的事件
     */
    public void addEvent(MacroEvent event) {
        events.add(event);
        // 更新宏的总时长
        if (event.getTimestamp() > duration) {
            duration = event.getTimestamp();
        }
    }

    /**
     * 清空所有事件
     */
    public void clearEvents() {
        events.clear();
        duration = 0;
    }

    /**
     * 获取宏中的所有事件
     *
     * @return 事件列表
     */
    public List<MacroEvent> getEvents() {
        return events;
    }

    /**
     * 获取宏名称
     *
     * @return 宏名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置宏名称
     *
     * @param name 新的宏名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取宏描述
     *
     * @return 宏描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置宏描述
     *
     * @param description 新的宏描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取宏创建时间
     *
     * @return 创建时间（毫秒时间戳）
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * 获取宏总时长
     *
     * @return 总时长（毫秒）
     */
    public long getDuration() {
        return duration;
    }

    /**
     * 获取宏中事件的数量
     *
     * @return 事件数量
     */
    public int getEventCount() {
        return events.size();
    }

    /**
     * 获取录制时的屏幕宽度
     *
     * @return 屏幕宽度
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * 设置录制时的屏幕宽度
     *
     * @param screenWidth 屏幕宽度
     */
    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * 获取录制时的屏幕高度
     *
     * @return 屏幕高度
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * 设置录制时的屏幕高度
     *
     * @param screenHeight 屏幕高度
     */
    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    @Override
    public String toString() {
        return String.format("%s (%d个事件, %dms)", name, events.size(), duration);
    }
}