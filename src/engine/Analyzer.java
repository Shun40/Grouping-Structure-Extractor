package engine;

import java.util.ArrayList;

import MIDI.MidiNoteEvent;
import MIDI.MidiNoteEvents;
import MIDI.MidiUtil;

public class Analyzer {
	public Analyzer() {
	}

	public ArrayList<Integer> extractGroupingStructure(String midiFilePath, double S2a, double S2b, double S3a, double S3d, double S6, double Wm, double Wl, double Ws, double TLow) {
		MidiNoteEvents midiNoteEvents = MidiUtil.extractMidiNoteEvents(midiFilePath);

		NoteVariables noteVariables = extractNoteVariables(midiNoteEvents);
		BasicVariables basicVariables = extractBasicVariables(noteVariables);

		ArrayList<Double> D2a = extractGPR2a(basicVariables);
		ArrayList<Double> D2b = extractGPR2b(basicVariables);
		ArrayList<Double> D3a = extractGPR3a(basicVariables);
		ArrayList<Double> D3d = extractGPR3d(basicVariables);
		ArrayList<Double> D6 = extractGPR6(midiNoteEvents, Wm, Wl, Ws);

		ArrayList<ArrayList<Double>> DR = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < midiNoteEvents.size(); i++) {
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

	public NoteVariables extractNoteVariables(MidiNoteEvents midiNoteEvents) {
		NoteVariables noteVariables = new NoteVariables();

		for(int i = 0; i < midiNoteEvents.size(); i++) {
			int tau = (int)midiNoteEvents.get(i).getNoteOnTickInSTGT();
			int epsilon = (int)midiNoteEvents.get(i).getNoteOffTickInSTGT();
			int f = midiNoteEvents.get(i).getNoteNumber();
			noteVariables.add(new NoteVariable(tau, epsilon, f));
		}

		return noteVariables;
	}

	public BasicVariables extractBasicVariables(NoteVariables noteVariables) {
		BasicVariables basicVariables = new BasicVariables();

		ArrayList<Integer> rhos = new ArrayList<Integer>();
		ArrayList<Integer> iotas = new ArrayList<Integer>();
		ArrayList<Integer> etas = new ArrayList<Integer>();
		ArrayList<Integer> betas = new ArrayList<Integer>();

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
			if(i == 0 || i == basicVariables.size() - 1) { // 始端と終端はとりあえず境界になり得ないものとする
				D2a.add(0.0);
			}
			else {
				temp1 = basicVariables.get(i - 1).getRho();
				temp2 = basicVariables.get(i).getRho();
				temp3 = basicVariables.get(i + 1).getRho();
				if(temp1 < temp2 && temp2 > temp3) {
					D2a.add(1.0);
				}
				else {
					D2a.add(0.0);
				}
			}
		}
		//for(int i = 0; i < D2a.size(); i++) System.out.println("D2a[" + i + "]: " + D2a.get(i));

		return D2a;
	}

	public ArrayList<Double> extractGPR2b(BasicVariables basicVariables) {
		ArrayList<Double> D2b = new ArrayList<Double>();

		int temp1, temp2, temp3; // iota_i-1, iota_i, iota_i+1
		for(int i = 0; i < basicVariables.size(); i++) {
			if(i == 0 || i == basicVariables.size() - 1) { // 始端と終端はとりあえず境界になり得ないものとする
				D2b.add(0.0);
			}
			else {
				temp1 = basicVariables.get(i - 1).getIota();
				temp2 = basicVariables.get(i).getIota();
				temp3 = basicVariables.get(i + 1).getIota();
				if(temp1 < temp2 && temp2 > temp3) {
					D2b.add(1.0);
				}
				else {
					D2b.add(0.0);
				}
			}
		}
		//for(int i = 0; i < D2b.size(); i++) System.out.println("D2b[" + i + "]: " + D2b.get(i));

		return D2b;
	}

