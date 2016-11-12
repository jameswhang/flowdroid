package soot.jimple.infoflow.android.nu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.MultiMap;

public class FlowTriggerEventAnalyzer {
	
	private String apkFileLocation;
	private MultiMap<ResultSinkInfo, ResultSourceInfo> infoflowResultMap;
	private InfoflowResults infoflowResult;
	private StaticValueService staticValueService;
	private Set<SootMethod> triggerMethods;
	
	public FlowTriggerEventAnalyzer(InfoflowResults results, String apkFileLocation) {
		this.infoflowResult = results;
		this.infoflowResultMap = results.getResults();
		this.apkFileLocation = apkFileLocation;
		this.staticValueService = new StaticValueService();
		this.triggerMethods = new HashSet<SootMethod>();
	}
	
	public void Analyze() {
		CallGraph cgraph = Scene.v().getCallGraph();
		for (ResultSinkInfo sink : this.infoflowResultMap.keySet()) {
			Set<ResultSourceInfo> sources = this.infoflowResultMap.get(sink);
			for (ResultSourceInfo source : sources) {
				if (source.getSource().containsInvokeExpr()) { // is a call to something
					//System.out.println("[NUTEXT] Looking at source: " + source.getSource().getInvokeExpr().getMethod().getSignature());
					SootMethod invokedMethod = source.getSource().getInvokeExpr().getMethod();
					Iterator<Edge> edges = cgraph.edgesInto(invokedMethod);
					
					if (edges.hasNext()) {
						SootMethod triggerMethodFromSource = findUITriggerMethodFromSource(cgraph, invokedMethod);
						if (triggerMethodFromSource != null) {
							this.triggerMethods.add(triggerMethodFromSource);
							System.out.println("[NUTEXT] Found source trigger: "+source+" tiggered by " + triggerMethodFromSource.getSignature());
						}
						ArrayList<SootMethod> triggerMethodBetweenSourceAndSink = findTriggerMethodsFromSinkToSource(cgraph, invokedMethod, sink.getSink().getInvokeExpr().getMethod());
						for (SootMethod m : triggerMethodBetweenSourceAndSink) {
							this.triggerMethods.add(m);
							System.out.println("[NUTEXT] Found source trigger: "+source+" tiggered by " + m.getSignature());
						}
					}
				}
			}
		}
	}
	
	
	
	public ArrayList<SootMethod> findTriggerMethodsFromSinkToSource(CallGraph cgraph, SootMethod sourceMethod, SootMethod sinkMethod) {
		/*
		 * Given a source and sink methods, tracks the call graph to find any triggering methods in the call flow between the source and sink methods
		 * @param cgraph CallGraph object of the Android apk
		 * @param sourceMethod SootMethod object of the source method
		 * @param sinkMethod SootMethod object of the sink method
		 * @return triggerMethod SootMethod object of the method that triggers
		 */
		ArrayList<SootMethod> triggerMethods = new ArrayList<SootMethod>();
		HashSet<String> UIActionsSet = this.staticValueService.getUIEventActionsSet();
		Set<String> visitedNodes = new HashSet<String>();
		
		LinkedList<SootMethod> queue = new LinkedList<SootMethod>();
		queue.add(sinkMethod);
		while(!queue.isEmpty()) {
			SootMethod m = queue.removeFirst();
			if (m.getSignature() == sourceMethod.getSignature()) {
				break;
			}
			if (UIActionsSet.contains(m.getName())) {
				triggerMethods.add(m);
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
		return triggerMethods;		
	}
	
	public SootMethod findUITriggerMethodFromSource(CallGraph cgraph, SootMethod method) {
		/*
		 * Given a source method, tracks the call graph to find any triggering methods that use UI actions in Android framework like "onClick()"
		 * @param cgraph CallGraph object of the Android apk
		 * @param method SootMethod object of the source method
		 * @return triggerMethod SootMethod object of the method that triggers
		 */
		//System.out.println("findTrigger called for method: " + method.getName());
		LinkedList<SootMethod> queue = new LinkedList<SootMethod>();
		queue.add(method);
		
		HashSet<String> UIActionsSet = this.staticValueService.getUIEventActionsSet();
		Set<String> visitedNodes = new HashSet<String>();
		
		while(!queue.isEmpty()) {
			SootMethod m = queue.removeFirst();
			//System.out.println("[NUTEXT] Found UI trigger of " + m.getName() + " while looking at method: " + method.getName());
			if (UIActionsSet.contains(m.getName())) {
				return m;
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
		return null;
	}

}
