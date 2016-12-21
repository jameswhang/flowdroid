package soot.jimple.infoflow.android.nu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.resources.ARSCFileParser;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.Orderer;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.graph.UnitGraph;
import soot.util.MultiMap;

public class TraceParamSource {
	
	private StaticValueService staticValueService;
	private ConstantDefResult cdr;
	
	// Iterate through a Callgraph to find potential methods 
	public List<SootMethod> traceNonConstantParam(CallGraph cgraph, SootMethod sourceMethod, SootMethod sinkMethod) {

		List<SootMethod> orginMethods = new ArrayList<SootMethod>();
		Set<String> visitedNodes = new HashSet<String>();
		LinkedList<SootMethod> queue = new LinkedList<SootMethod>();
		List<Value> retValuesOfMethod = new ArrayList<Value>();
		
		queue.add(sinkMethod);
		while(!queue.isEmpty()) {
			SootMethod m = queue.removeFirst();
			if (m.getSignature() == sourceMethod.getSignature()) {
				break;
			}
			retValuesOfMethod = getRetValues(m);
			if(originFound(retValuesOfMethod,cdr)){
				orginMethods.add(m);
			}
			visitedNodes.add(m.getSignature());
			Iterator<Edge> edges = cgraph.edgesInto(m);
			while(edges.hasNext()) {
				Edge e = edges.next();
				SootMethod pred = e.getSrc().method();
				if (pred != null) {
					if (!visitedNodes.contains(pred.getSignature())) {
						queue.addLast(pred);
					}
				}
			}
		}
		return orginMethods;			
	}
	
	//Given a Sootmethod on the callgraph, retrieve all its return values and save in a list
	public List<Value> getRetValues(SootMethod m){
		
		UnitGraph g = new ExceptionalUnitGraph(m.getActiveBody());
		Orderer<Unit> orderer = new PseudoTopologicalOrderer<Unit>();
		List<Unit> units = orderer.newList(g, true);
		
		List<Value> retVals = new ArrayList<Value>();
		
		for (Unit unit : units) {
			Stmt s = (Stmt)unit;
			if (s instanceof ReturnStmt) {
				ReturnStmt retStmt = (ReturnStmt)s;
				Value returnVal = retStmt.getOp();
				retVals.add(returnVal);
			}
		}
			
		return retVals;
	}
	
	//Given return values of a Sootmethod, compare it with the ID we got and see if they matches
	public boolean originFound(List<Value> retVal, ConstantDefResult cdr){
		boolean found = false;
		for(Value ret : retVal){
			if (ret.equals(cdr.id)){
				found = true;
				break;
			}
		}
		return found;
	}
	
}