	public ArrayList<Double> extractGPR3a(BasicVariables basicVariables) {
		ArrayList<Double> D3a = new ArrayList<Double>();

		int temp1, temp2, temp3; // |eta_i-1|, |eta_i|, |eta_i+1|
		for(int i = 0; i < basicVariables.size(); i++) {
			if(i == 0 || i == basicVariables.size() - 1) { // 始端と終端はとりあえず境界になり得ないものとする
				D3a.add(0.0);
			}
			else {
				temp1 = Math.abs(basicVariables.get(i - 1).getEta());
				temp2 = Math.abs(basicVariables.get(i).getEta());
				temp3 = Math.abs(basicVariables.get(i + 1).getEta());
				if(temp1 < temp2 && temp2 > temp3) {
					D3a.add(1.0);
				}
				else {
					D3a.add(0.0);
				}
			}
		}
		//for(int i = 0; i < D3a.size(); i++) System.out.println("D3a[" + i + "]: " + D3a.get(i));

		return D3a;
	}

	public ArrayList<Double> extractGPR3d(BasicVariables basicVariables) {
		ArrayList<Double> D3d = new ArrayList<Double>();

		int temp1, temp2, temp3; // beta_i-1, beta_i, beta_i+1
		for(int i = 0; i < basicVariables.size(); i++) {
			if(i == 0 || i == basicVariables.size() - 1) { // 始端と終端はとりあえず境界になり得ないものとする
				D3d.add(0.0);
			}
			else {
				temp1 = basicVariables.get(i - 1).getBeta();
				temp2 = basicVariables.get(i).getBeta();
				temp3 = basicVariables.get(i + 1).getBeta();
				if(temp1 == 0 && temp2 != 0 && temp3 == 0) {
					D3d.add(1.0);
				}
				else {
					D3d.add(0.0);
				}
			}
		}
		//for(int i = 0; i < D3d.size(); i++) System.out.println("D3d[" + i + "]: " + D3d.get(i));

		return D3d;
	}

