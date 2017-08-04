package engine;

public class BasicVariable {
	private int rho; // 消音時刻から次の発音時刻までの間隔
	private int iota; // 発音時刻間隔
	private int eta; // 音高の差(音程)
	private int beta; // 音価の差の絶対値

	public BasicVariable(int rho, int iota, int eta, int beta) {
		this.rho = rho;
		this.iota = iota;
		this.eta = eta;
		this.beta = beta;
	}

	public int getRho() {
		return rho;
	}

	public int getIota() {
		return iota;
	}

	public int getEta() {
		return eta;
	}

	public int getBeta() {
		return beta;
	}
}
