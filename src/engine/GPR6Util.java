package engine;

import MIDI.NoteEvent;
import MIDI.NoteEvents;

public class GPR6Util {
	// 曲全体の拍数Lを抽出する
	public static int extractL(NoteEvents events) {
		int L = 0;
		NoteEvent lastEvent = events.get(events.size() - 1);
		int lastMeasure = lastEvent.getNoteOnTickInM();
		L = 4 * lastMeasure;
		return L;
	}

	// 区間[m, m+1)に存在する音符数Nを抽出する
	public static int[] extractN(NoteEvents events) {
		int L = extractL(events);

		// 各拍区間のMIDIノートイベントを抽出する
		NoteEvents[] eventsInSubSection = new NoteEvents[L];
		for(int l = 1; l <= L; l++) {
			eventsInSubSection[l - 1] = events.extractNoteEventsInSection(l);
		}

		// 音符数を抽出する
		int[] N = new int[L];
		for(int m = 1; m <= L; m++) {
			N[m - 1] = eventsInSubSection[m - 1].size();
		}
		return N;
	}

	// 区間[m, m+1)と区間[n, n+1)の音符のうち, 発音時刻が一致する音符数Oを抽出する
	public static int[][] extractO(NoteEvents events) {
		int L = extractL(events);

		// 各拍区間のMIDIノートイベントを抽出する
		NoteEvents[] eventsInSubSection = new NoteEvents[L];
		for(int l = 1; l <= L; l++) {
			eventsInSubSection[l - 1] = events.extractNoteEventsInSection(l);
		}

		// 発音時刻が同じ音符数を抽出する
		int[][] O = new int[L][L];
		for(int m = 1; m <= L; m++) {
			for(int n = 1; n <= L; n++) {
				NoteEvents eventsM = eventsInSubSection[m - 1];
				NoteEvents eventsN = eventsInSubSection[n - 1];
				O[m - 1][n - 1] = extractCountOfSameAttack(eventsM, eventsN);
			}
		}
		return O;
	}

	// 区間[m, m+1)と区間[n, n+1)の音符のうち, 発音時刻と音高が一致する音符数Pを抽出する
	public static int[][] extractP(NoteEvents events) {
		int L = extractL(events);

		// 各拍区間のMIDIノートイベントを抽出する
		NoteEvents[] eventsInSubSection = new NoteEvents[L];
		for(int l = 1; l <= L; l++) {
			eventsInSubSection[l - 1] = events.extractNoteEventsInSection(l);
		}

		// 発音時刻と音高が同じ音符数を抽出する
		int[][] P = new int[L][L];
		for(int m = 1; m <= L; m++) {
			for(int n = 1; n <= L; n++) {
				NoteEvents eventsM = eventsInSubSection[m - 1];
				NoteEvents eventsN = eventsInSubSection[n - 1];
				P[m - 1][n - 1] = extractCountOfSameAttackAndPitchDiff(eventsM, eventsN, events);
			}
		}
		return P;
	}

	// 与えられたMIDIノートイベント群間で, 発音時刻が一致するMIDIノートイベント数を抽出する
	private static int extractCountOfSameAttack(NoteEvents eventsA, NoteEvents eventsB) {
		int count = 0;
		for(NoteEvent eventA : eventsA) {
			for(NoteEvent eventB : eventsB) {
				int noteOnTickA = eventA.getNoteOnTickInT();
				int noteOnTickB = eventB.getNoteOnTickInT();
				if(noteOnTickA == noteOnTickB) {
					count++;
				}
			}
		}
		return count;
	}

	// 与えられたMIDIノートイベント群間で, 発音時刻と音高差が一致するMIDIノートイベント数を抽出する
	private static int extractCountOfSameAttackAndPitchDiff(NoteEvents eventsA, NoteEvents eventsB, NoteEvents events) {
		int count = 0;
		for(NoteEvent eventA : eventsA) {
			for(NoteEvent eventB : eventsB) {
				int noteOnTickA = eventA.getNoteOnTickInT();
				int noteOnTickB = eventB.getNoteOnTickInT();
				if(noteOnTickA == noteOnTickB) {
					// AとBについて前の音符を探す
					NoteEvent prevA = null, prevB = null;
					if(events.indexOf(eventA) > 0) prevA = events.get(events.indexOf(eventA) - 1);
					if(events.indexOf(eventB) > 0) prevB = events.get(events.indexOf(eventB) - 1);

					// 隣接する音符との音高差を計算
					int diffA = -1, diffB = -1;
					if(prevA != null) diffA = Math.abs(eventA.getNoteNumber() - prevA.getNoteNumber());
					if(prevB != null) diffB = Math.abs(eventB.getNoteNumber() - prevB.getNoteNumber());

					// 隣接する音符間で一致する音高差の個数を数える
					if(Math.abs(diffA - diffB) == 0) {
						count++;
					}
				}
			}
		}
		return count;
	}
}
