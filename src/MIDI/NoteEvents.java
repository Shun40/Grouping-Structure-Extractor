package MIDI;

import java.util.ArrayList;

public class NoteEvents extends ArrayList<NoteEvent> {
	public NoteEvents() {
		super();
	}

	// m小節目b拍目から1拍分の区間にあるノートイベントを抽出する
	public NoteEvents extractNoteEventsInSection(int m, int b) {
		NoteEvents noteEvents = new NoteEvents();
		for(NoteEvent noteEvent : this) {
			int _m = noteEvent.getNoteOnTickInM();
			int _b = noteEvent.getNoteOnTickInB();
			if(_m == m && _b == b) {
				noteEvents.add(noteEvent);
			}
		}
		return noteEvents;
	}

	// b拍目から1拍分の区間にあるMIDIノートイベントを抽出する
	public NoteEvents extractNoteEventsInSection(int b) {
		int _m = ((b - 1) / 4) + 1;
		int _b = ((b - 1) % 4) + 1;
		return extractNoteEventsInSection(_m, _b);
	}

	// 区間[m, m+1)における最先頭の音符を返す
	public NoteEvent head(int m) {
		return extractNoteEventsInSection(m).get(0);
	}

	// 区間[m, m+1)における最後尾の音符を返す
	public NoteEvent tail(int m) {
		return extractNoteEventsInSection(m).get(extractNoteEventsInSection(m).size() - 1);
	}

	// MIDIノートイベントiが区間[m, m+1)に現れる時にmを返す
	public int beat(NoteEvent event) {
		NoteEvent lastEvent = get(size() - 1);
		int L = 4 * lastEvent.getNoteOnTickInM();
		int m = 0;

		for(int l = 1; l <= L; l++) {
			NoteEvents eventsL = extractNoteEventsInSection(l);
			if(eventsL.indexOf(event) != -1) {
				m = l;
			}
		}
		return m;
	}

	// MIDIノートイベント群の情報を出力する
	public void show() {
		for(NoteEvent noteEvent : this) {
			String noteOnTick = noteEvent.getNoteOnTickInMBT();
			long length = noteEvent.getLength();
			int noteNumber = noteEvent.getNoteNumber();
			System.out.println(noteOnTick + " " + length + " " + noteNumber);
		}
	}
}
