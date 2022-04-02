package GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 639, 488);
        stage.setTitle("Caesar's cipher!");
        stage.setResizable(false);
        stage.setScene(scene);
        Thread.sleep(1000);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}