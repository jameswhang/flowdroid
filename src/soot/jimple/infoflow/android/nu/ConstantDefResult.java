package soot.jimple.infoflow.android.nu;
import soot.Value;


public class ConstantDefResult {
	public Value id;
	public boolean isConstant;
	
	public ConstantDefResult(Value id, boolean isConstant) {
		this.id = id;
		this.isConstant = isConstant;
	}
}
