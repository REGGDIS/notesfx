package com.regdevs.notesfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        var label = new Label("¡Hola desde NotesFX!");
        var scene = new Scene(label, 400, 300);
        stage.setScene(scene);
        stage.setTitle("NotesFX");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
