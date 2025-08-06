package com.regdevs.notesfx;

import com.regdevs.notesfx.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView.getRoot(), 1000, 700);
        stage.setScene(scene);
        stage.setTitle("NotesFX");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
