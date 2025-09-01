package com.hnu.service;

import com.hnu.model.Macro;
import com.hnu.model.MacroState;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 宏管理器，负责宏的保存和加载
 */
public class MacroManager {
    private static final String MACRO_DIR = "macros";
    private static final String MACRO_EXT = ".macro";
    private final MacroState macroState;

    /**
     * 创建一个新的宏管理器
     */
    public MacroManager(MacroState macroState) {
        this.macroState = macroState;
        // 确保宏目录存在
        ensureMacroDirectoryExists();
        // 加载所有保存的宏
        loadAllMacros();
    }
    
    /**
     * 确保宏目录存在
     */
    private void ensureMacroDirectoryExists() {
        try {
            Path dir = Paths.get(MACRO_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            System.err.println("无法创建宏目录: " + e.getMessage());
        }
    }
    
    /**
     * 加载所有保存的宏
     */
    private void loadAllMacros() {
        try {
            Path dir = Paths.get(MACRO_DIR);
            if (Files.exists(dir)) {
                Files.list(dir)
                    .filter(path -> path.toString().endsWith(MACRO_EXT))
                    .forEach(this::loadMacro);
            }
        } catch (IOException e) {
            System.err.println("加载宏文件时出错: " + e.getMessage());
        }
    }
    
    /**
     * 从文件加载一个宏
     * 
     * @param path 宏文件路径
     */
    private void loadMacro(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            Macro macro = (Macro) ois.readObject();
            macroState.addMacro(macro);
            System.out.println("已加载宏: " + macro.getName());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载宏文件失败: " + path + ", " + e.getMessage());
        }
    }
    
    /**
     * 保存宏到文件
     * 
     * @param macro 要保存的宏
     * @return 是否保存成功
     */
    public boolean saveMacro(Macro macro) {
        if (macro == null || macro.getEvents().isEmpty()) {
            return false;
        }
        
        String fileName = sanitizeFileName(macro.getName()) + MACRO_EXT;
        Path filePath = Paths.get(MACRO_DIR, fileName);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(macro);
            
            // 如果这是一个新的宏，添加到列表中
            if (!macroState.containsMacro(macro)) {
                macroState.addMacro(macro);
            }

            return true;
        } catch (IOException e) {
            System.err.println("保存宏文件失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 删除宏
     * 
     * @param macro 要删除的宏
     * @return 是否删除成功
     */
    public boolean deleteMacro(Macro macro) {
        if (macro == null) {
            return false;
        }
        
        String fileName = sanitizeFileName(macro.getName()) + MACRO_EXT;
        Path filePath = Paths.get(MACRO_DIR, fileName);
        
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            macroState.removeMacro(macro);
            return true;
        } catch (IOException e) {
            System.err.println("删除宏文件失败: " + e.getMessage());
            return false;
        }
    }



    /**
     * 清理文件名，确保文件名有效
     * 
     * @param fileName 原始文件名
     * @return 清理后的文件名
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\/:*?\"<>|]", "_");
    }
}
