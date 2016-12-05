package soot.jimple.infoflow.android.nu;

import java.util.Arrays;
import java.util.HashSet;

import soot.SootMethod;

public class NonConstantMethodAnalyzer {
	// Class defining methods for analyzing non-constant methods 


	private String[] knownMethods = {
			"getIdentifier",
	};
	
	public NonConstantMethodAnalyzer() {}
	
	public HashSet<String> getAllNonConstantMethods() {
		return new HashSet<String>(Arrays.asList(this.knownMethods)); 
	}
	
	public ConstantDefResult analyze(SootMethod m) {
		if (m.getName().equals("getIdentifier")) {
			System.out.println("YAAAAAAY");
		}
		
		System.out.println("Huh");
		
		return new ConstantDefResult(null, false);
		
	}
}
