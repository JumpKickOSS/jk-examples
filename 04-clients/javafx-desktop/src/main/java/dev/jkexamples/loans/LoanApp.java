package dev.jkexamples.loans;

import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** The JavaFX application: one window, the view loaded from {@code loan.fxml}. */
public class LoanApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(LoanApp.class.getResource("loan.fxml")));
        stage.setTitle("Loan calculator");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
