package engine;

import java.util.ArrayList;

import MIDI.MidiUtil;
import MIDI.NoteEvent;
import MIDI.NoteEvents;

public class Analyzer {
	public Analyzer() {
	}

	public ArrayList<Integer> extractGroupingStructure(String midiFilePath, double S2a, double S2b, double S3a, double S3d, double S6, double Wm, double Wl, double Ws, double TLow) {
		NoteEvents events = MidiUtil.extractNoteEvents(midiFilePath);
		NoteVariables noteVariables = extractNoteVariables(events);
		BasicVariables basicVariables = extractBasicVariables(noteVariables);

		ArrayList<Double> D2a = extractGPR2a(basicVariables);
		ArrayList<Double> D2b = extractGPR2b(basicVariables);
		ArrayList<Double> D3a = extractGPR3a(basicVariables);
		ArrayList<Double> D3d = extractGPR3d(basicVariables);
		ArrayList<Double> D6 = extractGPR6(events, Wm, Wl, Ws, 4);

		ArrayList<ArrayList<Double>> DR = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < events.size(); i++) {
			ArrayList<Double> DRi = new ArrayList<Double>();
			DRi.add(D2a.get(i));
			DRi.add(D2b.get(i));
			DRi.add(D3a.get(i));
			DRi.add(D3d.get(i));
			DRi.add(D6.get(i));
			DR.add(DRi);
		}
		ArrayList<Double> SR = new ArrayList<Double>();
		SR.add(S2a); // D2aルールの強さ
		SR.add(S2b); // D2bルールの強さ
		SR.add(S3a); // D3aルールの強さ
		SR.add(S3d); // D3dルールの強さ
		SR.add(S6);  // D6ルールの強さ

		ArrayList<Double> BLow = extractBLow(DR, SR);

