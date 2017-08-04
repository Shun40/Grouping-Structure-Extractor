package MIDI;

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

public class MidiUtil {
	public static MidiNoteEvents extractMidiNoteEvents(String midiFilePath) {
		MidiNoteEvents midiNoteEvents = new MidiNoteEvents();
		File midiFile = new File(midiFilePath);
		try {
			Sequence sequence = MidiSystem.getSequence(midiFile);
			midiNoteEvents = extractMidiNoteEvents(sequence);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return midiNoteEvents;
	}

	public static MidiNoteEvents extractMidiNoteEvents(Sequence sequence) {
		MidiNoteEvents midiNoteEvents = new MidiNoteEvents();
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
							midiNoteEvents.add(new MidiNoteEvent(noteOnTick, noteOffTick, noteNumber));
							break;
						}
					}
				}
			}
		}
		return midiNoteEvents;
	}

	// 与えられたMIDIノートイベント群間で, 発音時刻が一致するMIDIノートイベント数を抽出する
	public static int extractCountOfSameNoteOn(MidiNoteEvents eventsA, MidiNoteEvents eventsB) {
		int count = 0;
		for(MidiNoteEvent eventA : eventsA) {
			for(MidiNoteEvent eventB : eventsB) {
				int noteOnTickA = eventA.getNoteOnTickInT();
				int noteOnTickB = eventB.getNoteOnTickInT();
				if(noteOnTickA == noteOnTickB) {
					count++;
				}
			}
		}
		return count;
	}

	// 与えられたMIDIノートイベント群間で, 発音時刻と音高が一致するMIDIノートイベント数を抽出する
	public static int extractCountOfSameNoteOnAndNoteNumber(MidiNoteEvents eventsA, MidiNoteEvents eventsB) {
		int count = 0;
		for(MidiNoteEvent eventA : eventsA) {
			for(MidiNoteEvent eventB : eventsB) {
				int noteOnTickA = eventA.getNoteOnTickInT();
				int noteOnTickB = eventB.getNoteOnTickInT();
				int noteNumberA = eventA.getNoteNumber();
				int noteNumberB = eventB.getNoteNumber();
				if(noteOnTickA == noteOnTickB && noteNumberA == noteNumberB) {
					count++;
				}
			}
		}
		return count;
	}
}
