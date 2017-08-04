package engine;

public class NoteVariable {
	private int tau; // 発音時刻
	private int epsilon; // 消音時刻
	private int f; // 音高

	public NoteVariable(int tau, int epsilon, int f) {
		this.tau = tau;
		this.epsilon = epsilon;
		this.f = f;
	}

	public int getTau() {
		return tau;
	}

	public int getEpsilon() {
		return epsilon;
	}

	public int getF() {
		return f;
	}
}
