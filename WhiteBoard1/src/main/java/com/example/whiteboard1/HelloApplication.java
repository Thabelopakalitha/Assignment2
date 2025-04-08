package com.example.whiteboard1;

import com.example.whiteboardController.WhiteboardController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private WhiteboardController controller;
    private Pane canvasPane;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Canvas canvas = new Canvas(800, 600);
        canvasPane = new Pane(canvas);
        root.setCenter(canvasPane);
        canvas.setFocusTraversable(true);

        // Controls
        Button clearButton = new Button("Clear");
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        ChoiceBox<String> brushSelector = new ChoiceBox<>();
        brushSelector.getItems().addAll("Pencil", "Marker", "Eraser", "Rectangle", "Circle", "Line", "Image");
        brushSelector.setValue("Pencil");

        Slider brushSizeSlider = new Slider(1, 10, 2);
        brushSizeSlider.setShowTickLabels(true);
        brushSizeSlider.setShowTickMarks(true);

        ChoiceBox<String> mediaSelector = new ChoiceBox<>();
        mediaSelector.getItems().addAll("Image", "Audio", "Video");
        mediaSelector.setValue("Image");

        Button mediaButton = new Button("Add Media");
        Button saveButton = new Button("Save");
        Button textButton = new Button("Text");

        ToolBar toolbar = new ToolBar(
                clearButton, colorPicker, brushSelector, brushSizeSlider, mediaSelector, mediaButton, textButton, saveButton
        );
        root.setTop(toolbar);

        controller = new WhiteboardController(canvas, canvasPane);

        clearButton.setOnAction(e -> controller.clearCanvas());
        colorPicker.setOnAction(e -> controller.setSelectedColor(colorPicker.getValue()));
        brushSelector.setOnAction(e -> controller.setBrushType(brushSelector.getValue()));
        brushSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> controller.setBrushSize(newVal.doubleValue()));
        mediaButton.setOnAction(e -> controller.addMediaFromChooser(primaryStage, mediaSelector.getValue()));
        saveButton.setOnAction(e -> controller.saveCanvas(primaryStage));
        textButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("Enter text here");
            dialog.setTitle("Add Text");
            dialog.setHeaderText(null);
            dialog.setContentText("Text:");

            dialog.showAndWait().ifPresent(text -> {
                if (text != null && !text.isEmpty()) {
                    controller.addText(text, 50, 50, colorPicker.getValue());
                }
            });
        });

        Scene scene = new Scene(root, 900, 700);

        root.setStyle("-fx-background-color: #f8f9fa; -fx-font-family: 'Segoe UI';");

        toolbar.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ffffff, #dee2e6);" +
                        " -fx-padding: 10; -fx-spacing: 10;" +
                        " -fx-border-color: #ced4da;" +
                        " -fx-border-width: 0 0 1 0;" +
                        " -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 2);"
        );

        String baseButtonStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15;" +
                " -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;" +
                " -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 2, 0, 1, 1);";

        String hoverButtonStyle = "-fx-background-color: #45a049; -fx-text-fill: white; -fx-padding: 8 15;" +
                " -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;" +
                " -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 2, 0, 1, 1);";

        String textButtonStyle = "-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 8 15;" +
                " -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;" +
                " -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 2, 0, 1, 1);";

        String textButtonHoverStyle = "-fx-background-color: #0056b3; -fx-text-fill: white; -fx-padding: 8 15;" +
                " -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px;" +
                " -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 2, 0, 1, 1);";

        clearButton.setStyle(baseButtonStyle);
        clearButton.setOnMouseEntered(e -> clearButton.setStyle(hoverButtonStyle));
        clearButton.setOnMouseExited(e -> clearButton.setStyle(baseButtonStyle));

        mediaButton.setStyle(baseButtonStyle);
        mediaButton.setOnMouseEntered(e -> mediaButton.setStyle(hoverButtonStyle));
        mediaButton.setOnMouseExited(e -> mediaButton.setStyle(baseButtonStyle));

        saveButton.setStyle(baseButtonStyle);
        saveButton.setOnMouseEntered(e -> saveButton.setStyle(hoverButtonStyle));
        saveButton.setOnMouseExited(e -> saveButton.setStyle(baseButtonStyle));

        textButton.setStyle(textButtonStyle);
        textButton.setOnMouseEntered(e -> textButton.setStyle(textButtonHoverStyle));
        textButton.setOnMouseExited(e -> textButton.setStyle(textButtonStyle));

        colorPicker.setStyle("-fx-background-color: transparent;");
        brushSelector.setStyle("-fx-pref-width: 120; -fx-background-radius: 10;");
        mediaSelector.setStyle("-fx-pref-width: 120; -fx-background-radius: 10;");
        brushSizeSlider.setStyle("-fx-pref-width: 150;");

        primaryStage.setTitle("Interactive Digital Whiteboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
