module com.example.whiteboard1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.swing;

    opens com.example.whiteboard1 to javafx.fxml;
    exports com.example.whiteboard1;
    exports com.example.whiteboardController;
    opens com.example.whiteboardController to javafx.fxml;
}