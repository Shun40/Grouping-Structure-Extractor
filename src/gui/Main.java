package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		MainScene scene = new MainScene(root, 1000 + 8, 500 + 34);

		stage.setTitle("Grouping Structure Extractor");
		stage.setResizable(false);
		stage.setOnCloseRequest(req -> close());
		stage.setScene(scene);
		stage.show();
	}

	public void close() {
		Platform.exit();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
