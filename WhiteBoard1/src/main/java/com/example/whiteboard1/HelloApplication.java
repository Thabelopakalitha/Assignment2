// HelloApplication.java
package com.example.whiteboard1;

import com.example.whiteboardController.WhiteboardController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Canvas canvas = new Canvas(800, 600);
        root.setCenter(canvas);

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

        ToolBar toolbar = new ToolBar(
                clearButton, colorPicker, brushSelector, brushSizeSlider, mediaSelector, mediaButton, saveButton
        );
        root.setTop(toolbar);

        WhiteboardController controller = new WhiteboardController(canvas);

        clearButton.setOnAction(e -> controller.clearCanvas());
        colorPicker.setOnAction(e -> controller.setSelectedColor(colorPicker.getValue()));
        brushSelector.setOnAction(e -> controller.setBrushType(brushSelector.getValue()));
        brushSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> controller.setBrushSize(newVal.doubleValue()));
        mediaButton.setOnAction(e -> controller.addMediaFromChooser(primaryStage, mediaSelector.getValue()));
        saveButton.setOnAction(e -> controller.saveCanvas(primaryStage));

        Scene scene = new Scene(root, 900, 700);

        // Inline styling
        root.setStyle("-fx-background-color: #f0f0f0;");
        toolbar.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 10;");

        clearButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 15; -fx-cursor: hand;");
        clearButton.setOnMouseEntered(e -> clearButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 15; -fx-cursor: hand;"));
        clearButton.setOnMouseExited(e -> clearButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 15; -fx-cursor: hand;"));

        mediaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 15; -fx-cursor: hand;");
        mediaButton.setOnMouseEntered(e -> mediaButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 15; -fx-cursor: hand;"));
        mediaButton.setOnMouseExited(e -> mediaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 15; -fx-cursor: hand;"));

        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 15; -fx-cursor: hand;");
        saveButton.setOnMouseEntered(e -> saveButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 15; -fx-cursor: hand;"));
        saveButton.setOnMouseExited(e -> saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 15; -fx-cursor: hand;"));

        colorPicker.setStyle("-fx-padding: 0 5 0 0;");
        brushSelector.setStyle("-fx-pref-width: 100;");
        brushSizeSlider.setStyle("-fx-pref-width: 150;");

        primaryStage.setTitle("Interactive Digital Whiteboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}