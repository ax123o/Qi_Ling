package com.hnu.view;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.hnu.model.AppState;
import com.hnu.model.ClickerState;
import com.hnu.service.ClickerService;
import com.hnu.service.KeyboardService;
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
    private AtomicInteger clickCount = new AtomicInteger(0);
    private long startTime;
    private KeyboardService keyboardService;

    // 页面相关字段

    private Label speedLabel;
    private Label selectedKeyLabel;
    private Label clickerStatusLabel;
    private Label clickerCountLabel;
    private Label clickerTimeLabel;
    private long clickerStartTime = 0;
    private Timeline timeline;

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

        primaryStage.setTitle("智能连点器");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            cleanup();
            Platform.exit();
        });

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                primaryStage.close(); // 这会触发setOnCloseRequest
            }
        });

        // 启动状态更新线程
        startStatusUpdateThread();
    }

    private void initializeServices() {
        robot = RobotFactory.createRobot();

        //初始化热键监听服务
        keyboardService = new KeyboardService(clickerState, null);
        keyboardService.start();

        //初始化连点服务
        Thread clickerThread = new Thread(new ClickerService(clickerState, robot, clickCount));
        clickerThread.setDaemon(true);
        clickerThread.start();
    }

    private VBox createRootLayout() {
        VBox root = new VBox(10);
        root.getStyleClass().add("app-root"); // 添加CSS类

        // 标题
        Label titleLabel = new Label("自动连点器");
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

        // 这里添加宏功能的具体控件
        Label infoLabel = new Label("宏功能正在开发中...");
        infoLabel.getStyleClass().add("info-text"); // 添加CSS类

        page.getChildren().addAll(infoLabel);
        return page;
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

        TextField speedField = new TextField(String.valueOf(clickerState.getInterval()));
        speedField.getStyleClass().add("section-field");

        Button speedButton = new Button("应用");
        speedButton.getStyleClass().add("action-button");

        speedButton.setOnAction(e -> {
            try {
                int speed = Integer.parseInt(speedField.getText());
                if (speed > 0) {
                    clickerState.setInterval(speed);
                    speedLabel.setText("速度: " + speed + "ms/次");
                } else {
                    showAlert("错误", "速度必须大于0");
                }
            } catch (NumberFormatException ex) {
                showAlert("错误", "请输入有效的数字");
            }
        });
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

        // 为设置键盘按键的窗口也添加ESC键监听
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                keyboardService.clearKeyConsumer();
                keyStage.close();
            }
        });

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
            boolean wasRunning = false; // 记录上一次的状态
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTime()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            while (!appState.isShutdownRequested()) {
                try {
                    Thread.sleep(100);
                    if (clickerState.isRunning() && !wasRunning) {
                        Platform.runLater(() -> {
                            clickerStatusLabel.setText("状态: 运行中");
                            startTime = System.currentTimeMillis();
                            timeline.play();
                        });
                    }
                    else if (!clickerState.isRunning() && wasRunning) {
                        Platform.runLater(() -> {
                            clickerStatusLabel.setText("状态: 已停止");
                            // 停止计时器
                            if (timeline != null) {
                                timeline.stop();
                            }
                        });
                    }
                    Platform.runLater(() -> {
                        // 更新计数
                        clickerCountLabel.setText("点击次数: " + clickCount.get());
                    });
                    wasRunning = clickerState.isRunning(); // 更新状态记录
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
        }
    }
}