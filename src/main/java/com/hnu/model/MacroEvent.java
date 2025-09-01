package com.hnu.model;

import java.awt.Point;
import java.io.Serializable;

/**
 * 表示宏中的一个事件，可以是鼠标事件或键盘事件
 */
public class MacroEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // 事件类型
    private EventType type;
    // 事件代码（鼠标按钮或键盘键码）
    private int code;
    // 事件发生的时间戳（相对于宏开始的时间）
    private long timestamp;
    // 鼠标位置（仅对鼠标事件有效）
    private Point position;
    
    /**
     * 事件类型枚举
     */
    public enum EventType {
        MOUSE_PRESS,
        MOUSE_RELEASE,
        MOUSE_MOVE,
        KEY_PRESS,
        KEY_RELEASE
    }
    
    /**
     * 创建一个键盘事件
     * 
     * @param type 事件类型（KEY_PRESS或KEY_RELEASE）
     * @param keyCode 键码
     * @param timestamp 时间戳
     */
    public MacroEvent(EventType type, int keyCode, long timestamp) {
        if (type != EventType.KEY_PRESS && type != EventType.KEY_RELEASE) {
            throw new IllegalArgumentException("键盘事件类型必须是KEY_PRESS或KEY_RELEASE");
        }
        this.type = type;
        this.code = keyCode;
        this.timestamp = timestamp;
        this.position = null;
    }
    
    /**
     * 创建一个鼠标事件
     * 
     * @param type 事件类型（MOUSE_PRESS、MOUSE_RELEASE或MOUSE_MOVE）
     * @param buttonCode 鼠标按钮代码（对于MOUSE_MOVE可以为0）
     * @param position 鼠标位置
     * @param timestamp 时间戳
     */
    public MacroEvent(EventType type, int buttonCode, Point position, long timestamp) {
        if (type != EventType.MOUSE_PRESS && type != EventType.MOUSE_RELEASE && type != EventType.MOUSE_MOVE) {
            throw new IllegalArgumentException("鼠标事件类型必须是MOUSE_PRESS、MOUSE_RELEASE或MOUSE_MOVE");
        }
        this.type = type;
        this.code = buttonCode;
        this.position = position;
        this.timestamp = timestamp;
    }
    
    public EventType getType() {
        return type;
    }
    
    public int getCode() {
        return code;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public Point getPosition() {
        return position;
    }
    
    @Override
    public String toString() {
        if (type == EventType.KEY_PRESS || type == EventType.KEY_RELEASE) {
            return String.format("%s: keyCode=%d, time=%d", type, code, timestamp);
        } else {
            return String.format("%s: button=%d, position=(%d,%d), time=%d", 
                    type, code, position.x, position.y, timestamp);
        }
    }
}