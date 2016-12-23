package soot.jimple.infoflow.android.nu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.Orderer;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.graph.UnitGraph;

public class MethodAnalyzer {
	private SootMethod method;
	private ArrayList<Value> params;
	private ReturnStmt retStmt;
	private List<Unit> unitsReverse;
	private List<Unit> units;
	private HashMap<Value, InvokeExpr> localInvokeDefs; // map of local variable => method definition within this method
	private HashMap<Value, Value> localAssignDefs; // map of local variable => method definition within this method
	
	public MethodAnalyzer(SootMethod m, ArrayList<Value> params) {
		this.method = m;
		this.params = params;
		
		if (m.hasActiveBody()) {
			analyzeMethod();
		}
	}
	
	private void analyzeMethod() {
		UnitGraph g = new ExceptionalUnitGraph(this.method.getActiveBody());
		Orderer<Unit> orderer = new PseudoTopologicalOrderer<Unit>();
		this.unitsReverse = orderer.newList(g, true);
		this.units = orderer.newList(g, false);
		
		for (Unit u : units) {
			Stmt s = (Stmt)u;
			if (s instanceof ReturnStmt) {
				retStmt = (ReturnStmt)s;
			} else if (s instanceof AssignStmt) {
				AssignStmt as = (AssignStmt)s;
				localAssignDefs.put(as.getLeftOp(), as.getRightOp());
			} else if (s.containsInvokeExpr()) {
				List<ValueBox> defs = s.getDefBoxes();
				for (ValueBox defbox : defs) {
					localInvokeDefs.put(defbox.getValue(), s.getInvokeExpr());
				}
			}
		}
	}
	
	public int getParamNumber(Value v) {
		/*
		 * Given a Value, check if it is a parameter reference, and if so, return the parameter number. 
		 * ex) @parameter2 => 2 
		 * If not a parameter reference, returns -1
		 * @param Value v : Retrieve by s.getRightOp(); from an IdentityStmt
		 */
		if (v.toString().contains("@parameter")) {
			int sIndex = v.toString().indexOf("@parameter") + "@parameter".length();
			int eIndex = v.toString().indexOf(":");
			return Integer.parseInt(v.toString().substring(sIndex, eIndex));
		} else {
			return -1;
		}
	}
	
	private HashMap<Value, Value> getDefinitions(Unit u) {
		/*
		 * Finds all definition stmts such as 
		 * $r0 := @parameter0 : java.lang.String
		 * @param Unit u : If not null, find all units that happen before the definition of this unit
		 */
		HashMap<Value, Value> definitions = new HashMap<Value, Value>();
		boolean uStmtFound = false;
		if (u == null) {
			uStmtFound = true;
		}
		for (Unit unit : unitsReverse) {
			Stmt s = (Stmt)unit; 
			if (uStmtFound && s instanceof DefinitionStmt) {
				DefinitionStmt ds = (DefinitionStmt)s;
				//System.out.println("Found definition: " + ds.getLeftOp() + " := " + ds.getRightOp());
				definitions.put(ds.getLeftOp(), ds.getRightOp());
			} else if (!uStmtFound && unit.equals(u)) {
				uStmtFound = true;
			}
		}
		return definitions;
	}
	public ConstantDefResult hasConstantReturn() {
		/*
		 * If 
		 */
		if (!this.method.hasActiveBody()) {
			return new ConstantDefResult(-1, false);
		}
		Value returnVal = this.retStmt.getOp();
		
		if (this.localInvokeDefs.containsKey(returnVal)) {
			InvokeExpr ie = localInvokeDefs.get(returnVal);
			System.out.println("[NUTEXT] Returns: " + returnVal.toString() + " which invokes: " + ie.toString() + ", with parameters of: " + ie.getArgs().toString());
			
		}
		
		return new ConstantDefResult(-1, false);
	}
	
	
	

}
