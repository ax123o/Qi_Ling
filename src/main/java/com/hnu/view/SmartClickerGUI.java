package com.hnu.view;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.hnu.model.AppState;
import com.hnu.model.ClickerState;
import com.hnu.model.Macro;
import com.hnu.model.MacroState;
import com.hnu.service.ClickerService;
import com.hnu.service.GlobalHookManager;
import com.hnu.service.KeyboardService;
import com.hnu.service.MacroManager;
import com.hnu.service.MacroPlayer;
import com.hnu.service.MacroRecorder;
import com.hnu.util.KeyCodeConverter;
import com.hnu.util.RobotFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SmartClickerGUI extends Application {

    private Robot robot;
    private final AppState appState = new AppState();
    private final ClickerState clickerState = appState.getClickerState();
    private final MacroState macroState = appState.getMacroState();
    private AtomicInteger clickCount = new AtomicInteger(0);
    private KeyboardService keyboardService;
    private MacroRecorder macroRecorder;
    private MacroPlayer macroPlayer;
    private MacroManager macroManager;

    // 页面相关字段

    // 连点器相关
    private Label speedLabel;
    private Label selectedKeyLabel;
    private Label clickerStatusLabel;
    private Label clickerCountLabel;
    private Label clickerTimeLabel;
    private TextField speedField;
    private long startTime = 0;
    private Timeline timeline;
    
    // 宏相关
    private Label macroStatusLabel;
    private Label selectedMacroLabel;
    private Label macroEventsLabel;
    private Label macroDurationLabel;
    private Button recordButton;
    private Button playButton;
    private Button deleteButton;
    private Button refreshButton;
    private TextField loopCountField;
    private TextField macroNameField;
    private ListView<Macro> macroListView;

    // 页面映射和当前页面ID
    private Map<String, Button> buttons = new HashMap<>();
    private Map<String, VBox> pages = new HashMap<>();
    private String currentPageId = "clicker";
    private VBox root; // 添加根布局引用

    @Override
    public void start(Stage primaryStage) {
        // 初始化服务
        initializeServices();

        // 创建UI组件
        root = createRootLayout();

        // 设置场景和舞台
        Scene scene = new Scene(root, 1280, 800);

        // 加载CSS文件
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setTitle("自制器灵");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            cleanup();
            Platform.exit();
        });

        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                cleanup();
                Platform.exit();
                event.consume();
            }
        });

        // 启动状态更新线程
        startStatusUpdateThread();
    }

    private void initializeServices() {
        robot = RobotFactory.createRobot();

        //注册全局钩子
        GlobalHookManager.registerNativeHook();

        // 初始化宏管理器
        macroManager = new MacroManager(macroState);

        // 初始化宏录制器
        macroRecorder = new MacroRecorder(macroState);

        // 初始化宏播放器
        macroPlayer = new MacroPlayer(macroState);

        // 初始化热键监听服务
        keyboardService = new KeyboardService(clickerState, macroRecorder, macroPlayer);
        keyboardService.start();

        // 初始化连点服务
        Thread clickerThread = new Thread(new ClickerService(clickerState, robot, clickCount));
        clickerThread.setDaemon(true);
        clickerThread.start();
    }

    private VBox createRootLayout() {
        VBox root = new VBox(10);
        root.getStyleClass().add("app-root"); // 添加CSS类

        // 标题
        Label titleLabel = new Label("自制器灵");
        titleLabel.getStyleClass().add("app-title"); // 添加CSS类

        // 创建切换按钮
        HBox navigationBar = createNavigationButtons();

        // 创建页面并添加到映射中
        pages.put("clicker", createClickerPage());
        pages.put("macro", createMacroPage());

        // 默认显示连点器页面
        switchToPage("clicker");

        // 添加所有组件到根布局
        root.getChildren().addAll(titleLabel, navigationBar);
        // 添加所有页面到根布局
        for (VBox page : pages.values()) {
            root.getChildren().add(page);
        }

        return root;
    }

    private HBox createNavigationButtons() {
        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(5)); // 添加一些内边距

        // 创建连点器按钮
        Button clickerButton = new Button("连点器");
        clickerButton.getStyleClass().add("nav-button");
        clickerButton.setOnAction(e -> switchToPage("clicker"));

        // 创建宏按钮
        Button macroButton = new Button("宏功能");
        macroButton.getStyleClass().add("nav-button");
        macroButton.setOnAction(e -> switchToPage("macro"));

        // 存储按钮引用
        buttons.put("clicker", clickerButton);
        buttons.put("macro", macroButton);

        buttonBar.getChildren().addAll(clickerButton, macroButton);

        return buttonBar;
    }

    private void switchToPage(String pageId) {
        // 隐藏所有页面
        for (VBox page : pages.values()) {
            page.setVisible(false);
            page.setManaged(false);
        }

        // 显示选中的页面
        VBox selectedPage = pages.get(pageId);
        if (selectedPage != null) {
            selectedPage.setVisible(true);
            selectedPage.setManaged(true);
            currentPageId = pageId;
        }

        // 更新按钮样式
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        // 重置所有按钮样式
        for (Button button : buttons.values()) {
            button.getStyleClass().remove("nav-button-active");
            if (!button.getStyleClass().contains("nav-button")) {
                button.getStyleClass().add("nav-button");
            }
        }

        // 为当前页面按钮添加特殊样式
        Button activeButton = buttons.get(currentPageId);
        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    private VBox createClickerPage() {
        VBox page = new VBox(10);
        page.getStyleClass().add("page-container"); // 添加CSS类

        // 状态区域
        VBox statusBox = createClickerStatusBox();

        // 设置区域
        VBox settingsBox = createClickerSettingsBox();

        // 统计区域
        VBox statsBox = createClickerStatsBox();

        page.getChildren().addAll(statusBox, settingsBox, statsBox);
        return page;
    }

    private VBox createMacroPage() {
        VBox page = new VBox(10);
        page.getStyleClass().add("page-container"); // 添加CSS类

        // 宏状态区域
        VBox statusBox = createMacroStatusBox();

        // 宏控制区域
        VBox controlBox = createMacroControlBox();

        // 宏列表区域
        VBox listBox = createMacroListBox();

        page.getChildren().addAll(statusBox, controlBox, listBox);

        // 初始化宏列表
        refreshMacroList();
        
        return page;
    }
    
    private VBox createMacroStatusBox() {
        VBox statusBox = new VBox(5);
        statusBox.getStyleClass().add("section-container");
        
        Label titleLabel = new Label("宏状态:");
        titleLabel.getStyleClass().add("section-title");
        
        macroStatusLabel = new Label("状态: 已停止");
        macroStatusLabel.getStyleClass().add("section-text");
        
        selectedMacroLabel = new Label("当前选中: 无");
        selectedMacroLabel.getStyleClass().add("section-text");
        
        macroEventsLabel = new Label("事件数量: 0");
        macroEventsLabel.getStyleClass().add("section-text");

        macroDurationLabel = new Label("总时长: 0ms");
        macroDurationLabel.getStyleClass().add("section-text");
        
        statusBox.getChildren().addAll(titleLabel, macroStatusLabel, selectedMacroLabel, macroEventsLabel, macroDurationLabel);
        return statusBox;
    }
    
    private VBox createMacroControlBox() {
        VBox controlBox = new VBox(5);
        controlBox.getStyleClass().add("section-container");
        
        Label titleLabel = new Label("控制:");
        titleLabel.getStyleClass().add("section-title");
        
        // 录制和播放按钮
        HBox recordPlayBox = new HBox(10);
        recordPlayBox.setAlignment(Pos.CENTER_LEFT);
        
        recordButton = new Button("开始录制");
        recordButton.getStyleClass().add("action-button");
        recordButton.setOnAction(e -> macroRecorder.toggleRecording());
        
        playButton = new Button("开始播放");
        playButton.getStyleClass().add("action-button");
        playButton.setOnAction(e -> macroPlayer.togglePlaying());
        playButton.setDisable(true); // 初始时禁用播放按钮
        
        recordPlayBox.getChildren().addAll(recordButton, playButton);
        
        // 循环次数设置
        HBox loopBox = new HBox(10);
        loopBox.setAlignment(Pos.CENTER_LEFT);
        
        Label loopLabel = new Label("循环次数:");
        loopLabel.getStyleClass().add("section-text");
        
        loopCountField = new TextField("1");
        loopCountField.getStyleClass().add("section-field");
        loopCountField.setPrefWidth(60);

        loopCountField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                updateLoopCount();
                loopCountField.getParent().requestFocus();
            }
        });

        // 添加应用按钮
        Button applyButton = new Button("应用");
        applyButton.getStyleClass().add("action-button");
        applyButton.setOnAction(e -> updateLoopCount());
        
        Label loopHintLabel = new Label("(0表示无限循环)");
        loopHintLabel.getStyleClass().add("section-text");
        
        loopBox.getChildren().addAll(loopLabel, loopCountField, applyButton, loopHintLabel);
        
        controlBox.getChildren().addAll(titleLabel, recordPlayBox, loopBox);
        return controlBox;
    }
    
    private VBox createMacroListBox() {
        VBox listBox = new VBox(5);
        listBox.getStyleClass().add("section-container");
        
        Label titleLabel = new Label("已保存的宏:");
        titleLabel.getStyleClass().add("section-title");
        
        // 宏列表
        macroListView = new ListView<>();
        macroListView.getStyleClass().add("macro-list");
        macroListView.setPrefHeight(150);
        macroListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                macroState.setSelectedMacro(newVal);
                updateMacroInfo(newVal);
            }
        });
        
        // 删除按钮
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        deleteButton = new Button("删除选中的宏");
        deleteButton.getStyleClass().add("action-button");
        deleteButton.setOnAction(e -> deleteMacro());
        deleteButton.setDisable(true); // 初始时禁用删除按钮
        
        refreshButton = new Button("刷新列表");
        refreshButton.getStyleClass().add("action-button");
        refreshButton.setOnAction(e -> refreshMacroList());
        
        buttonBox.getChildren().addAll(deleteButton, refreshButton);
        
        listBox.getChildren().addAll(titleLabel, macroListView, buttonBox);
        return listBox;
    }

    private VBox createClickerStatusBox() {
        VBox statusBox = new VBox(5);
        statusBox.getStyleClass().add("section-container");

        Label titleLabel = new Label("当前状态:");
        titleLabel.getStyleClass().add("section-title");

        clickerStatusLabel = new Label("状态: 已停止");
        clickerStatusLabel.getStyleClass().add("section-text");
        speedLabel = new Label("速度: " + clickerState.getInterval() + "ms/次");
        speedLabel.getStyleClass().add("section-text");
        selectedKeyLabel = new Label("按键: " + clickerState.getKeyName());
        selectedKeyLabel.getStyleClass().add("section-text");

        statusBox.getChildren().addAll(titleLabel, clickerStatusLabel, speedLabel, selectedKeyLabel);
        return statusBox;
    }

    private VBox createClickerSettingsBox(){
        VBox settingsBox = new VBox(5);
        settingsBox.getStyleClass().add("section-container");

        Label settingsTitle = new Label("设置:");
        settingsTitle.getStyleClass().add("section-title");

        // 速度设置
        HBox speedBox = new HBox(10);
        speedBox.setAlignment(Pos.CENTER_LEFT);

        Label speedTitle = new Label("速度 (ms):");
        speedTitle.getStyleClass().add("section-text");

        speedField = new TextField(String.valueOf(clickerState.getInterval()));
        speedField.getStyleClass().add("section-field");
        speedField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                updateSpeed();
                speedField.getParent().requestFocus();
            }
        });

        Button speedButton = new Button("应用");
        speedButton.getStyleClass().add("action-button");
        speedButton.setOnAction(e -> updateSpeed());


        speedBox.getChildren().addAll(speedTitle, speedField, speedButton);

        // 按键设置
        HBox keyBox = new HBox(10);
        keyBox.setAlignment(Pos.CENTER_LEFT);

        Label keyTitle = new Label("按键:");
        keyTitle.getStyleClass().add("section-text");

        ComboBox<String> keyCombo = new ComboBox<>();
        keyCombo.getItems().addAll("左键", "右键", "中键", "自定义键盘按键");
        keyCombo.setValue(clickerState.getKeyName());
        keyCombo.getStyleClass().add("combo-box");
        keyCombo.setOnAction(e -> {
            String selection = keyCombo.getValue();
            if(selection.equals("自定义键盘按键")){
                setCustomKeyboardKey(keyCombo);
            }else{
                clickerState.setMouseButton(selection);
                selectedKeyLabel.setText("按键: " + clickerState.getKeyName());
            }


        });

        keyBox.getChildren().addAll(keyTitle, keyCombo);

        settingsBox.getChildren().addAll(settingsTitle, speedBox, keyBox);
        return settingsBox;
    }

    private VBox createClickerStatsBox() {
        VBox statsBox = new VBox(5);
        statsBox.getStyleClass().add("section-container");

        Label statsTitle = new Label("统计:");
        statsTitle.getStyleClass().add("section-title");

        clickerCountLabel = new Label("点击次数: 0");
        clickerCountLabel.getStyleClass().add("section-text");

        clickerTimeLabel = new Label("运行时间: 00:00:00");
        clickerTimeLabel.getStyleClass().add("section-text");

        // 重置按钮
        Button resetButton = new Button("重置统计");
        resetButton.getStyleClass().add("action-button");
        resetButton.setOnAction(e -> {
            clickCount.set(0);
            clickerCountLabel.setText("点击次数: 0");
            clickerTimeLabel.setText("运行时间: 00:00:00");
            startTime = System.currentTimeMillis();
        });
        HBox resetButtonContainer = new HBox();
        resetButtonContainer.setPadding(new Insets(0, 0, 0, 20)); // 左内边距20px
        resetButtonContainer.getChildren().add(resetButton);

        statsBox.getChildren().addAll(statsTitle, clickerCountLabel, clickerTimeLabel, resetButtonContainer);
        return statsBox;
    }

    private void setCustomKeyboardKey(ComboBox<String> keyCombo) {
        Stage keyStage = new Stage();
        keyStage.setTitle("设置键盘按键");

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #f5f5f5;");

        Label instruction = new Label("请按下要设置的键盘按键...");
        instruction.setStyle("-fx-font-weight: bold;");

        Label hint = new Label("(支持字母、数字、方向键和常用符号键)");
        hint.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Button cancelButton = new Button("取消");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        box.getChildren().addAll(instruction, hint, cancelButton);

        Scene scene = new Scene(box, 300, 150);
        keyStage.setScene(scene);
        keyStage.setResizable(false);

        // 设置按键监听器
        keyboardService.setKeyConsumer(event -> {
            int nativeKeyCode = event.getKeyCode();
            Integer javaKeyCode = KeyCodeConverter.nativeToJavaKeyCode(nativeKeyCode);

            if (javaKeyCode != null) {
                clickerState.setKeyboardKey(javaKeyCode);
                Platform.runLater(() -> {
                    selectedKeyLabel.setText("按键: " + KeyEvent.getKeyText(javaKeyCode));
                    keyCombo.setValue("自定义键盘按键");
                    keyStage.close();
                    showAlert("成功", "按键已设置为: " + KeyEvent.getKeyText(javaKeyCode));
                });
            } else {
                Platform.runLater(() -> {
                    showAlert("错误", "不支持该按键: " + NativeKeyEvent.getKeyText(nativeKeyCode));
                    keyStage.close();
                });
            }
        });

        cancelButton.setOnAction(e -> {
            keyboardService.clearKeyConsumer();
            keyStage.close();
        });

        keyStage.setOnCloseRequest(e -> {
            keyboardService.clearKeyConsumer();
        });

        keyStage.show();
    }

    private void startStatusUpdateThread() {
        Thread updateThread = new Thread(() -> {
            boolean clickerWasRunning = false; // 记录上一次的状态
            boolean macroRecorderWasRunning = false;
            boolean macroPlayerWasRunning = false;
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTime()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            while (!appState.isShutdownRequested()) {
                try {
                    Thread.sleep(100);
                    if (clickerState.isRunning() && !clickerWasRunning) {
                        startTime = System.currentTimeMillis();
                        timeline.play();
                        clickCount.set(0);
                        Platform.runLater(() -> {
                            clickerStatusLabel.setText("状态: 运行中");
                        });
                    }
                    else if (!clickerState.isRunning() && clickerWasRunning) {
                        if (timeline != null) {
                            timeline.stop();
                        }
                        Platform.runLater(() -> {
                            clickerStatusLabel.setText("状态: 已停止");
                        });
                    }

                    if(macroState.isRecording() && !macroRecorderWasRunning) {
                        Platform.runLater(() -> {
                            recordButton.setText("停止录制");
                            macroStatusLabel.setText("状态: 正在录制");
                        });
                    }
                    else if(macroState.isPlaying() && !macroPlayerWasRunning) {

                        Platform.runLater(() -> {
                            playButton.setText("停止播放");
                            macroStatusLabel.setText("状态: 正在播放");
                        });
                    }
                    else if(!macroState.isRecording() && macroRecorderWasRunning) {
                        // 保存当前录制的宏
                        Macro currentMacro = macroRecorder.getCurrentMacro();
                        //刷新宏列表
                        if (currentMacro != null) {
                            macroManager.saveMacro(currentMacro);
                        }
                        Platform.runLater(() -> {
                            refreshMacroList();
                            recordButton.setText("开始录制");
                            macroStatusLabel.setText("状态: 已停止");
                        });
                    }
                    else if(!macroState.isPlaying() && macroPlayerWasRunning){
                        Platform.runLater(() -> {
                            playButton.setText("开始播放");
                            macroStatusLabel.setText("状态: 已停止");
                        });
                    }
                    Platform.runLater(() -> {
                        // 更新计数
                        clickerCountLabel.setText("点击次数: " + clickCount.get());
                    });
                    clickerWasRunning = clickerState.isRunning(); // 更新状态记录
                    macroRecorderWasRunning = macroState.isRecording();
                    macroPlayerWasRunning = macroState.isPlaying();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void updateTime() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        long hours = elapsedMillis / 3600000;
        long minutes = (elapsedMillis % 3600000) / 60000;
        long seconds = (elapsedMillis % 60000) / 1000;

        Platform.runLater(() -> {
            clickerTimeLabel.setText(String.format("运行时间: %02d:%02d:%02d", hours, minutes, seconds));
            clickerCountLabel.setText("点击次数: " + clickCount.get());
        });
}

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void cleanup() {
        appState.requestShutdown();
        if (keyboardService != null) {
            keyboardService.stop();
            System.out.println("热键监听服务已停止");
        }
        if(macroState.isRecording()){
            macroRecorder.stop();
            System.out.println("宏录制服务已停止");
        }
        if (macroState.isPlaying()) {
            macroPlayer.stop();
            System.out.println("宏播放服务已停止");
        }
        if (timeline != null) {
            timeline.stop();
        }
        GlobalHookManager.unregisterNativeHook();
    }

    private void updateSpeed(){
        try {
            int speed = Integer.parseInt(speedField.getText());
            if (speed > 0) {
                clickerState.setInterval(speed);
                speedLabel.setText("速度: " + speed + "ms/次");
            } else {
                showAlert("错误", "速度必须大于0");
                speedField.setText(Integer.toString(clickerState.getInterval()));
            }
        } catch (NumberFormatException ex) {
            showAlert("错误", "请输入有效的数字");
            speedField.setText(Integer.toString(clickerState.getInterval()));
        }
    }


    /**
     * 更新选中的宏的循环次数
     */
    private void updateLoopCount() {
        try {
            int loopCount = Integer.parseInt(loopCountField.getText().trim());
            if(loopCount < 0){
                macroState.setLoopCount(1);
                loopCountField.setText("1");
            }else{
                macroState.setLoopCount(loopCount);
            }
        } catch (NumberFormatException e) {
            macroState.setLoopCount(1);
            loopCountField.setText("1");
            showAlert("错误", "请输入正确的数字");
        }
    }

    /**
     * 删除选中的宏
     */
    private void deleteMacro() {
        Macro selectedMacro = macroListView.getSelectionModel().getSelectedItem();
        if (selectedMacro != null) {
            boolean success = macroManager.deleteMacro(selectedMacro);
            if (success) {
                refreshMacroList();
                
                // 清空选中状态
                macroState.setSelectedMacro(null);
                selectedMacroLabel.setText("当前选中: 无");
                macroEventsLabel.setText("事件数量: 0");
                macroDurationLabel.setText("总时长: 0ms");
                playButton.setDisable(true);
                deleteButton.setDisable(true);
            } else {
                showAlert("错误", "删除宏失败");
            }
        }
    }
    
    /**
     * 刷新宏列表
     */
    private void refreshMacroList() {
        macroListView.getItems().clear();
        macroListView.getItems().addAll(macroState.getMacros());
    }
    
    /**
     * 更新宏信息显示
     */
    private void updateMacroInfo(Macro macro) {
        if (macro != null) {
            selectedMacroLabel.setText("当前选中: " + macro.getName());
            macroEventsLabel.setText("事件数量: " + macro.getEventCount());
            macroDurationLabel.setText("总时长: " + macro.getDuration() + "ms");
            loopCountField.setText(String.valueOf(macroState.getLoopCount()));
            deleteButton.setDisable(false);
            playButton.setDisable(false);
        } else {
            selectedMacroLabel.setText("当前选中: 无");
            macroEventsLabel.setText("事件数量: 0");
            macroDurationLabel.setText("总时长: 0ms");
            deleteButton.setDisable(true);
            playButton.setDisable(true);
        }
    }
}