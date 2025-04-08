package com.example.whiteboardController;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardController {
    private final GraphicsContext gc;
    private final Canvas canvas;
    private final Pane canvasPane;
    private Color selectedColor = Color.BLACK;
    private double brushSize = 2;
    private String brushType = "Pencil";
    private double initialX, initialY;

    private final List<ImageObject> images = new ArrayList<>();
    private final List<MediaPlayerObject> mediaPlayers = new ArrayList<>();
    private final List<TextObject> texts = new ArrayList<>();
    private ImageObject selectedImage;
    private MediaPlayerObject selectedMediaPlayer; 
    private double mediaDragOffsetX;
    private double mediaDragOffsetY;

    private boolean resizing = false;

    public WhiteboardController(Canvas canvas, Pane canvasPane) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.canvasPane = canvasPane;
        setupDrawingEvents();
        setupImageDraggingAndResizing();
        setupMediaInteraction();
        setupMediaDragging(); // New method for media dragging
        redraw();
    }

    public void setSelectedColor(Color color) {
        this.selectedColor = color;
    }

    public void setBrushSize(double size) {
        this.brushSize = size;
    }

    public void setBrushType(String type) {
        this.brushType = type;
    }

    public void addText(String text, double x, double y, Color color) {
        texts.add(new TextObject(text, x, y, color));
        redraw();
    }

    public void addImage(Image image) {
        ImageObject imgObj = new ImageObject(image, 10, 10);
        images.add(imgObj);
        redraw();
    }

    public void addMediaFromChooser(Stage stage, String mediaType) {
        FileChooser fileChooser = new FileChooser();
        if ("Image".equals(mediaType)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        } else if ("Audio".equals(mediaType)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        } else if ("Video".equals(mediaType)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi"));
        }
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                if ("Image".equals(mediaType)) {
                    Image image = new Image(selectedFile.toURI().toString());
                    addImage(image);
                } else if ("Audio".equals(mediaType)) {
                    Media media = new Media(selectedFile.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayers.add(new MediaPlayerObject(mediaPlayer, 50, 50, 100, 75));
                    mediaPlayer.play();
                } else if ("Video".equals(mediaType)) {
                    Media media = new Media(selectedFile.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    MediaPlayerObject mediaPlayerObject = new MediaPlayerObject(mediaPlayer, 50, 50, 160, 120);
                    mediaPlayers.add(mediaPlayerObject);

                    // Create and add MediaView to the canvasPane
                    MediaView mediaView = new MediaView(mediaPlayer);
                    mediaView.setX(mediaPlayerObject.getX());
                    mediaView.setY(mediaPlayerObject.getY());
                    mediaView.setFitWidth(mediaPlayerObject.getWidth());
                    mediaView.setFitHeight(mediaPlayerObject.getHeight());
                    canvasPane.getChildren().add(mediaView);
                    mediaPlayerObject.setMediaView(mediaView); // Store the MediaView
                    mediaPlayer.play();
                }
                redraw(); // Redraw the canvas elements (excluding video)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        images.clear();
        mediaPlayers.forEach(mp -> {
            if (mp.getMediaPlayer() != null) {
                mp.getMediaPlayer().stop();
            }
            if (mp.getMediaView() != null) {
                canvasPane.getChildren().remove(mp.getMediaView());
            }
        });
        mediaPlayers.clear();
        texts.clear();
    }

    public void saveCanvas(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupDrawingEvents() {
        canvas.setOnMousePressed(e -> {
            initialX = e.getX();
            initialY = e.getY();
            gc.setStroke("Eraser".equals(brushType) ? Color.WHITE : selectedColor);
            gc.setLineWidth("Marker".equals(brushType) ? brushSize * 2 : brushSize);
            if (List.of("Pencil", "Marker", "Eraser").contains(brushType)) {
                gc.beginPath();
                gc.moveTo(initialX, initialY);
                gc.stroke();
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (List.of("Pencil", "Marker", "Eraser").contains(brushType)) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (!List.of("Rectangle", "Circle", "Line").contains(brushType)) return;

            double x = Math.min(initialX, e.getX());
            double y = Math.min(initialY, e.getY());
            double width = Math.abs(e.getX() - initialX);
            double height = Math.abs(e.getY() - initialY);

            gc.setStroke(selectedColor);
            gc.setLineWidth(brushSize);
            gc.setFill(selectedColor);

            switch (brushType) {
                case "Rectangle" -> {
                    gc.fillRect(x, y, width, height);
                    gc.strokeRect(x, y, width, height);
                }
                case "Circle" -> {
                    gc.fillOval(x, y, width, height);
                    gc.strokeOval(x, y, width, height);
                }
                case "Line" -> gc.strokeLine(initialX, initialY, e.getX(), e.getY());
            }
        });
    }

    private void setupImageDraggingAndResizing() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (!brushType.equals("Image")) return;
            selectedImage = null;
            resizing = false;
            for (ImageObject img : images) {
                if (img.isInsideResizeHandle(e.getX(), e.getY())) {
                    selectedImage = img;
                    resizing = true;
                    initialX = e.getX();
                    initialY = e.getY();
                    break;
                } else if (img.isInside(e.getX(), e.getY())) {
                    selectedImage = img;
                    initialX = e.getX() - img.getX();
                    initialY = e.getY() - img.getY();
                    break;
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (selectedImage != null && brushType.equals("Image")) {
                if (resizing) {
                    selectedImage.setWidth(e.getX() - selectedImage.getX());
                    selectedImage.setHeight(e.getY() - selectedImage.getY());
                    redraw();
                } else {
                    selectedImage.setX(e.getX() - initialX);
                    selectedImage.setY(e.getY() - initialY);
                    redraw();
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            resizing = false;
        });
    }

    private void setupMediaInteraction() {
        canvasPane.setOnMouseClicked(e -> {
            boolean mediaClickedForControl = false;
            for (MediaPlayerObject mediaObj : mediaPlayers) {
                if (mediaObj.getMediaView() != null &&
                        e.getX() >= mediaObj.getX() && e.getX() <= mediaObj.getX() + mediaObj.getWidth() &&
                        e.getY() >= mediaObj.getY() && e.getY() <= mediaObj.getY() + mediaObj.getHeight()) {
                    MediaPlayer player = mediaObj.getMediaPlayer();
                    if (player.getStatus() == MediaPlayer.Status.PLAYING) {
                        player.pause();
                    } else if (player.getStatus() == MediaPlayer.Status.PAUSED || player.getStatus() == MediaPlayer.Status.READY) {
                        player.play();
                    }
                    mediaClickedForControl = true;
                    break;
                }
            }
        });
    }

    private void setupMediaDragging() {
        canvasPane.setOnMousePressed(e -> {
            selectedMediaPlayer = null;
            for (MediaPlayerObject mediaObj : mediaPlayers) {
                if (mediaObj.getMediaView() != null &&
                        e.getX() >= mediaObj.getX() && e.getX() <= mediaObj.getX() + mediaObj.getWidth() &&
                        e.getY() >= mediaObj.getY() && e.getY() <= mediaObj.getY() + mediaObj.getHeight()) {
                    selectedMediaPlayer = mediaObj;
                    mediaDragOffsetX = e.getX() - mediaObj.getX();
                    mediaDragOffsetY = e.getY() - mediaObj.getY();
                    break;
                }
            }
        });

        canvasPane.setOnMouseDragged(e -> {
            if (selectedMediaPlayer != null) {
                selectedMediaPlayer.setX(e.getX() - mediaDragOffsetX);
                selectedMediaPlayer.setY(e.getY() - mediaDragOffsetY);
                selectedMediaPlayer.getMediaView().setX(selectedMediaPlayer.getX());
                selectedMediaPlayer.getMediaView().setY(selectedMediaPlayer.getY());
            }
        });

        canvasPane.setOnMouseReleased(e -> {
            selectedMediaPlayer = null;
        });
    }

    private void redraw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (ImageObject img : images) {
            gc.drawImage(img.getImage(), img.getX(), img.getY(), img.getWidth(), img.getHeight());
        }
        for (TextObject textObj : texts) {
            gc.setFill(textObj.getColor());
            gc.fillText(textObj.getText(), textObj.getX(), textObj.getY());
        }
    }

    private static class ImageObject {
        private final Image image;
        private double x, y;
        private double width, height;

        public ImageObject(Image image, double x, double y) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = image.getWidth() > 50 ? 50 : image.getWidth();
            this.height = image.getHeight() > 50 ? 50 : image.getHeight();
        }

        public boolean isInside(double px, double py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }

        public boolean isInsideResizeHandle(double px, double py) {
            double handleSize = 10;
            return px >= x + width - handleSize && px <= x + width + handleSize &&
                    py >= y + height - handleSize && py <= y + height + handleSize;
        }

        public Image getImage() {
            return image;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }

    private static class MediaPlayerObject {
        private final MediaPlayer mediaPlayer;
        private double x, y;
        private double width;
        private double height;
        private MediaView mediaView; // Store the MediaView

        public MediaPlayerObject(MediaPlayer mediaPlayer, double x, double y, double width, double height) {
            this.mediaPlayer = mediaPlayer;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public MediaView getMediaView() {
            return mediaView;
        }

        public void setMediaView(MediaView mediaView) {
            this.mediaView = mediaView;
        }
    }

    private static class TextObject {
        private String text;
        private double x, y;
        private Color color;

        public TextObject(String text, double x, double y, Color color) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }
}
