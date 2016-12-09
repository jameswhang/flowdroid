package soot.jimple.infoflow.android.nu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import soot.SootMethod;
import soot.Value;
import soot.jimple.infoflow.android.resources.ARSCFileParser;

public class NonConstantMethodAnalyzer {
	// Class defining methods for analyzing non-constant methods 

	private LayoutFileParserForTextExtraction lfp;

	private String[] knownMethods = {
			"getIdentifier",
	};
	
	public NonConstantMethodAnalyzer(LayoutFileParserForTextExtraction lfpTE) {
		this.lfp = lfpTE;
	}
	
	public HashSet<String> getAllNonConstantMethods() {
		return new HashSet<String>(Arrays.asList(this.knownMethods)); 
	}
	
	public ConstantDefResult getIdentifierAnalyze(SootMethod m, ArrayList<Value> params) {
		System.out.println("Found a getIdentifier method invocation with params: " + params);
		System.out.println("Resource ID: " + this.lfp.findResourceIDByName(params.get(0).toString().replaceAll("\"", "")) + " for name: " + params.get(0));
		return new ConstantDefResult(null, false);
	}
	
	public ConstantDefResult analyze(SootMethod m, ArrayList<Value> params) {
		if (m.getName().equals("getIdentifier")) {
			return getIdentifierAnalyze(m, params);
		}
		return new ConstantDefResult(null, false);
	}
}
