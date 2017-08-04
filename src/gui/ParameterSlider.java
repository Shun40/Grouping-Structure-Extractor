package gui;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class ParameterSlider extends Group {
	private Label label;
	private Slider slider;

	public ParameterSlider(String name, double min, double max, double current, int x, int y) {
		super();
		setLayoutX(x);
		setLayoutY(y);

		setupLabel(name);
		setupSlider(min, max, current);
	}

	private void setupLabel(String name) {
		label = new Label(name);
		label.setLayoutX(0);
		label.setLayoutY(0);
		getChildren().add(label);
	}

	private void setupSlider(double min, double max, double current) {
		slider = new Slider(min, max, current);
		slider.setMajorTickUnit(0.25);
		slider.setMinorTickCount(0);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.setSnapToTicks(true);
		slider.setLayoutX(0);
		slider.setLayoutY(20);
		getChildren().add(slider);
	}

	public double getValue() {
		return slider.getValue();
	}
}
