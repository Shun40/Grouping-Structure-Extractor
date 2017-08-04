package MIDI;

import java.util.ArrayList;

public class MidiNoteEvents extends ArrayList<MidiNoteEvent> {
	public MidiNoteEvents() {
		super();
	}

	// m小節目b拍目から1拍分の区間にあるMIDIノートイベントを抽出する
	public MidiNoteEvents extractMidiEventsInSection(int m, int b) {
		MidiNoteEvents midiNoteEvents = new MidiNoteEvents();
		for(MidiNoteEvent midiNoteEvent : this) {
			String mbt = midiNoteEvent.getNoteOnTickInMBT();
			int _m = Integer.parseInt(mbt.split(":")[0]);
			int _b = Integer.parseInt(mbt.split(":")[1]);
			if(_m == m && _b == b) {
				midiNoteEvents.add(midiNoteEvent);
			}
		}
		return midiNoteEvents;
	}

	// b拍目から1拍分の区間にあるMIDIノートイベントを抽出する
	public MidiNoteEvents extractMidiEventsInSection(int b) {
		int _m = ((b - 1) / 4) + 1;
		int _b = ((b - 1) % 4) + 1;
		return extractMidiEventsInSection(_m, _b);
	}

	// 区間[m, m+1)における最先頭の音符を返す
	public MidiNoteEvent head(int m) {
		return extractMidiEventsInSection(m).get(0);
	}

	// 区間[m, m+1)における最後尾の音符を返す
	public MidiNoteEvent tail(int m) {
		return extractMidiEventsInSection(m).get(extractMidiEventsInSection(m).size() - 1);
	}

	// MIDIノートイベントiが区間[m, m+1)に現れる時にmを返す
	public int beat(MidiNoteEvent i) {
		MidiNoteEvent lastEvent = get(size() - 1);
		int L = 4 * lastEvent.getNoteOnTickInM();
		int m = 0;

		for(int l = 1; l <= L; l++) {
			MidiNoteEvents eventsL = extractMidiEventsInSection(l);
			for(MidiNoteEvent eventL : eventsL) {
				if(i == eventL) {
					m = l;
				}
			}
		}
		return m;
	}

	// MIDIノートイベント群の情報を出力する
	public void show() {
		for(MidiNoteEvent midiNoteEvent : this) {
			String noteOnTick = midiNoteEvent.getNoteOnTickInMBT();
			long length = midiNoteEvent.getLength();
			int noteNumber = midiNoteEvent.getNoteNumber();
			System.out.println(noteOnTick + " " + length + " " + noteNumber);
		}
	}
}
