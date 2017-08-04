package engine;

import java.util.ArrayList;

public class BasicVariables extends ArrayList<BasicVariable> {
	public BasicVariables() {
		super();
	}

	public void showVariables() {
		for(BasicVariable basicVariable : this) {
			int rho = basicVariable.getRho();
			int iota = basicVariable.getIota();
			int eta = basicVariable.getEta();
			int beta = basicVariable.getBeta();
			System.out.println(rho + " " + iota + " " + eta + " " + beta);
		}
	}
}
