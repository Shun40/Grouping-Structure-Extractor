package engine;

import java.util.ArrayList;

public class NoteVariables extends ArrayList<NoteVariable> {
	public NoteVariables() {
		super();
	}

	public void showVariables() {
		for(NoteVariable noteVariable : this) {
			int tau = noteVariable.getTau();
			int epsilon = noteVariable.getEpsilon();
			int f = noteVariable.getF();
			System.out.println(tau + " " + epsilon + " " + f);
		}
	}
}