		ArrayList<Integer> D1 = extractGPR1(BLow);
		return extractDLow(BLow, D1, TLow);
	}

	public NoteVariables extractNoteVariables(NoteEvents events) {
		NoteVariables noteVariables = new NoteVariables();
		for(int i = 0; i < events.size(); i++) {
			int tau = (int)events.get(i).getNoteOnTickInSTGT();
			int epsilon = (int)events.get(i).getNoteOffTickInSTGT();
			int f = events.get(i).getNoteNumber();
			noteVariables.add(new NoteVariable(tau, epsilon, f));
		}
		return noteVariables;
	}

	public BasicVariables extractBasicVariables(NoteVariables noteVariables) {
		BasicVariables basicVariables = new BasicVariables();

		ArrayList<Integer> rhos = new ArrayList<Integer>();
		for(int i = 0; i < noteVariables.size(); i++) {
			int rho;
			if(i == noteVariables.size() - 1) {
				rho = 0;
			}
			else {
				rho = noteVariables.get(i + 1).getTau() - noteVariables.get(i).getEpsilon();
				if(rho < 0) rho = 0;
			}
			rhos.add(rho);
		}

		ArrayList<Integer> iotas = new ArrayList<Integer>();
		for(int i = 0; i < noteVariables.size(); i++) {
			int iota;
			if(i == noteVariables.size() - 1) {
				iota = noteVariables.get(i).getEpsilon() - noteVariables.get(i).getTau();
			}
			else {
				iota = noteVariables.get(i + 1).getTau() - noteVariables.get(i).getTau();
			}
			iotas.add(iota);
		}

		ArrayList<Integer> etas = new ArrayList<Integer>();
		for(int i = 0; i < noteVariables.size(); i++) {
			int eta;
			if(i == noteVariables.size() - 1) {
				eta = 0;
			}
			else {
				eta = noteVariables.get(i + 1).getF() - noteVariables.get(i).getF();
			}
			etas.add(eta);
		}

		ArrayList<Integer> betas = new ArrayList<Integer>();
		for(int i = 0; i < noteVariables.size(); i++) {
			int beta;
			int temp1; // iota_i+1
			int temp2; // iota_i
			if(i == noteVariables.size() - 1) {
				beta = 0;
			}
			else if(i == noteVariables.size() - 2) {
				temp1 = noteVariables.get(i + 1).getEpsilon() - noteVariables.get(i + 1).getTau();
				temp2 = noteVariables.get(i + 1).getTau() - noteVariables.get(i).getTau();
				beta = Math.abs(temp1 - temp2);
			}
			else {
				temp1 = noteVariables.get(i + 2).getTau() - noteVariables.get(i + 1).getTau();
				temp2 = noteVariables.get(i + 1).getTau() - noteVariables.get(i).getTau();
				beta = Math.abs(temp1 - temp2);
			}
			betas.add(beta);
		}

		for(int i = 0; i < noteVariables.size(); i++) {
			int rho = rhos.get(i);
			int iota = iotas.get(i);
			int eta = etas.get(i);
			int beta = betas.get(i);
			basicVariables.add(new BasicVariable(rho, iota, eta, beta));
		}
		return basicVariables;
	}

	public ArrayList<Double> extractGPR2a(BasicVariables basicVariables) {
		ArrayList<Double> D2a = new ArrayList<Double>();

		int temp1, temp2, temp3; // rho_i-1, rho_i, rho_i+1
		for(int i = 0; i < basicVariables.size(); i++) {
			if(i == 0) { // 始端の処理
				temp2 = basicVariables.get(i).getRho();
				temp3 = basicVariables.get(i + 1).getRho();
				if(temp2 > temp3) D2a.add(1.0);
				else              D2a.add(0.0);
			}
			else if(i == basicVariables.size() - 1) { // 終端の処理
				temp1 = basicVariables.get(i - 1).getRho();
				temp2 = basicVariables.get(i).getRho();
				if(temp1 < temp2) D2a.add(1.0);
				else              D2a.add(0.0);
			}
			else {
				temp1 = basicVariables.get(i - 1).getRho();
				temp2 = basicVariables.get(i).getRho();
				temp3 = basicVariables.get(i + 1).getRho();
				if(temp1 < temp2 && temp2 > temp3) D2a.add(1.0);
				else                               D2a.add(0.0);
			}
		}
		return D2a;
	}

	public ArrayList<Double> extractGPR2b(BasicVariables basicVariables) {
		ArrayList<Double> D2b = new ArrayList<Double>();

		int temp1, temp2, temp3; // iota_i-1, iota_i, iota_i+1
		for(int i = 0; i < basicVariables.size(); i++) {
			if(i == 0) { // 始端の処理
				temp2 = basicVariables.get(i).getIota();
				temp3 = basicVariables.get(i + 1).getIota();
				if(temp2 > temp3) D2b.add(1.0);
				else              D2b.add(0.0);
			}
			else if(i == basicVariables.size() - 1) { // 終端の処理
				temp1 = basicVariables.get(i - 1).getIota();
				temp2 = basicVariables.get(i).getIota();
				if(temp1 < temp2) D2b.add(1.0);
				else              D2b.add(0.0);
			}
			else {
				temp1 = basicVariables.get(i - 1).getIota();
				temp2 = basicVariables.get(i).getIota();
				temp3 = basicVariables.get(i + 1).getIota();
				if(temp1 < temp2 && temp2 > temp3) D2b.add(1.0);
				else                               D2b.add(0.0);
			}
		}
		return D2b;
	}

	public ArrayList<Double> extractGPR3a(BasicVariables basicVariables) {
		ArrayList<Double> D3a = new ArrayList<Double>();

		int temp1, temp2, temp3; // |eta_i-1|, |eta_i|, |eta_i+1|
		for(int i = 0; i < basicVariables.size(); i++) {
			if(i == 0) { // 始端の処理
				temp2 = Math.abs(basicVariables.get(i).getEta());
				temp3 = Math.abs(basicVariables.get(i + 1).getEta());
				if(temp2 > temp3) D3a.add(1.0);
				else              D3a.add(0.0);
			}
			else if(i == basicVariables.size() - 1) { // 終端の処理
				temp1 = Math.abs(basicVariables.get(i - 1).getEta());
				temp2 = Math.abs(basicVariables.get(i).getEta());
				if(temp1 < temp2) D3a.add(1.0);
				else              D3a.add(0.0);
			}
			else {
				temp1 = Math.abs(basicVariables.get(i - 1).getEta());
				temp2 = Math.abs(basicVariables.get(i).getEta());
				temp3 = Math.abs(basicVariables.get(i + 1).getEta());
				if(temp1 < temp2 && temp2 > temp3) D3a.add(1.0);
				else                               D3a.add(0.0);
			}
		}
		return D3a;
	}

	public ArrayList<Double> extractGPR3d(BasicVariables basicVariables) {
		ArrayList<Double> D3d = new ArrayList<Double>();

		int temp1, temp2, temp3; // beta_i-1, beta_i, beta_i+1
		for(int i = 0; i < basicVariables.size(); i++) {
			if(i == 0) { // 始端の処理
				temp2 = basicVariables.get(i).getBeta();
				temp3 = basicVariables.get(i + 1).getBeta();
				if(temp2 != 0 && temp3 == 0) D3d.add(1.0);
				else                         D3d.add(0.0);
			}
			else if(i == basicVariables.size() - 1) { // 終端の処理
				temp1 = basicVariables.get(i - 1).getBeta();
				temp2 = basicVariables.get(i).getBeta();
				if(temp1 == 0 && temp2 != 0) D3d.add(1.0);
				else                         D3d.add(0.0);
			}
			else {
				temp1 = basicVariables.get(i - 1).getBeta();
				temp2 = basicVariables.get(i).getBeta();
				temp3 = basicVariables.get(i + 1).getBeta();
				if(temp1 == 0 && temp2 != 0 && temp3 == 0) D3d.add(1.0);
				else                                       D3d.add(0.0);
			}
		}
		return D3d;
	}

	public ArrayList<Double> extractGPR6(NoteEvents events, double Wm, double Wl, double Ws, int R) {
		ArrayList<Double> D6 = new ArrayList<Double>();

		// Lの計算
		int L = GPR6Util.extractL(events);

		// Nの計算
		int[] _N = GPR6Util.extractN(events);
		int[][] N = new int[L][R];
		for(int m = 1; m <= L; m++) {
			for(int r = 1; r <= R; r++) {
				N[m - 1][r - 1] = 0;
				for(int j = 0; j <= r - 1; j++) {
					if(m + j <= L) {
						N[m - 1][r - 1] += _N[m + j - 1];
					}
				}
			}
		}

		// Oの計算
		int[][] _O = GPR6Util.extractO(events);
		int[][][] O = new int[L][L][R];
		for(int m = 1; m <= L; m++) {
			for(int n = 1; n <= L; n++) {
				for(int r = 1; r <= R; r++) {
					O[m - 1][n - 1][r - 1] = 0;
					for(int j = 0; j <= r - 1; j++) {
						if(m + j <= L && n + j <= L) {
							O[m - 1][n - 1][r - 1] += _O[m + j - 1][n + j - 1];
						}
					}
				}
			}
		}

		// Pの計算
		int[][] _P = GPR6Util.extractP(events);
		int[][][] P = new int[L][L][R];
		for(int m = 1; m <= L; m++) {
			for(int n = 1; n <= L; n++) {
				for(int r = 1; r <= R; r++) {
					P[m - 1][n - 1][r - 1] = 0;
					for(int j = 0; j <= r - 1; j++) {
						if(m + j <= L && n + j <= L) {
							P[m - 1][n - 1][r - 1] += _P[m + j - 1][n + j - 1];
						}
					}
				}
			}
		}

		// Gの計算
		double[][][] G = new double[L][L][R];
		for(int m = 1; m <= L; m++) {
			for(int n = 1; n <= L; n++) {
				for(int r = 1; r <= R; r++) {
					// Gの定義域に収まっているかチェック
					if(1 <= m && n <= L - r + 1 && (1 <= r && r <= L)) {
						double temp1 = 0.0, temp2 = 0.0;
						if(N[m - 1][r - 1] + N[n - 1][r - 1] > 0.0) { // 0除算エラー防止
							temp1 = (double)O[m - 1][n - 1][r - 1] / (double)(N[m - 1][r - 1] + N[n - 1][r - 1]);
						}
						if(O[m - 1][n - 1][r - 1] > 0.0) { // 0除算エラー防止
							temp2 = (double)P[m - 1][n - 1][r - 1] / (double)O[m - 1][n - 1][r - 1];
						}
						G[m - 1][n - 1][r - 1] = ((temp1 * (1.0 - Wm)) + (temp2 * Wm)) * Math.pow(r, Wl);
					}
					else {
						G[m - 1][n - 1][r - 1] = 0;
					}
				}
			}
		}

		// b, e, tの計算
		boolean[] b = new boolean[events.size()];
		boolean[] e = new boolean[events.size()];
		boolean[] t = new boolean[events.size()];
		for(int i = 0; i < events.size(); i++) {
			NoteEvent note = events.get(i);
			int beat = events.beat(note);
			NoteEvent head = events.head(beat);
			NoteEvent tail = events.tail(beat);
			b[i] = (note == head && note != tail);
			e[i] = (note != head && note == tail);
			t[i] = (note == head && note == tail);
		}

		// Aの計算
		ArrayList<Double> A = new ArrayList<Double>();
		for(int i = 0; i < events.size(); i++) {
			NoteEvent note = events.get(i);
			int beat = events.beat(note);
			double sum = 0;
			for(int n = 1; n <= L; n++) {
				for(int r = 1; r <= R; r++) {
					if(r > L / 2) break; // rがL/2の範囲に収まっているかチェック
					if(b[i] && _N[n - 1] > 0) {
						sum += G[beat - 1][n - 1][r - 1] * (1.0 - Ws);
					}
					else if(e[i] && _N[n - 1] > 0) {
						if(beat - r > 0 && n - r > 0) {
							sum += G[beat - r - 1][n - r - 1][r - 1] * Ws;
						} else {
							sum += 0;
						}
					}
					else if(t[i] && _N[n - 1] > 0) {
						if(beat - r > 0 && n - r > 0) {
							sum += ((G[beat - 1][n - 1][r - 1] * (1.0 - Ws)) + (G[beat - r - 1][n - r - 1][r - 1] * Ws));
						} else {
							sum += G[beat - 1][n - 1][r - 1] * (1.0 - Ws);
						}
					}
					else {
						sum += 0.0;
					}
				}
			}
			A.add(sum);
		}

		// Aの正規化
		double Amax = 0;
		for(double a : A) {
			if(Amax <= a) Amax = a;
		}
		for(int i = 0; i < A.size(); i++) {
			D6.add(A.get(i) / Amax);
		}

		return D6;
	}

	public ArrayList<Double> extractBLow(ArrayList<ArrayList<Double>> DR, ArrayList<Double> SR) {
		ArrayList<Double> BLow = new ArrayList<Double>();

		double max = 0.0;
		for(int i = 0; i < DR.size(); i++) {
			double sigma = 0.0;
			for(int r = 0; r < DR.get(i).size(); r++) {
				sigma += DR.get(i).get(r) * SR.get(r);
			}
			if(max < sigma) max = sigma;
		}

		for(int i = 0; i < DR.size(); i++) {
			double sigma = 0.0;
			for(int r = 0; r < DR.get(i).size(); r++) {
				sigma += DR.get(i).get(r) * SR.get(r);
			}
			BLow.add(sigma / max);
		}
		return BLow;
	}

	public ArrayList<Integer> extractGPR1(ArrayList<Double> BLow) {
		ArrayList<Integer> D1 = new ArrayList<Integer>();

		double Blow1, Blow2, Blow3; // Blow_i-1, Blow_i, Blow_i+1
		for(int i = 0; i < BLow.size(); i++) {
			if(i == 0) { // 始端の処理
				D1.add(0); // この時点ではD1(i-1)を使った計算が出来ないため, とりあえず始端は0にしておく
			}
			else if(i == BLow.size() - 1) { // 終端の処理
				Blow1 = BLow.get(i - 1);
				Blow2 = BLow.get(i);
				if(Blow1 <= Blow2 && D1.get(i - 1) == 0) D1.add(1);
				else                                     D1.add(0);
			}
			else {
				Blow1 = BLow.get(i - 1);
				Blow2 = BLow.get(i);
				Blow3 = BLow.get(i + 1);
				if(Blow1 <= Blow2 && Blow2 >= Blow3 && D1.get(i - 1) == 0) D1.add(1);
				else                                                       D1.add(0);
			}
		}
		return D1;
	}

	public ArrayList<Integer> extractDLow(ArrayList<Double> BLow, ArrayList<Integer> D1, double TLow) {
		ArrayList<Integer> DLow = new ArrayList<Integer>();

		for(int i = 0; i < BLow.size(); i++) {
			if(BLow.get(i) > TLow && D1.get(i) == 1) DLow.add(1);
			else                                     DLow.add(0);
		}
		return DLow;
	}
}
