package MIDI;

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

public class MidiUtil {
	public static NoteEvents extractNoteEvents(String midiFilePath) {
		NoteEvents events = new NoteEvents();
		File midiFile = new File(midiFilePath);
		try {
			Sequence sequence = MidiSystem.getSequence(midiFile);
			events = extractNoteEvents(sequence);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return events;
	}

	public static NoteEvents extractNoteEvents(Sequence sequence) {
		NoteEvents events = new NoteEvents();
		Track track = sequence.getTracks()[0]; // メロディはトラック1に入っている前提
		for(int i = 0; i < track.size(); i++) {
			byte statByte1 = track.get(i).getMessage().getMessage()[0];
			byte dataByte1 = track.get(i).getMessage().getMessage()[1];
			if((statByte1 & 0xF0) == 0x90) { // ノートオンメッセージかチェック
				for(int j = i; j < track.size(); j++) {
					byte statByte2 = track.get(j).getMessage().getMessage()[0];
					byte dataByte2 = track.get(j).getMessage().getMessage()[1];
					if((statByte2 & 0xF0) == 0x80) { // ノートオフメッセージかチェック
						if(dataByte1 == dataByte2) { // ノート番号の比較
							long noteOnTick = track.get(i).getTick();
							long noteOffTick = track.get(j).getTick();
							int noteNumber = Byte.toUnsignedInt(dataByte1);
							events.add(new NoteEvent(noteOnTick, noteOffTick, noteNumber));
							break;
						}
					}
				}
			}
		}
		return events;
	}
}
