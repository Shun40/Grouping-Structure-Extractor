package MIDI;

public class NoteEvent {
	private long noteOnTick;
	private long noteOffTick;
	private int noteNumber;
	private long length;

	public NoteEvent(long noteOnTick, long noteOffTick, int noteNumber) {
		this.noteOnTick = noteOnTick;
		this.noteOffTick = noteOffTick;
		this.noteNumber = noteNumber;
		this.length = noteOffTick - noteOnTick;
	}

	private String stgtToMbt(long stgtTick) {
		int PPQ = 960; // tick per beat
		long m = (stgtTick / (PPQ * 4)) + 1;
		long b = ((stgtTick % (PPQ * 4)) / PPQ) + 1;
		long t = ((stgtTick % PPQ) / (PPQ / 4)) + 1;
		return m + ":" + b + ":" + t;
	}

	public long getNoteOnTickInSTGT() {
		return noteOnTick;
	}

	public long getNoteOffTickInSTGT() {
		return noteOffTick;
	}

	public String getNoteOnTickInMBT() {
		return stgtToMbt(noteOnTick);
	}

	public int getNoteOnTickInM() {
		return Integer.parseInt(stgtToMbt(noteOnTick).split(":")[0]);
	}

	public int getNoteOnTickInB() {
		return Integer.parseInt(stgtToMbt(noteOnTick).split(":")[1]);
	}

	public int getNoteOnTickInT() {
		return Integer.parseInt(stgtToMbt(noteOnTick).split(":")[2]);
	}

	public String getNoteOffTickInMBT() {
		return stgtToMbt(noteOffTick);
	}

	public int getNoteOffTickInM() {
		return Integer.parseInt(stgtToMbt(noteOffTick).split(":")[0]);
	}

	public int getNoteOffTickInB() {
		return Integer.parseInt(stgtToMbt(noteOffTick).split(":")[1]);
	}

	public int getNoteOffTickInT() {
		return Integer.parseInt(stgtToMbt(noteOffTick).split(":")[2]);
	}

	public int getNoteNumber() {
		return noteNumber;
	}

	public long getLength() {
		return length;
	}
}
