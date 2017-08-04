package gui;

import java.util.ArrayList;

import MIDI.MidiNoteEvent;
import MIDI.MidiNoteEvents;
import MIDI.MidiUtil;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Viewer extends Group {
	public Viewer(int x, int y) {
		super();
		setLayoutX(x);
		setLayoutY(y);
	}

	public void show(String midiFilePath, ArrayList<Integer> DLow) {
		MidiNoteEvents midiNoteEvents = MidiUtil.extractMidiNoteEvents(midiFilePath);

		getChildren().clear();

		int prev = 1;
		for(int n = 0; n < midiNoteEvents.size(); n++) {
			// 座標や幅の計算
			MidiNoteEvent midiNoteEvent = midiNoteEvents.get(n);
			String mbt = midiNoteEvent.getNoteOnTickInMBT();
			int measure = Integer.parseInt(mbt.split(":")[0]);
			int beat = Integer.parseInt(mbt.split(":")[1]);
			int tick = Integer.parseInt(mbt.split(":")[2]);
			long length = midiNoteEvent.getLength();
			int noteNumber = midiNoteEvent.getNoteNumber();

			int x = 160 * (measure - 1) + 40 * (beat - 1) + 10 * (tick - 1);
			int y = 10 * (84 - noteNumber);
			int width = 10 * ((int)length / 240);
			int height = 10;

			// 境界描画
			int _DLow = DLow.get(n);
			if(_DLow == 0 && n != midiNoteEvents.size() - 1) {
				if(prev == 0) {
					Line line = new Line(x, 150, x + width, 150);
					line.setStroke(Color.RED);
					line.setStrokeWidth(1 + 0.5);
					getChildren().add(line);
				} else {
					Line line1 = new Line(x + 5, 150, x + width, 150);
					Line line2 = new Line(x + 5, 145, x + 5, 150);
					line1.setStroke(Color.RED);
					line1.setStrokeWidth(1 + 0.5);
					line2.setStroke(Color.RED);
					line2.setStrokeWidth(1 + 0.5);
					getChildren().add(line1);
					getChildren().add(line2);
				}
			} else {
				if(prev == 0) {
					Line line1 = new Line(x, 150, x + width - 5, 150);
					Line line2 = new Line(x + width - 5, 145, x + width - 5, 150);
					line1.setStroke(Color.RED);
					line1.setStrokeWidth(1 + 0.5);
					line2.setStroke(Color.RED);
					line2.setStrokeWidth(1 + 0.5);
					getChildren().add(line1);
					getChildren().add(line2);
				} else {
					Line line1 = new Line(x + 5, 150, x + width - 5, 150);
					Line line2 = new Line(x + 5, 145, x + 5, 150);
					Line line3 = new Line(x + width - 5, 145, x + width - 5, 150);
					line1.setStroke(Color.RED);
					line1.setStrokeWidth(1 + 0.5);
					line2.setStroke(Color.RED);
					line2.setStrokeWidth(1 + 0.5);
					line3.setStroke(Color.RED);
					line3.setStrokeWidth(1 + 0.5);
					getChildren().add(line1);
					getChildren().add(line2);
					getChildren().add(line3);
				}
			}
			prev = _DLow;

			// ノート描画
			Rectangle block = new Rectangle(x + 0.5, y + 0.5, width, height);
			block.setFill(Color.GRAY);
			block.setStroke(Color.BLACK);
			getChildren().add(block);
		}
	}
}