	public ArrayList<Double> extractGPR6(MidiNoteEvents midiNoteEvents, double Wm, double Wl, double Ws) {
		ArrayList<Double> D6 = new ArrayList<Double>();

		int R = 16; // 最大R拍分(1拍 = 4分音符)の区間の並列度をチェック

		MusicalInformation musicalInformation = new MusicalInformation(midiNoteEvents);

		int L = musicalInformation.getL();

		// N(m, r)の計算
		int[][] Nmr = new int[L][R];
		ArrayList<Integer> Nm = musicalInformation.getNm();
		for(int m = 0; m < L; m++) {
			for(int r = 0; r < R; r++) {
				Nmr[m][r] = 0;
				for(int j = 0; j <= r; j++) {
					if(m + j < L) {
						Nmr[m][r] += Nm.get(m + j);
					}
				}
			}
		}

		// O(m, n, r)の計算
		int[][][] Omnr = new int[L][L][R];
		ArrayList<ArrayList<Integer>> Omn = musicalInformation.getOmn();

		for(int m = 0; m < L; m++) {
			for(int n = 0; n < L; n++) {
				for(int r = 0; r < R; r++) {
					Omnr[m][n][r] = 0;
					for(int j = 0; j <= r; j++) {
						if(m + j < L && n + j < L) {
							Omnr[m][n][r] += Omn.get(m + j).get(n + j);
						}
					}
				}
			}
		}

		// P(m, n, r)の計算
		int[][][] Pmnr = new int[L][L][R];
		ArrayList<ArrayList<Integer>> Pmn = musicalInformation.getPmn();
		for(int m = 0; m < L; m++) {
			for(int n = 0; n < L; n++) {
				for(int r = 0; r < R; r++) {
					Pmnr[m][n][r] = 0;
					for(int j = 0; j <= r; j++) {
						if(m + j < L && n + j < L) {
							Pmnr[m][n][r] += Pmn.get(m + j).get(n + j);
						}
					}
				}
			}
		}

		// G(m, n, r)の計算
		double[][][] Gmnr = new double[L][L][R];
		for(int m = 0; m < L; m++) {
			for(int n = 0; n < L; n++) {
				for(int r = 0; r < R; r++) {
					int _Nmr = Nmr[m][r];
					int _Nnr = Nmr[n][r];
					int _Omnr = Omnr[m][n][r];
					int _Pmnr = Pmnr[m][n][r];
					double temp1 = 0;
					double temp2 = 0;
					if(_Nmr + _Nnr > 0) temp1 = _Omnr / (_Nmr + _Nnr); // 0除算エラー防止
					if(_Omnr > 0)       temp2 = _Pmnr / _Omnr;         // 0除算エラー防止
					Gmnr[m][n][r] = ((temp1 * (1 - Wm)) + (temp2 * Wm)) * Math.pow(r, Wl);
				}
			}
		}

		// b(i), e(i), t(i)の計算
		boolean[] b = new boolean[midiNoteEvents.size()];
		boolean[] e = new boolean[midiNoteEvents.size()];
		boolean[] t = new boolean[midiNoteEvents.size()];
		for(int i = 0; i < midiNoteEvents.size(); i++) {
			MidiNoteEvent note = midiNoteEvents.get(i);
			int beat = midiNoteEvents.beat(note);
			MidiNoteEvent head = midiNoteEvents.head(beat);
			MidiNoteEvent tail = midiNoteEvents.tail(beat);
			b[i] = (note == head && note != tail);
			e[i] = (note != head && note == tail);
			t[i] = (note == head && note == tail);
		}

		// A(i)の計算
		ArrayList<Double> A = new ArrayList<Double>();
		for(int i = 0; i < midiNoteEvents.size(); i++) {
			MidiNoteEvent note = midiNoteEvents.get(i);
			int beat = midiNoteEvents.beat(note) - 1;
			double sum = 0;
			for(int n = 0; n < L; n++) {
				int Nn = Nm.get(n);
				//for(int r = 0; r < L / 2; r++) {
				for(int r = 0; r < R; r++) {
					if(b[i] && Nn > 0) {
						sum += Gmnr[beat][n][r] * (1 - Ws);
					}
					else if(e[i] && Nn > 0) {
						if(beat - r > 0 && n - r > 0) {
							sum += Gmnr[beat - r][n - r][r] * Ws;
						} else {
							sum += 0;
						}
					}
					else if(t[i] && Nn > 0) {
						if(beat - r > 0 && n - r > 0) {
							sum += ((Gmnr[beat][n][r] * (1 - Ws)) + (Gmnr[beat - r][n - r][r] * Ws));
						} else {
							sum += Gmnr[beat][n][r] * (1 - Ws);
						}
					}
					else {
						sum += 0;
					}
				}
			}
			A.add(sum);
		}

		// A(i)の正規化
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
		//for(int i = 0; i < BLow.size(); i++) System.out.println("BLow[" + i + "]: " + BLow.get(i));

		return BLow;
	}

	public ArrayList<Integer> extractGPR1(ArrayList<Double> BLow) {
		ArrayList<Integer> D1 = new ArrayList<Integer>();

		double Blow1, Blow2, Blow3; // Blow_i-1, Blow_i, Blow_i+1
		for(int i = 0; i < BLow.size(); i++) {
			if(i == 0 || i == BLow.size() - 1) { // 始端と終端はとりあえず境界になり得ないものとする
				D1.add(0);
			}
			else {
				Blow1 = BLow.get(i - 1);
				Blow2 = BLow.get(i);
				Blow3 = BLow.get(i + 1);
				if(Blow1 <= Blow2 && Blow2 >= Blow3 && D1.get(i - 1) == 0) {
					D1.add(1);
				}
				else {
					D1.add(0);
				}
			}
		}
		//for(int i = 0; i < D1.size(); i++) System.out.println("D1[" + i + "]: " + D1.get(i));

		return D1;
	}

	public ArrayList<Integer> extractDLow(ArrayList<Double> BLow, ArrayList<Integer> D1, double TLow) {
		ArrayList<Integer> DLow = new ArrayList<Integer>();

		for(int i = 0; i < BLow.size(); i++) {
			if(i == 0 || i == BLow.size() - 1) { // 始端と終端はとりあえず境界になり得ないものとする
				DLow.add(0);
			}
			else {
				if(BLow.get(i) > TLow && D1.get(i) == 1) {
					DLow.add(1);
				}
				else {
					DLow.add(0);
				}
			}
		}
		//for(int i = 0; i < DLow.size(); i++) System.out.println("DLow[" + i + "]: " + DLow.get(i));

		return DLow;
	}
}
