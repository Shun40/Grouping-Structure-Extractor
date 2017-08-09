package gui;

import java.util.ArrayList;

import MIDI.MidiUtil;
import MIDI.NoteEvent;
import MIDI.NoteEvents;
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
		NoteEvents events = MidiUtil.extractNoteEvents(midiFilePath);

		getChildren().clear();

		// 小節線描画
		NoteEvent lastEvent = events.get(events.size() - 1);
		int lastMeasure = lastEvent.getNoteOnTickInM();
		for(int measure = 1; measure <= lastMeasure + 1; measure++) {
			int x = 160 * (measure - 1);
			Line line = new Line(x + 0.5, 0, x + 0.5, 150);
			line.setStroke(Color.GRAY);
			getChildren().add(line);
		}

		int prev = 1;
		for(int n = 0; n < events.size(); n++) {
			// 座標や幅の計算
			NoteEvent event = events.get(n);
			int measure =  event.getNoteOnTickInM();
			int beat = event.getNoteOnTickInB();
			int tick = event.getNoteOnTickInT();
			long length = event.getLength();
			int noteNumber = event.getNoteNumber();

			int x = 160 * (measure - 1) + 40 * (beat - 1) + 10 * (tick - 1);
			int y = 10 * (84 - noteNumber);
			int width = 10 * ((int)length / 240);
			int height = 10;

			// 境界描画
			int _DLow = DLow.get(n);
			if(_DLow == 0 && n != events.size() - 1) {
				if(prev == 0) {
					Line line = new Line(x, 150 + 0.5, x + width, 150 + 0.5);
					line.setStroke(Color.RED);
					getChildren().add(line);
				} else {
					Line line1 = new Line(x + 5, 150 + 0.5, x + width, 150 + 0.5);
					Line line2 = new Line(x + 5 - 0.5, 145, x + 5 - 0.5, 150);
					line1.setStroke(Color.RED);
					line2.setStroke(Color.RED);
					getChildren().add(line1);
					getChildren().add(line2);
				}
			} else {
				if(prev == 0) {
					Line line1 = new Line(x, 150 + 0.5, x + width - 5, 150 + 0.5);
					Line line2 = new Line(x + width - 5 + 0.5, 145, x + width - 5 + 0.5, 150);
					line1.setStroke(Color.RED);
					line2.setStroke(Color.RED);
					getChildren().add(line1);
					getChildren().add(line2);
				} else {
					Line line1 = new Line(x + 5, 150 + 0.5, x + width - 5, 150 + 0.5);
					Line line2 = new Line(x + 5 - 0.5, 145, x + 5 - 0.5, 150);
					Line line3 = new Line(x + width - 5 + 0.5, 145, x + width - 5 + 0.5, 150);
					line1.setStroke(Color.RED);
					line2.setStroke(Color.RED);
					line3.setStroke(Color.RED);
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
