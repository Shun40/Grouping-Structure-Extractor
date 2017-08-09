package gui;

import java.util.ArrayList;

import engine.Analyzer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MainScene extends Scene {
	private TextField textField;
	private Button button;
	private ParameterSlider S2a;
	private ParameterSlider S2b;
	private ParameterSlider S3a;
	private ParameterSlider S3d;
	private ParameterSlider S6;
	private ParameterSlider Wm;
	private ParameterSlider Wl;
	private ParameterSlider Ws;
	private ParameterSlider TLow;
	private Viewer viewer;

	public MainScene(Group root, int width, int height) {
		super(root, width, height);
		setupTextField();
		setupButton();
		setupSliders();
		setupViewer();
	}

	public void setupTextField() {
		textField = new TextField("C:\\Users\\yamashita\\Desktop\\GTTM_Sample\\sample.mid"); // テスト用midiファイル
		textField.setPrefColumnCount(36);
		textField.setLayoutX(20);
		textField.setLayoutY(20);
		((Group)getRoot()).getChildren().add(textField);
	}

	public void setupButton() {
		button = new Button("Extract");
		button.setLayoutX(430);
		button.setLayoutY(20);
		button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				Analyzer analyzer = new Analyzer();
				ArrayList<Integer> DLow = analyzer.extractGroupingStructure(
						textField.getText(),
						S2a.getValue(), S2b.getValue(), S3a.getValue(), S3d.getValue(), S6.getValue(),
						Wm.getValue(), Wl.getValue(), Ws.getValue(),
						TLow.getValue());
				viewer.show(textField.getText(), DLow);
			}
		});
		((Group)getRoot()).getChildren().add(button);
	}

	public void setupSliders() {
		S2a = new ParameterSlider("S2a (スラー/休符)", 0.0, 1.0, 1.0, 20, 70);
		S2b = new ParameterSlider("S2b (アタックポイント)", 0.0, 1.0, 1.0, 200, 70);
		S3a = new ParameterSlider("S3a (音高差)", 0.0, 1.0, 1.0, 380, 70);
		S3d = new ParameterSlider("S3d (音価)", 0.0, 1.0, 1.0, 560, 70);
		S6  = new ParameterSlider("S6 (メロディの並列性)", 0.0, 1.0, 1.0, 740, 70);
		Wm  = new ParameterSlider("Wm (GPR6 P1)", 0.0, 1.0, 0.5, 20, 150);
		Wl  = new ParameterSlider("Wl (GPR6 P2)", 0.0, 1.0, 0.0, 200, 150);
		Ws  = new ParameterSlider("Ws (GPR6 P3)", 0.0, 1.0, 0.5, 380, 150);
		TLow = new ParameterSlider("TLow (境界閾値)", 0.0, 1.0, 0.25, 20, 230);
		((Group)getRoot()).getChildren().add(S2a);
		((Group)getRoot()).getChildren().add(S2b);
		((Group)getRoot()).getChildren().add(S3a);
		((Group)getRoot()).getChildren().add(S3d);
		((Group)getRoot()).getChildren().add(S6);
		((Group)getRoot()).getChildren().add(Wm);
		((Group)getRoot()).getChildren().add(Wl);
		((Group)getRoot()).getChildren().add(Ws);
		((Group)getRoot()).getChildren().add(TLow);
	}

	public void setupViewer() {
		viewer = new Viewer(20, 320);
		((Group)getRoot()).getChildren().add(viewer);
	}
}
