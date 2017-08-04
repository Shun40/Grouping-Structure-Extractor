package engine;

import java.util.ArrayList;
import java.util.Collections;

import MIDI.MidiNoteEvent;
import MIDI.MidiNoteEvents;
import MIDI.MidiUtil;

public class MusicalInformation {
	private int L; // 曲全体の拍数
	private ArrayList<Integer> Nm; // 区間[m, m+1)に存在する音符数
	private ArrayList<ArrayList<Integer>> Omn; // 区間[m, m+1)と区間[n, n+1)の音符のうち, 発音時刻が一致する音符数
	private ArrayList<ArrayList<Integer>> Pmn; // 区間[m, m+1)と区間[n, n+1)の音符のうち, 発音時刻と音高が一致する音符数

	public MusicalInformation() {
		L = 0;
		Nm = new ArrayList<Integer>();
		Omn = new ArrayList<ArrayList<Integer>>();
		Pmn = new ArrayList<ArrayList<Integer>>();
	}

	public MusicalInformation(MidiNoteEvents midiNoteEvents) {
		L = extractL(midiNoteEvents);
		System.out.println("L: " + L);

		Nm = extractNm(midiNoteEvents);
		for(int m = 0; m < Nm.size(); m++) {
			System.out.println("m = " + (m + 1) + ": " + Nm.get(m));
		}

		Omn = extractOmn(midiNoteEvents);
		for(int m = 0; m < Omn.size(); m++) {
			for(int n = 0; n < Omn.get(m).size(); n++) {
				System.out.println("m = " + (m + 1) + ", n = " + (n + 1) + ": " + Omn.get(m).get(n));
			}
		}

		Pmn = extractPmn(midiNoteEvents);
		for(int m = 0; m < Pmn.size(); m++) {
			for(int n = 0; n < Pmn.get(m).size(); n++) {
				System.out.println("m = " + (m + 1) + ", n = " + (n + 1) + ": " + Pmn.get(m).get(n));
			}
		}
	}

	public int extractL(MidiNoteEvents midiNoteEvents) {
		int L = 0;
		MidiNoteEvent lastEvent = midiNoteEvents.get(midiNoteEvents.size() - 1);
		int m = lastEvent.getNoteOnTickInM();
		L = 4 * m;
		return L;
	}

	public ArrayList<Integer> extractNm(MidiNoteEvents midiNoteEvents) {
		ArrayList<Integer> Nm = new ArrayList<Integer>();

		// 各拍区間の音符数を抽出する
		int L = extractL(midiNoteEvents);
		for(int l = 1; l <= L; l++) {
			Nm.add(midiNoteEvents.extractMidiEventsInSection(l).size());
		}
		return Nm;
	}

	public ArrayList<ArrayList<Integer>> extractOmn(MidiNoteEvents midiNoteEvents) {
		ArrayList<ArrayList<Integer>> Omn = new ArrayList<ArrayList<Integer>>();

		// Omnの全要素を0で初期化
		int L = extractL(midiNoteEvents);
		for(int l = 0; l < L; l++) {
			Omn.add(new ArrayList<Integer>(Collections.nCopies(L, 0)));
		}

		// 各拍区間のMIDIノートイベントを抽出する
		ArrayList<MidiNoteEvents> eventsInSubSection = new ArrayList<MidiNoteEvents>(L);
		for(int l = 1; l <= L; l++) {
			eventsInSubSection.add(midiNoteEvents.extractMidiEventsInSection(l));
		}

		for(int m = 0; m < L; m++) {
			for(int n = 0; n < L; n++) {
				MidiNoteEvents eventsM = eventsInSubSection.get(m);
				MidiNoteEvents eventsN = eventsInSubSection.get(n);
				int count = MidiUtil.extractCountOfSameNoteOn(eventsM, eventsN);
				Omn.get(m).set(n, count);
			}
		}
		return Omn;
	}

	public ArrayList<ArrayList<Integer>> extractPmn(MidiNoteEvents midiNoteEvents) {
		ArrayList<ArrayList<Integer>> Pmn = new ArrayList<ArrayList<Integer>>();

		// Pmnの全要素を0で初期化
		int L = extractL(midiNoteEvents);
		for(int l = 0; l < L; l++) {
			Pmn.add(new ArrayList<Integer>(Collections.nCopies(L, 0)));
		}

		// 各拍区間のMIDIノートイベントを抽出する
		ArrayList<MidiNoteEvents> eventsInSubSection = new ArrayList<MidiNoteEvents>(L);
		for(int l = 1; l <= L; l++) {
			eventsInSubSection.add(midiNoteEvents.extractMidiEventsInSection(l));
		}

		for(int m = 0; m < L; m++) {
			for(int n = 0; n < L; n++) {
				MidiNoteEvents eventsM = eventsInSubSection.get(m);
				MidiNoteEvents eventsN = eventsInSubSection.get(n);
				int count = MidiUtil.extractCountOfSameNoteOnAndNoteNumber(eventsM, eventsN);
				Pmn.get(m).set(n, count);
			}
		}
		return Pmn;
	}

	public int getL() {
		return L;
	}

	public ArrayList<Integer> getNm() {
		return Nm;
	}

	public ArrayList<ArrayList<Integer>> getOmn() {
		return Omn;
	}

	public ArrayList<ArrayList<Integer>> getPmn() {
		return Pmn;
	}
}
